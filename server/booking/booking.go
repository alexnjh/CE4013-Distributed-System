 package booking

import(
  "encoding/hex"
  "fmt"
  "server/messagesocket"
  "time"
  "crypto/sha1"
  "server/facility"
)

// Implementation of a booking entry
type Booking struct{
  // The user that created this booking
  BookerName string
  // The ID of the booking entry
  ConfirmationID string
  // The start date of the booking
  Start Date
  // The end date of the booking
  End Date
  // The facility that the user booked
  Fac facility.Facility
}

// Marshal booking object into bytes
func (b *Booking) Marshal() []byte {

  bnLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(b.BookerName)))
  facLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(b.Fac)))

  payload := bnLen
  payload = append(payload, []byte(b.BookerName)...)
  payload = append(payload, []byte(b.Start.ToBytes())...) // 7 bytes
  payload = append(payload, []byte(b.End.ToBytes())...) // 7 bytes
  payload = append(payload, facLen...)
  payload = append(payload, []byte(b.Fac)...)
  payload = append(payload, []byte(b.ConfirmationID)...)
  hdr := messagesocket.CreateBookingDetailHeader(uint16(len(payload)))

  return append(hdr, payload...)
}

// Un-marshal from bytes to booking object
func Unmarshal(data []byte) (*Booking,error) {
  index := 0
  // Booker Name
  bnLen := int(data[index])
  bn := string(data[index+1:index+1+bnLen])
  index += 1+bnLen // Get next byte

  // Start Date
  sd := data[index:index+3]
  index += 3 // Get next byte

  // End Date
  ed := data[index:index+3]
  index += 3 // Get next byte

  // Facility
  facLen := int(data[index])
  fac := string(data[index+1:index+1+facLen])
  index += 1+bnLen+1 // Get next byte

  sdate, err := NewDateFromByteArray(sd)

  if err != nil {
    return nil,err
  }

  edate, err := NewDateFromByteArray(ed)

  if err != nil {
    return nil,err
  }

  return &Booking{
    BookerName: bn,
    Start: *sdate,
    End: *edate,
    Fac: facility.Facility(fac),
  },nil
}

// Create a new booking
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
