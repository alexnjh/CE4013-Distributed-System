package main

import (
	"encoding/hex"
	"fmt"
	"net"
  "flag"
	"server/availability"
	"server/booking"
	"server/facility"
	message "server/message"
	"server/messagesocket"
)

var (
	actualRun = true

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

func main() {

	bm := booking.NewGenericBookingManager()

  hostPtr := flag.String("h", "127.0.0.1", "Hostname")
  portPtr := flag.Int("p", 2222, "Host port")
  flag.Parse()

  hostname = *hostPtr
	hostport = *portPtr

	if actualRun {
		//Uncomment this if receiving messages from client
		startRun(bm)
	} else {
		fmt.Println("Running tests")

		x := message.NewErrorMessage("This is a error!!")
		fmt.Printf("%x\n", x.Marshal())

		testRun(bm)
	}
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

// Testing purposes
func testRun(bm *booking.BookingManager) {
	println("[INFO] System Start")
	PrintListOfAvailableDates(listofDayNames, listOfFac, bm)

	obj, err := bm.AddBooking("Alex", booking.Date{booking.Monday, 5, 0}, booking.Date{booking.Monday, 12, 0}, listOfFac[1])

	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return
	}

	println("[INFO] System After Add")
	PrintListOfAvailableDates(listofDayNames, listOfFac, bm)

	// negative numbers means move forward while positive numbers indicate postpone
	err = bm.UpdateBooking(obj.ConfirmationID, 180)

	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return
	}

	println("[INFO] System After Update")
	PrintListOfAvailableDates(listofDayNames, listOfFac, bm)

	// negative numbers means move forward while positive numbers indicate postpone
	err = bm.UpdateBookingDuration(obj.ConfirmationID, 180)

	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return
	}

	println("[INFO] System After Update Duration")
	PrintListOfAvailableDates(listofDayNames, listOfFac, bm)

	// Delete booking
	err = bm.RemoveBooking(obj.ConfirmationID)

	if err != nil {
		fmt.Printf("%s\n", err.Error())
		return
	}

	println("[INFO] System After Remove")
	PrintListOfAvailableDates(listofDayNames, listOfFac, bm)

	// Expect error handling
	err = bm.UpdateBooking(obj.ConfirmationID+"err", 180) // Error confirmation ID
	if err == nil {
		panic("We have an error checking error for confirmation ID")
	}
	fmt.Println(err.Error())
	err = bm.UpdateBooking(obj.ConfirmationID, 18000000000) // Error different day
	if err == nil {
		panic("Error checking different day")
	}
	fmt.Println(err.Error())
	err = bm.UpdateBooking(obj.ConfirmationID, -18000000000) // Error different day
	if err == nil {
		panic("Error checking different day")
	}
	fmt.Println(err.Error())
	err = bm.UpdateBookingDuration(obj.ConfirmationID, -600) // Error different day
	if err == nil {
		panic("Error checking different day")
	}
	fmt.Println(err.Error())

	fmt.Println("Validate Monitor state")
	mm := booking.GetManager()
	mm.PrintMonitoring()
	addr, _ := net.ResolveUDPAddr("udp", "localhost") // Will probably crash here
	mm.AddIP(messagesocket.Message{Addr: addr}, 2000, listOfFac[1])
	mm.PrintMonitoring()
	mm.AddIP(messagesocket.Message{Addr: addr}, 2000, listOfFac[0])
	mm.PrintMonitoring()
	mm.AddIP(messagesocket.Message{Addr: addr}, 3000, listOfFac[1])
	mm.PrintMonitoring()

	fmt.Println()
	fmt.Println("Test Marshalling of Availability")
	avail := availability.New("2_2,3-3,3")
	fmt.Printf("Original: %+v\n", avail)
	fmt.Printf("Marshalled: %x\n", avail.Marshal())
	unmarshAvail, err := availability.Unmarshal(avail.Marshal()[19:])
	if err != nil {
		panic(err)
	}
	fmt.Printf("Unmarshalled: %+v\n", unmarshAvail)

	fmt.Println()
	fmt.Println("Test Marshalling of Availabilities")
	availArr := availability.NewArray("2_2,3-3,3|3_2,3-4,3|1_3,3-4,3|4_2,3-4,3")
	marshAvailArr := availability.ConvertArrayToBytes(availArr)
	fmt.Printf("Original: %+v\n", availArr)
	fmt.Printf("Marshalled: %x\n", marshAvailArr)
	unmarshAvailArr, err := availability.ConvertBytesToArray(marshAvailArr[17:])
	if err != nil {
		panic(err)
	}
	fmt.Printf("Unmarshalled: %+v\n", unmarshAvailArr)

	fmt.Println()
	fmt.Println("Test Marshalling of Booking")
	tb, err := bm.AddBooking("Alex", booking.Date{Day: booking.Thursday, Hour: 5}, booking.Date{Day: booking.Monday, Hour: 12, Minute: 20}, listOfFac[1])
	marshedTb := tb.Marshal()
	fmt.Printf("Original: %+v\n", tb)
	fmt.Printf("Marshalled (w Header): %x\n", marshedTb)
	fmt.Printf("Marshalled (w/o Header): %x\n", marshedTb[15:])
	unmarshedTb, err := booking.Unmarshal(marshedTb[15:])
	fmt.Printf("Unmarshalled: %+v\n", &unmarshedTb)
	// 19
}

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

func DoesFacilityExist(list []facility.Facility, fac facility.Facility) bool {
	for _, v := range list {
		if v == fac {
			return true
		}
	}

	return false
}
