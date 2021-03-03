package message

import(
  "server/facility"
  "server/booking"
  "server/messagesocket"
)

type QueryAvailabilityMessage struct {
  Fac facility.Facility
  Days []booking.Day
}

func MarshalQueryAvailabilityMsg(raw [][]booking.DateRange, dname []booking.Day) ([]byte,error){

  payload := make([]byte, 0)

  for idx, x := range raw {

      payload = append(payload, byte(dname[idx]))
      payload = append(payload, byte(len(x)))

      for _, v := range x {

        temp := make([]byte, 6)

        temp[0] = byte(v.Start.Day)
        temp[1] = byte(v.Start.Hour)
        temp[2] = byte(v.Start.Minute)
        temp[3] = byte(v.End.Day)
        temp[4] = byte(v.End.Hour)
        temp[5] = byte(v.End.Minute)

        payload = append(payload, temp...)
      }

  }

  hdr := messagesocket.CreateQueryFacilityAvailabilityHeader(uint16(len(payload)))
  return append(hdr, payload...),nil
}


func UnmarshalQueryAvailabilityMsg(data []byte) (QueryAvailabilityMessage,error){

  index := 0
  // Facility name length
  fcLen := int(data[index])

  // Facility name
  fc := string(data[index+1:index+1+fcLen])
  index += 1+fcLen // Get next byte

  d := make([]booking.Day, 0)

  if hasBit(int(data[index]), uint(booking.Monday)) {
    d = append(d, booking.Monday)
  }

  if hasBit(int(data[index]), uint(booking.Tuesday)) {
    d = append(d, booking.Tuesday)
  }

  if hasBit(int(data[index]), uint(booking.Wednesday)) {
    d = append(d, booking.Wednesday)
  }

  if hasBit(int(data[index]), uint(booking.Thursday)) {
    d = append(d, booking.Thursday)
  }

  if hasBit(int(data[index]), uint(booking.Friday)) {
    d = append(d, booking.Friday)
  }

  if hasBit(int(data[index]), uint(booking.Saturday)) {
    d = append(d, booking.Saturday)
  }

  if hasBit(int(data[index]), uint(booking.Sunday)) {
    d = append(d, booking.Sunday)
  }

  return QueryAvailabilityMessage{
    Fac: facility.Facility(fc),
    Days: d,
  },nil

}

// check if bit set
func hasBit(n int, pos uint) bool {
    val := n & (1 << pos)
    return (val > 0)
}
