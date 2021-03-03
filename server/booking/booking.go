 package booking

import(
  "encoding/hex"
  "fmt"
  "server/messagesocket"
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

// Test marshal of what client is sending
func (b *Booking) Marshal() []byte {
  // The way we marshal is first string length (1 byte), string, etc
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

// Unmarshal from client (receive booking info), send confirmation ID after booking
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
