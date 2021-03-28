package main

import (
	"encoding/hex"
	"fmt"
  "flag"
	"server/booking"
	"server/facility"
	message "server/message"
	"server/messagesocket"
)

var (

  hostname = "127.0.0.1"
  hostport = 2222

	listofDayNames = []string{
		"MONDAY",
		"TUESDAY",
		"WEDNESDAY",
		"THURSDAY",
		"FRIDAY",
		"SATURDAY",
		"SUNDAY",
	}

	listOfFac = []facility.Facility{
		"Meeting Room A",
		"Meeting Room B",
		"Meeting Room C",
	}

	list []messagesocket.Message
)

/*

Main routine

1. Get command line arguments which includes the hostname and port number the application will be listening to.
2. Create the connection handler object and start receiving messages.
3. Process messages based on the type of message received.

*/
func main() {

	bm := booking.NewGenericBookingManager()

  hostPtr := flag.String("h", "127.0.0.1", "Hostname")
  portPtr := flag.Int("p", 2222, "Host port")
  flag.Parse()

  hostname = *hostPtr
	hostport = *portPtr

  // Switch between test run and actual run
	startRun(bm)

}

// Actual program loop
func startRun(bm *booking.BookingManager) {
	fmt.Printf("Listening on %s Port %d\n", hostname, hostport)
	msgCh := messagesocket.NewMessageSocket(hostname, hostport)

	// Loop through channel to wait for messages
	for msg := range msgCh {

		fmt.Printf("%s\n", msg.Type)
		fmt.Printf("%s\n", hex.EncodeToString(msg.Data)) // Print message data

		switch msg.Type {
    // Invoke view booking service
		case "ViewBooking":
			bk, err := message.UnmarshalViewBookingMsg(msg.Data)

			if err != nil {
				fmt.Printf("%s\n", err.Error())
				errMsg := message.NewErrorMessage(err.Error())
				msg.Reply(errMsg.Marshal())
			} else {
				obj, err := bm.GetBooking(bk.ConfirmationID)

				if err != nil {
					fmt.Printf("%s\n", err.Error())
					errMsg := message.NewErrorMessage(err.Error())
					msg.Reply(errMsg.Marshal())
				} else {
					msg.Reply(obj.Marshal())
				}
			}
    // Invoke add booking service
		case "AddBooking":
			bk, err := booking.Unmarshal(msg.Data)

			if err != nil {
				fmt.Printf("%s\n", err.Error())
				errMsg := message.NewErrorMessage(err.Error())
				msg.Reply(errMsg.Marshal())
			} else {
				// Invalid Facility
				if !DoesFacilityExist(listOfFac, bk.Fac) {
					errMsg := message.NewErrorMessage(fmt.Sprintf("Facility %s not found!", bk.Fac))
					msg.Reply(errMsg.Marshal())
				} else {
					obj, err := bm.AddBooking(bk.BookerName, bk.Start, bk.End, bk.Fac)

					if err != nil {
						fmt.Printf("%s\n", err.Error())
						errMsg := message.NewErrorMessage(err.Error())
						msg.Reply(errMsg.Marshal())
						fmt.Printf("%s\n", hex.EncodeToString(errMsg.Marshal())) // Print message data
					} else {
						fmt.Printf("%s\n", obj.ConfirmationID)
						repMsg := message.NewConfirmMessage(obj.ConfirmationID)
						msg.Reply(repMsg.Marshal())
						fmt.Printf("%s\n", hex.EncodeToString(repMsg.Marshal())) // Print message data
					}
				}
			}
    // Invoke query availability service
		case "QueryAvailability":
			bk, err := message.UnmarshalQueryAvailabilityMsg(msg.Data)

			if err != nil {
				fmt.Printf("%s\n", err.Error())
				errMsg := message.NewErrorMessage(err.Error())
				msg.Reply(errMsg.Marshal())
			} else {

				if !DoesFacilityExist(listOfFac, bk.Fac) {
					errMsg := message.NewErrorMessage(fmt.Sprintf("Facility %s not found!", bk.Fac))
					msg.Reply(errMsg.Marshal())
				} else {

					fmt.Printf("%v", bk.Days)

					obj := bm.GetAvailableDates(bk.Fac, bk.Days...)
					byteArr, err := message.MarshalQueryAvailabilityMsg(obj, bk.Days)

					if err != nil {
						fmt.Printf("%s\n", err.Error())
						errMsg := message.NewErrorMessage(err.Error())
						msg.Reply(errMsg.Marshal())
					} else {
						msg.Reply(byteArr)
					}
				}

			}
    // Invoke delete booking service
    case "DeleteBooking":
      bk, err := message.UnmarshalDelBookingMsg(msg.Data)

      if err != nil {
        fmt.Printf("%s\n", err.Error())
        errMsg := message.NewErrorMessage(err.Error())
        msg.Reply(errMsg.Marshal())
      } else {
        err := bm.RemoveBooking(bk.ConfirmationID)
        if err != nil {
          // Invalid booking
          errMsg := message.NewErrorMessage(err.Error())
          msg.Reply(errMsg.Marshal())
        } else {
          // If successful send back confirmation ID
          repMsg := message.NewConfirmMessage(bk.ConfirmationID)
          msg.Reply(repMsg.Marshal())
          fmt.Printf("%s\n", hex.EncodeToString(repMsg.Marshal())) // Print message data
        }
      }
    // Invoke monitoring service
		case "StartMonitor":
			bk, err := message.UnmarshalStartMonitoringMsg(msg.Data)

			if err != nil {
				fmt.Printf("%s\n", err.Error())
				errMsg := message.NewErrorMessage(err.Error())
				msg.Reply(errMsg.Marshal())
			} else {
				mm := booking.GetManager()
				mm.AddIP(msg, bk.Duration, facility.Facility(bk.FacilityName))
				// Send initial query availability
				days := []booking.Day{booking.Monday, booking.Tuesday, booking.Wednesday, booking.Thursday, booking.Friday, booking.Saturday, booking.Sunday}
				obj := bm.GetAvailableDates(facility.Facility(bk.FacilityName), days...)
				byteArr, err := message.MarshalQueryAvailabilityMsg(obj, days)

				if err != nil {
					fmt.Printf("%s\n", err.Error())
					errMsg := message.NewErrorMessage(err.Error())
					msg.Reply(errMsg.Marshal())
				} else {
					msg.Reply(byteArr)
				}
			}
    // Invoke update booking service
		case "UpdateBooking":
          bk, err := message.UnmarshalUpdateBookingMsg(msg.Data)

          if err != nil {
            fmt.Printf("%s\n", err.Error())
            errMsg := message.NewErrorMessage(err.Error())
            msg.Reply(errMsg.Marshal())
          } else {
            err := bm.UpdateBooking(bk.ConfirmationID,bk.Offset)
            if err != nil {
              // Invalid booking
              errMsg := message.NewErrorMessage(err.Error())
              msg.Reply(errMsg.Marshal())
            } else {
              // If successful send back confirmation ID
              repMsg := message.NewConfirmMessage(bk.ConfirmationID)
              msg.Reply(repMsg.Marshal())
              fmt.Printf("%s\n", hex.EncodeToString(repMsg.Marshal())) // Print message data
            }
          }
    // Invoke update booking duration service
		case "UpdateDuration":
          bk, err := message.UnmarshalUpdateDurationMsg(msg.Data)

          if err != nil {
            fmt.Printf("%s\n", err.Error())
            errMsg := message.NewErrorMessage(err.Error())
            msg.Reply(errMsg.Marshal())
          } else {
			fmt.Printf("%v\n",bk.Offset)
			fmt.Printf("%v\n",len(bk.ConfirmationID))

            err := bm.UpdateBookingDuration(bk.ConfirmationID,bk.Offset)
            if err != nil {
              // Invalid booking
              errMsg := message.NewErrorMessage(err.Error())
              msg.Reply(errMsg.Marshal())
            } else {
              // If successful send back confirmation ID
              repMsg := message.NewConfirmMessage(bk.ConfirmationID)
              msg.Reply(repMsg.Marshal())
              fmt.Printf("%s\n", hex.EncodeToString(repMsg.Marshal())) // Print message data
            }
          }
    // Invoke acknowledgement service (Send by the client to acknowledge successful received monitoring updates)
		case "AckMon":
			fmt.Println("Received acknowledgement")
			mm := booking.GetManager()
			mm.MarkSubmit(msg)

		default:
			fmt.Println("Unimplemented")
			errMsg := message.NewErrorMessage("Unimplemented Function (" + msg.Type + ")")
			msg.Reply(errMsg.Marshal())
		}
	}
}

// Print a list of avaiable dates (Used mainly for testing)
func PrintListOfAvailableDates(listofDayNames []string, listOfFac []facility.Facility, bm *booking.BookingManager) {
	for _, f := range listOfFac {
		list := bm.GetAvailableDates(f,
			booking.Monday,
			booking.Tuesday,
			booking.Wednesday,
			booking.Thursday,
			booking.Friday,
			booking.Saturday,
			booking.Sunday,
		)
		fmt.Printf("Facility Name: %s\n-------------------------\n", f)
		for _, a := range list {
			if len(a) != 0 {
				fmt.Printf("[%s]\n", listofDayNames[a[0].Start.Day])
			}
			for _, v := range a {
				fmt.Printf("%s => %s\n", v.Start.String(), v.End.String())
			}
		}
		fmt.Printf("\n")
	}
}

// Check if a facility exist
func DoesFacilityExist(list []facility.Facility, fac facility.Facility) bool {
	for _, v := range list {
		if v == fac {
			return true
		}
	}

	return false
}
