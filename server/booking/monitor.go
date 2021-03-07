package booking

import (
	"fmt"
	"net"
	"server/availability"
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

func (mgmt *MonitoringManager) Broadcast(facility facility.Facility, delType string, bm *BookingManager) {
	mgmt.CheckExpiry() // Remove expired listeners

	blastMsg := fmt.Sprintf("Booking %s for %s", delType, facility)
	fmt.Println(blastMsg)

	facAvail := GetFacilityAvailability(facility, bm)

	toBc := mgmt.GetClientsToBroadcast(facility)
	messageList := make([]messagesocket.Message, len(toBc))
	for i,bc := range toBc {
		messageList[i] = bc.Message
	}

	// Send availability of facility
	marshalled := availability.ConvertArrayToBytes(facAvail)

	// TODO: Do we need to send blast message

	mgmt.BroadcastUsingMsgList(marshalled, messageList)
}

func GetFacilityAvailability(facility facility.Facility, bm *BookingManager) []availability.Availability {
	dates := bm.GetAvailableDates(facility, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
	availArr := make([]availability.Availability, 0)

	for _, a := range dates {
		for _, v := range a {
			availStr := fmt.Sprintf("%d_%d,%d-%d,%d", v.Start.Day, v.Start.Hour, v.Start.Minute, v.End.Hour, v.End.Minute)
			avail := availability.New(availStr)

			availArr = append(availArr, avail)
		}
	}
	return availArr
}

func (mgmt *MonitoringManager) BroadcastUsingMsgList(data []byte, list []messagesocket.Message){
	// TODO: NOOP for now while testing
	return
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

