package booking

import (
	"fmt"
	"server/facility"
	"time"
)

type MonitoringManager struct {
	MonitorList []*Monitor
}

type Monitor struct {
	Address  IpAddress
	Start    time.Time
	End      time.Time
	Interval time.Duration
	Facility facility.Facility
}

type IpAddress struct {
	IP string
	Port int
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

func (mgmt *MonitoringManager) AddIP(ipAddress IpAddress, duration int64, facility facility.Facility) {
	// Check that IP Exists and remove if so
	mgmt.RemoveIPIfExists(ipAddress, facility)

	start := time.Now()
	dura := time.Duration(duration) * time.Second
	end := start.Add(dura)

	mgmt.MonitorList = append(mgmt.MonitorList, &Monitor{
		Address: ipAddress,
		Start: start,
		End: end,
		Interval: dura,
		Facility: facility,
	})
}

func (mgmt *MonitoringManager) PrintMonitoring() {
	if len(mgmt.MonitorList) <= 0 {
		fmt.Println("No IP monitoring facility")
		return
	}
	fmt.Println("==========================================")
	for _,v := range mgmt.MonitorList {
		fmt.Printf("%s:%d - %s - %s to %s\n", v.Address.IP, v.Address.Port, v.Facility, v.Start.Format("02/01/2006 15:04:05"), v.End.Format("02/01/2006 15:04:05"))
	}
	fmt.Println("==========================================")
}

func (mgmt *MonitoringManager) Broadcast(facility facility.Facility, delType string, bm *BookingManager) {
	mgmt.CheckExpiry() // Remove expired listeners

	blastMsg := fmt.Sprintf("Booking %s for %s", delType, facility)
	fmt.Println(blastMsg)

	dates := bm.GetAvailableDates(facility, Monday,
		Tuesday,
		Wednesday,
		Thursday,
		Friday,
		Saturday,
		Sunday)

	// TODO: Broadcast facility update to user through callbacks
	_ = dates // We will be broadcasting this to the user eventually
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

func (mgmt *MonitoringManager) RemoveIPIfExists(address IpAddress, fac facility.Facility) {
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

func (mgmt *MonitoringManager) CheckIPExistIndex(address IpAddress, fac facility.Facility) int {
	exist := -1

	for i,v := range mgmt.MonitorList {
		if v.Address == address && v.Facility == fac {
			exist = i
			break
		}
	}
	return exist
}