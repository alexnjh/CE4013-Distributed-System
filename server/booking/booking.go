 package booking

import(
  "fmt"
  "time"
  "errors"
  "crypto/sha1"
  "server/facility"
)

type Day int

const(
    Monday Day      = 0
    Tuesday Day     = 1
    Wednesday Day   = 2
    Thursday Day    = 3
    Friday Day      = 4
    Saturday Day    = 5
    Sunday Day      = 6
)

type DateRange struct {
    Start     Date
    End       Date
}

type Date struct {
    Day     Day
    Hour    int
    Minute  int
}

func (d *Date) String() string{
  return fmt.Sprintf("%d/%d/%d",d.Day,d.Hour,d.Minute);
}

func (d *Date) Equal(v Date) bool{
  return (d.Day == v.Day && d.Hour == v.Hour && d.Minute == v.Minute)
}

func (d *Date) LessThan(v Date) bool{

  if d.Hour < v.Hour {
    return true
  }else if d.Hour == v.Hour {
    if d.Minute < v.Minute {
        return true
    }else{
      return false
    }
  }else{
    return false
  }

}

func (d *Date) Minus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  if d.Hour > v.Hour {

    if d.Minute < v.Minute {
      hour := d.Hour
      min := d.Minute

      if min < v.Minute {
        hour-=1
        min+=60
      }

      return &Date{
        Day: d.Day,
        Hour: hour,
        Minute: min,
      },nil

    }

  }

  return nil,errors.New("Hour lesser than input hour")

}

type Booking struct{
  BookerName string
  ConfirmationID string
  Start Date
  End Date
  Fac facility.Facility
}

func NewBooking(name string, start Date, end Date, f facility.Facility) Booking{

  // Confirmation ID is a SHA1 hash in the form of BookerName@Current Time
  h := sha1.New()
  h.Write([]byte(name+"@"+time.Now().String()))
  bs := h.Sum(nil)
  cid := fmt.Sprintf("%x",bs)

  return Booking{
    BookerName: name,
    ConfirmationID: cid,
    Start: start,
    End: end,
    Fac: f,
  }
}
