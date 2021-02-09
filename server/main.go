package main

import (
  "fmt"
  "server/availability"
  "server/booking"
  "server/facility"
  errorMsg "server/errors"

  // Uncomment this if receiving messages from client
  // "server/messagesocket"
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

)

func main(){

  x := errorMsg.New("This is a error!!")
  fmt.Printf("%x\n",x.Marshal())

  // Uncomment this if receiving messages from client
  // msgCh := messagesocket.NewMessageSocket(hostname,hostport)
  //
  // // Loop through channel to wait for messages
  // for msg := range msgCh {
  //   if msg.Type == "Error" {
  //     fmt.Println(string(msg.Data))
  //   }
  // }

  bm := booking.NewGenericBookingManager()

  println("[INFO] System Start")
  PrintListOfAvailableDates(listofDayNames,listOfFac,bm)

  obj, err := bm.AddBooking("Alex",booking.Date{booking.Monday,5,0},booking.Date{booking.Monday,12,20},listOfFac[1])

  if err != nil {
    fmt.Printf("%s\n",err.Error())
    return
  }

  println("[INFO] System After Add")
  PrintListOfAvailableDates(listofDayNames,listOfFac,bm)

  // negative numbers means move forward while positive numbers indicate postpone
  err = bm.UpdateBooking(obj.ConfirmationID, 180)

  if err != nil {
    fmt.Printf("%s\n",err.Error())
    return
  }

  println("[INFO] System After Update")
  PrintListOfAvailableDates(listofDayNames,listOfFac,bm)

  // Expect error handling
  err = bm.UpdateBooking(obj.ConfirmationID + "err", 180) // Error confirmation ID
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

  fmt.Println("Validate Monitor state")
  mm := booking.GetManager()
  mm.PrintMonitoring()
  mm.AddIP(booking.IpAddress{IP: "127.0.0.1", Port: 65535}, 2000, listOfFac[1])
  mm.PrintMonitoring()
  mm.AddIP(booking.IpAddress{IP: "127.0.0.1", Port: 65536}, 2000, listOfFac[0])
  mm.PrintMonitoring()
  mm.AddIP(booking.IpAddress{IP: "127.0.0.1", Port: 65535}, 3000, listOfFac[1])
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

}

func PrintListOfAvailableDates(listofDayNames []string, listOfFac []facility.Facility, bm *booking.BookingManager){
  for _, f := range(listOfFac){
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
    for _, a := range(list){
      if len(a) != 0{
        fmt.Printf("[%s]\n", listofDayNames[a[0].Start.Day])
      }
      for _, v := range(a){
        fmt.Printf("%s => %s\n", v.Start.String(), v.End.String())
      }
    }
    fmt.Printf("\n")
  }
}

func DoesFacilityExist(list []facility.Facility, fac facility.Facility) bool{
  for _, v := range(list){
    if v == fac {
      return true
    }
  }

  return false
}
