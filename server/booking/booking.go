 package booking

import(
  "fmt"
  "time"
  "crypto/sha1"
  "server/facility"
)



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
