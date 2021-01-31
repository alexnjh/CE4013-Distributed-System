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

  bm.AddBooking("Alex",booking.Date{booking.Monday,0,0},booking.Date{booking.Monday,23,59},listOfFac[0])
  bm.AddBooking("Alex",booking.Date{booking.Monday,15,0},booking.Date{booking.Monday,22,59},listOfFac[1])
  bm.AddBooking("Alex",booking.Date{booking.Monday,2,0},booking.Date{booking.Monday,4,30},listOfFac[1])
  bm.AddBooking("Alex",booking.Date{booking.Sunday,10,0},booking.Date{booking.Sunday,23,59},listOfFac[0])
  bm.AddBooking("Alex",booking.Date{booking.Tuesday,15,0},booking.Date{booking.Tuesday,22,9},listOfFac[1])
  bm.AddBooking("Alex",booking.Date{booking.Monday,2,0},booking.Date{booking.Monday,6,20},listOfFac[1])

  for _, f := range(listOfFac){
    list := bm.GetAvailableDates(f,booking.Monday, booking.Sunday)
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
