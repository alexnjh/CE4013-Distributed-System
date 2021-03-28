package booking

import (
	"encoding/hex"
	"fmt"
	"net"
	"server/facility"
	"server/messagesocket"
	"time"
)

// Singleton to obtain the monitor object from any class
var globalMonitor *MonitoringManager

// Enum to differentiate what action has been made when the monitor message is sent
const (
	CreateBooking string = "created"
	UpdateBooking = "updated"
	DeleteBooking = "deleted"
	UpdateDurationBooking = "duration updated"
)


// MonitoringManager manages all the active monitoring sessions and is
// responsible for the creation and sending of monitoring updates to
// the clients
type MonitoringManager struct {
  // List of active monitoring sessions
	MonitorList []*Monitor
  // A map storing the last message send by the server to active monitoring clients
	LastMessageList map[string]*[]byte
  // True = start monitoring manager resend check, False = disable monitoring manager resend check
	Start bool
}

// Monitor is the implementaion of a monitoring session
type Monitor struct {
  // Store the client StartMonitoringMessage to identiy the client that initiate the monitoring session
	Message messagesocket.Message
  // The start time of the monitor session
	Start    time.Time
  // The end time of the monitor session
	End      time.Time
  // The total duration the monitor session remain active
	Interval time.Duration
  // The facility the monitoring session is monitoring
	Facility facility.Facility
}

// Initialize the global monitor manager (Used by the server to manage monitoring sessions)
func Init() {
	globalMonitor = &MonitoringManager{
		MonitorList: make([]*Monitor, 0),
		LastMessageList: make(map[string]*[]byte),
		Start: false,
	}
}

// Return the global monitor manager object
func GetManager() *MonitoringManager {
	if globalMonitor == nil {
		fmt.Println("Monitor not initialized. Initializing")
		Init() // Create monitor
	}
	return globalMonitor
}

// Mark that the client has received the monitor update message
// This will clear the message from the resend queue in the last message list
func (mgmt *MonitoringManager) MarkSubmit(msgObj messagesocket.Message) {
	println(mgmt.LastMessageList)
	// Set last message list to nil and remove it from the map
	mgmt.LastMessageList[msgObj.Addr.String()] = nil
	delete(mgmt.LastMessageList, msgObj.Addr.String())
	println(mgmt.LastMessageList)
	fmt.Printf("[MON] Marked %s as submitted\n", msgObj.Addr.String())
}

// Function to handle checking the last message list and resending them all if still unacknowledged
func (mgmt *MonitoringManager) Resend() {
	fmt.Println("[MON] Resending unacknowledged messages")
	// Iterate through all clients currently being monitored
	for _,v := range mgmt.MonitorList {
		// Check if client address/port combo matches that in the last message list
		// Last message list contains all messages that are yet to be acknowledged as received
		if value, ok := mgmt.LastMessageList[v.Message.Addr.String()]; ok {
			// Check that there is a message being kept in the list
			if mgmt.LastMessageList[v.Message.Addr.String()] != nil {
				fmt.Printf("Broadcasting update to: %s\n", v.Message.Addr.String())
				// Resend message to client
				v.Message.Reply(*value)
			}
		}
	}
}

// Multithreaded Goroutine to handle checking for unacknowledged messages
// Unacknowledged messages will be resent in a loop until all messages are acknowledged
func (mgmt *MonitoringManager) goStartCheckingResend() {
	// Defers setting resend check to set back to false
	// This check is used to ensure that only 1 go function is called at any time
	defer func() {mgmt.Start = false}()
	// Set resend check to true. This makes it such that we do not start anymore goroutine to do resend checks
	mgmt.Start = true
	for {
		// Wait 5 seconds
		time.Sleep(5 * time.Second)

		// Check if there are still messages in the list
		if len(mgmt.LastMessageList) <= 0 {
			// No messages left in list, stop and exit goroutine
			fmt.Println("Nothing to ack. bye")
			break
		}

		// Resend all unacknowledged messages
		mgmt.Resend()
	}
}

// Adds an IP address to the list of clients currently monitoring a facility
func (mgmt *MonitoringManager) AddIP(msgObj messagesocket.Message, duration int64, facility facility.Facility) {
	// Check that IP Combination current exists, replace if so
	mgmt.RemoveIPIfExists(msgObj.Addr, facility)

	// Get current time and end time for monitoring
	start := time.Now()
	dura := time.Duration(duration) * time.Second
	end := start.Add(dura)

	// Add to monitoring list
	mgmt.MonitorList = append(mgmt.MonitorList, &Monitor{
		Message: msgObj,
		Start: start,
		End: end,
		Interval: dura,
		Facility: facility,
	})
	fmt.Printf("Added %s to monitor list to monitor %s for %d seconds", msgObj.Addr.String(), facility, duration)
}

// Debug Helper function to print list of clients currently monitoring a facility
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

// Broadcast function that is used by the Booking manager to send a monitor broadcast to clients monitoring the specific facility
// This function is called after the add/modify/delete booking functions are executed successfully
func (mgmt *MonitoringManager) Broadcast(facility facility.Facility, delType string, bm *BookingManager, name string) {
	// Check if there are any clients currently on the monitoring list that has expired and should be removed. Remove if so
	mgmt.CheckExpiry() 

	blastMsg := fmt.Sprintf("Booking %s for %s by %s", delType, facility, name)
	fmt.Println(blastMsg)

	// Get facility availability dates and generate bytestream message from it to be broadcasted to the client
	days := []Day{Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}
	dates := bm.GetAvailableDates(facility, days...)
	byteArr, err := MarshalQueryAvailabilityMonitorMsg(dates, blastMsg, days)

	// Get list of all clients to broadcast this message to
	// toBc contains the list of clients that are currently monitoring this facility and have not expired
	toBc := mgmt.GetClientsToBroadcast(facility)
	messageList := make([]messagesocket.Message, len(toBc))
	for i,bc := range toBc {
		messageList[i] = bc.Message
	}

	var marshalled []byte
	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return // Don't send availability on error
	} else {
		marshalled = byteArr
	}

	// Broadcast the message to the clients
	mgmt.BroadcastUsingMsgList(marshalled, messageList)

	// Check if the unacknowledged messages handler is started
	// If it is not started, start it up
	if !mgmt.Start {
		fmt.Println("Starting resend loop")
		go mgmt.goStartCheckingResend()
	} else {
		fmt.Println("Existing routing ignoring")
	}
}

// Takes a list of clients, and the data to broadcast in bytes and sends it to each of the clients
func (mgmt *MonitoringManager) BroadcastUsingMsgList(data []byte, list []messagesocket.Message){
	for _, a := range list {
		mgmt.LastMessageList[a.Addr.String()] = &data
		fmt.Printf("Broadcasting update to: %s\n", a.Addr.String())
		a.Reply(data)
	}
}

// Obtain a list of clients that are currently listening to the specified facility from the monitoring list
func (mgmt *MonitoringManager) GetClientsToBroadcast(fac facility.Facility) []*Monitor {
	inform := make([]*Monitor, 0)

	// Iterate through the monitoring list to get the list of clients
	// If the client is currently monitoring the facility, add it to the final list that would be returned
	for _,v := range mgmt.MonitorList {
		if v.Facility == fac {
			inform = append(inform, v)
		}
	}

	return inform
}

// Check if the clients in the monitoring list have expired
// Remove automatically if so
func (mgmt *MonitoringManager) CheckExpiry() {
	// Get current time
	curTime := time.Now
	// Get new list to store all unexpired clients
	unexpired := make([]*Monitor, 0)

	// Iterate through current list of clients monitoring a facility
	for _,v := range mgmt.MonitorList {
		// If client's monitoring end time is after the current time, add to the new list
		if v.End.After(curTime()) {
			// Not expired
			unexpired = append(unexpired, v)
		}
	}

	// Overwrite the current list with the new list of monitoring clients
	mgmt.MonitorList = unexpired
}

// Helper function to remove the IP/Port combo if exists from the monitoring list
func (mgmt *MonitoringManager) RemoveIPIfExists(address net.Addr, fac facility.Facility) {
	// Check that IP exists. if Ind is -1, IP does not exist
	ind := mgmt.CheckIPExistIndex(address, fac)
	if ind > -1 {
		// IP exists, remove it from the list
		mgmt.RemoveIPWithIndex(ind)
	}
}

// Remove IP address from monitoring list based on index
func (mgmt *MonitoringManager) RemoveIPWithIndex(index int) {
	if index > -1 {
		// We do not need to care about order, so lets do a O(1) removal
		// We start by just swapping the last element of the list with the item specified in the index
		// Then we truncate the list by 1 to reduce the size
		mgmt.MonitorList[index] = mgmt.MonitorList[len(mgmt.MonitorList)-1]
		mgmt.MonitorList[len(mgmt.MonitorList)-1] = nil
		mgmt.MonitorList = mgmt.MonitorList[:len(mgmt.MonitorList)-1]
	}
}

// Obtain the index of the IP address in the monitoring list if it exists
func (mgmt *MonitoringManager) CheckIPExistIndex(address net.Addr, fac facility.Facility) int {
	exist := -1

	// Iterate through monitoring list of clients to find the IP combination
	for i,v := range mgmt.MonitorList {
		// If IP/Port matches and the facility being tracked also matches, returns the index of this item
		if v.Message.Addr.String() == address.String() && v.Facility == fac {
			exist = i
			break
		}
	}

	// Return -1 if not found
	return exist
}


// Marshals the query availability message that would be sent to the client
// This is identical to that of the query availbility message, however there are extra data that are used by monitoring that is appended to it
func MarshalQueryAvailabilityMonitorMsg(raw [][]DateRange, actionString string, dname []Day) ([]byte, error) {
	payload := make([]byte, 0)

	// We place the action string in front first (length of string 1 byte, string x byte)
	fmt.Println(len(actionString))
	// We obtain the length of the action string
	asLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(actionString)))
	// We append the action string length to the beginning of the data payload
	payload = append(payload, asLen...)
	// We append the action string itself after
	payload = append(payload, []byte(actionString)...)

	// This is similar to query availbility function where we simply append the availability of the facility for each day of the week
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

	// We create the header necessary for the data packet and appends it to the front of the data payload
	hdr := messagesocket.CreateMonitorAvailabilityHeader(uint16(len(payload)))
	return append(hdr, payload...),nil
}
