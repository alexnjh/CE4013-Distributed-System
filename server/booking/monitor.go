package booking

import (
	"encoding/hex"
	"fmt"
	"net"
	"server/facility"
	"server/messagesocket"
	"time"
)

type MonitoringManager struct {
	MonitorList []*Monitor
}

type Monitor struct {
	Message messagesocket.Message
	Start    time.Time
	End      time.Time
	Interval time.Duration
	Facility facility.Facility
}

const (
	CreateBooking string = "created"
	UpdateBooking = "updated"
	DeleteBooking = "deleted"
)

var globalMonitor *MonitoringManager

func Init() {
	globalMonitor = &MonitoringManager{
		MonitorList: make([]*Monitor, 0),
	}
}

func GetManager() *MonitoringManager {
	if globalMonitor == nil {
		fmt.Println("Monitor not initialized. Initializing")
		Init() // Create monitor
	}
	return globalMonitor
}

func (mgmt *MonitoringManager) AddIP(msgObj messagesocket.Message, duration int64, facility facility.Facility) {
	// Check that IP Exists and remove if so
	mgmt.RemoveIPIfExists(msgObj.Addr, facility)

	start := time.Now()
	dura := time.Duration(duration) * time.Second
	end := start.Add(dura)

	mgmt.MonitorList = append(mgmt.MonitorList, &Monitor{
		Message: msgObj,
		Start: start,
		End: end,
		Interval: dura,
		Facility: facility,
	})
	fmt.Printf("Added %s to monitor list to monitor %s for %d seconds", msgObj.Addr.String(), facility, duration)
}

func (mgmt *MonitoringManager) PrintMonitoring() {
	if len(mgmt.MonitorList) <= 0 {
		fmt.Println("No IP monitoring facility")
		return
	}
	fmt.Println("==========================================")
	for _,v := range mgmt.MonitorList {
		fmt.Printf("%s - %s - %s to %s\n", v.Message.Addr.String(), v.Facility, v.Start.Format("02/01/2006 15:04:05"), v.End.Format("02/01/2006 15:04:05"))
	}
	fmt.Println("==========================================")
}

func (mgmt *MonitoringManager) Broadcast(facility facility.Facility, delType string, bm *BookingManager, name string) {
	mgmt.CheckExpiry() // Remove expired listeners

	blastMsg := fmt.Sprintf("Booking %s for %s by %s", delType, facility, name)
	fmt.Println(blastMsg)

	days := []Day{Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}
	dates := bm.GetAvailableDates(facility, days...)
	byteArr, err := MarshalQueryAvailabilityMonitorMsg(dates, blastMsg, days)

	toBc := mgmt.GetClientsToBroadcast(facility)
	messageList := make([]messagesocket.Message, len(toBc))
	for i,bc := range toBc {
		messageList[i] = bc.Message
	}

	var marshalled []byte
	// Send availability of facility
	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return // Don't send availability on error
	} else {
		marshalled = byteArr
	}

	mgmt.BroadcastUsingMsgList(marshalled, messageList)
}

func (mgmt *MonitoringManager) BroadcastUsingMsgList(data []byte, list []messagesocket.Message){
	for _, a := range list {
		fmt.Printf("Broacasting update to: %s\n", a.Addr.String())
		a.Reply(data)
	}
}

func (mgmt *MonitoringManager) GetClientsToBroadcast(fac facility.Facility) []*Monitor {
	inform := make([]*Monitor, 0)

	for _,v := range mgmt.MonitorList {
		if v.Facility == fac {
			inform = append(inform, v)
		}
	}

	return inform
}

func (mgmt *MonitoringManager) CheckExpiry() {
	curTime := time.Now
	unexpired := make([]*Monitor, 0)

	for _,v := range mgmt.MonitorList {
		if v.End.After(curTime()) {
			// Not expired
			unexpired = append(unexpired, v)
		}
	}

	mgmt.MonitorList = unexpired
}

func (mgmt *MonitoringManager) RemoveIPIfExists(address net.Addr, fac facility.Facility) {
	ind := mgmt.CheckIPExistIndex(address, fac)
	if ind > -1 {
		mgmt.RemoveIPWithIndex(ind)
	}
}

func (mgmt *MonitoringManager) RemoveIPWithIndex(index int) {
	if index > -1 {
		// We do not need to care about order, so lets do a O(1) removal
		mgmt.MonitorList[index] = mgmt.MonitorList[len(mgmt.MonitorList)-1]
		mgmt.MonitorList[len(mgmt.MonitorList)-1] = nil
		mgmt.MonitorList = mgmt.MonitorList[:len(mgmt.MonitorList)-1]
	}
}

func (mgmt *MonitoringManager) CheckIPExistIndex(address net.Addr, fac facility.Facility) int {
	exist := -1

	for i,v := range mgmt.MonitorList {
		if v.Message.Addr.String() == address.String() && v.Facility == fac {
			exist = i
			break
		}
	}
	return exist
}

// This is simply query availabilty but for monitoring
func MarshalQueryAvailabilityMonitorMsg(raw [][]DateRange, actionString string, dname []Day) ([]byte, error) {
	payload := make([]byte, 0)

	// We place the action string in front first (length of string 1 byte, string x byte)
	fmt.Println(len(actionString))
	asLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(actionString)))
	payload = append(payload, asLen...)
	payload = append(payload, []byte(actionString)...)

	for idx, x := range raw {
		payload = append(payload, byte(dname[idx]))
		payload = append(payload, byte(len(x)))

		for _, v := range x {

			temp := make([]byte, 6)

			temp[0] = byte(v.Start.Day)
			temp[1] = byte(v.Start.Hour)
			temp[2] = byte(v.Start.Minute)
			temp[3] = byte(v.End.Day)
			temp[4] = byte(v.End.Hour)
			temp[5] = byte(v.End.Minute)

			payload = append(payload, temp...)
		}
	}



	hdr := messagesocket.CreateMonitorAvailabilityHeader(uint16(len(payload)))
	return append(hdr, payload...),nil
}
