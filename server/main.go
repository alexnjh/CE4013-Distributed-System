package main

import (
  "fmt"
  "server/facility"
  "server/booking"
)

func main(){

  listofDayNames := []string{
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY",
  }

  listOfFac := []facility.Facility{"Meeting Room A", "Meeting Room B", "Meeting Room C"}

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
