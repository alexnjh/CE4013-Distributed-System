package availability

import (
	"fmt"
	"server/facility"
	"server/messagesocket"
	"strconv"
	"strings"
)

// Array split by |

type Availability struct {
	Day uint8
	StartHour uint8
	EndHour uint8
	StartMin uint8
	EndMin uint8
}

// Facility Length (1 byte), Facility String (x bytes), CSV of Days
type AvailabilityRequest struct {
	Facility facility.Facility
	Days string
}

func (a *Availability) ToString() string{
	return fmt.Sprintf("%d_%d,%d-%d,%d", a.Day, a.StartHour, a.StartMin, a.EndHour, a.EndMin)
}

func (a *Availability) Marshal() []byte{

	payload := []byte(a.ToString()) // 9 bytes
	hdr := messagesocket.CreateQueryFacilityAvailabilityHeader(uint16(len(payload)))

	return append(hdr,payload...)
}

func (a *Availability) GetDay() int {
	return int(a.Day)
}

func (a *Availability) GetStartHour() int {
	return int(a.StartHour)
}

func (a *Availability) GetEndHour() int {
	return int(a.EndHour)
}

func (a *Availability) GetStartMin() int {
	return int(a.StartMin)
}

func (a *Availability) GetEndMin() int {
	return int(a.EndMin)
}

func New(s string) Availability {
	s1 := strings.Split(s, "_")
	s2 := strings.Split(s1[1], "-")
	s3 := strings.Split(s2[0], ",")
	s4 := strings.Split(s2[1], ",")
	day, _:= strconv.Atoi(s1[0])
	sh, _ := strconv.Atoi(s3[0])
	sm, _ := strconv.Atoi(s3[1])
	eh, _ := strconv.Atoi(s4[0])
	em, _ := strconv.Atoi(s4[1])

	return Availability{
		Day: uint8(day),
		StartHour: uint8(sh),
		StartMin: uint8(sm),
		EndHour: uint8(eh),
		EndMin: uint8(em),
	}
}

func NewArray(s string) []Availability {
	arr := make([]Availability, 0)
	availSplit := strings.Split(s, "|")

	for _,ss := range availSplit {
		s1 := strings.Split(ss, "_")
		s2 := strings.Split(s1[1], "-")
		s3 := strings.Split(s2[0], ",")
		s4 := strings.Split(s2[1], ",")
		day, _:= strconv.Atoi(s1[0])
		sh, _ := strconv.Atoi(s3[0])
		sm, _ := strconv.Atoi(s3[1])
		eh, _ := strconv.Atoi(s4[0])
		em, _ := strconv.Atoi(s4[1])
		arr = append(arr, Availability{
			Day: uint8(day),
			StartHour: uint8(sh),
			StartMin: uint8(sm),
			EndHour: uint8(eh),
			EndMin: uint8(em),
		})
	}

	return arr
}

func ConvertArrayToString(avail []Availability) string {
	s := ""
	for _, a := range avail {
		s = s + a.ToString() + "|"
	}
	s = strings.TrimSuffix(s, "|")
	return s
}

func ConvertArrayToBytes(avail []Availability) []byte {
	str := ConvertArrayToString(avail)

	payload := []byte(str) // x*10 + 9 bytes, where x is array size - 1
	hdr := messagesocket.CreateQueryFacilityAvailabilityHeader(uint16(len(payload)))
	return append(hdr,payload...)
}

// No header. Should be unmarshalled by then
func ConvertBytesToArray(data []byte) ([]Availability, error) {
	var arr []Availability

	// First 2 bytes are length of array
	d := data[0:2]
	dataLen :=  uint16(d[1]) | uint16(d[0])<<8

	msg := data[2:]

	if dataLen > 9 {
		// Array. Each is 10 bytes, with last data as 9 bytes
		arrSize := int((dataLen / 10) + 1)
		arr = make([]Availability, arrSize)
		for i := 0; i < arrSize - 1; i++ {
			curRange := i*10
			d, err := Unmarshal(msg[curRange:curRange+9])
			if err != nil {
				return nil, err
			}
			arr[i] = d
		}

		// Handle last 9 bytes
		d, err := Unmarshal(msg[len(msg)-9:])
		if err != nil {
			return nil, err
		}
		arr[arrSize-1] = d
	} else {
		arr = make([]Availability, 1)
		d, err := Unmarshal(msg)
		if err != nil {
			return nil, err
		}
		arr[0] = d
	}
	return arr, nil
}

func Unmarshal(data []byte) (Availability, error) {
	return New(string(data)), nil
}
