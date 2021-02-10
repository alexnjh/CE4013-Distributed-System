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

func (b *Booking) Marshal() []byte {
  // The way we marshal is first string length (1 byte), string, etc
  bnLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(b.BookerName)))
  cfmLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(b.ConfirmationID)))
  facLen, _ := hex.DecodeString(fmt.Sprintf("%02x", len(b.Fac)))

  payload := bnLen
  payload = append(payload, []byte(b.BookerName)...)
  payload = append(payload, cfmLen...)
  payload = append(payload, []byte(b.ConfirmationID)...)
  payload = append(payload, []byte(b.Start.String())...) // 7 bytes
  payload = append(payload, []byte(b.End.String())...) // 7 bytes
  payload = append(payload, facLen...)
  payload = append(payload, []byte(b.Fac)...)
  hdr := messagesocket.CreateAddBookingHeader(uint16(len(payload)))

  return append(hdr, payload...)
}

// TODO: Remove confirmation ID
// TODO: Client send booking info (w/o cfm id), Server send back cfm id
func Unmarshal(data []byte) Booking {
  index := 0
  // Booker Name
  bnLen := int(data[index])
  bn := string(data[index+1:index+1+bnLen])
  index += 1+bnLen // Get next byte

  // Confirmation ID
  cfmLen := int(data[index])
  cfm := string(data[index+1:index+1+cfmLen])
  index += 1+cfmLen // Get next byte

  // Start Date
  sd := string(data[index:index+7])
  index += 7 // Get next byte

  // End Date
  ed := string(data[index:index+7])
  index += 7 // Get next byte

  // Facility
  facLen := int(data[index])
  fac := string(data[index+1:index+1+facLen])
  index += 1+bnLen+1 // Get next byte

  return Booking{
    BookerName: bn,
    ConfirmationID: cfm,
    Start: FromString(sd),
    End: FromString(ed),
    Fac: facility.Facility(fac),
  }
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
