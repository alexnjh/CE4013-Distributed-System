package booking

import(
  "fmt"
  "errors"
)

// The Day of a week
type Day int

// Day Enum implementation in go
const(
    Monday Day      = iota
    Tuesday
    Wednesday
    Thursday
    Friday
    Saturday
    Sunday
)

// A range of dates
type DateRange struct {
    Start     Date
    End       Date
}

// Date implementaion of a booking
type Date struct {
    Day     Day
    Hour    int
    Minute  int
}

// The minimum date possible for a given Day
func MinDate(day Day) Date{
  return Date{
    Day: day,
    Hour: 0,
    Minute: 0,
  }
}

// The maximum date possible for a given Day
func MaxDate(day Day) Date{
  return Date{
    Day: day,
    Hour: 24,
    Minute: 00,
  }
}

// Convert minutes to a Date object
func MinutesToDate(d Day,i int) Date{
  hour := i/60
  minute := i % 60

  return Date{
    Day: d,
    Hour: hour,
    Minute: minute,
  }
}

// Convert byte array to a Date object (Un-marshalling)
func NewDateFromByteArray(str []byte) (*Date,error) {

  d:= Day(str[0])
  h:=int(str[1])
  m:=int(str[2])

  if d < 0 || d > 7 {
    return nil,errors.New("Invalid day given")
  }

  if h < 0 || h > 24 {
    return nil,errors.New("Invalid date given")
  }

  if m < 0 || m > 59 {
    return nil,errors.New("Invalid date given")
  }

  return &Date{
    Day: Day(str[0]),
    Hour: int(str[1]),
    Minute: int(str[2]),
  },nil
}

// Print the date object
func (d *Date) String() string{
  // Always 7 bytes
  return fmt.Sprintf("%d,%.2d:%.2d",d.Day,d.Hour,d.Minute)
}

// Convert date object to bytes (Marshalling)
func (d *Date) ToBytes() []byte{
  // Always 3 bytes
  return []byte{byte(d.Day),byte(d.Hour),byte(d.Minute)}
}

// Check if two dates are equal
func (d *Date) Equal(v Date) bool{
  return (d.Day == v.Day && d.Hour == v.Hour && d.Minute == v.Minute)
}

// Check if date is lesser then given date
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

// Increase the hours/minutes of a date
func (d *Date) Plus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  hour := d.Hour + v.Hour
  minute := d.Minute + v.Minute

  if minute >= 60 {
    hour +=  minute / 60
    minute = minute % 60
  }

  if hour > 24 || (hour == 24 && minute != 0){
    return nil,errors.New("Date offset overflows to next day")
  }

  return &Date{
    Day: d.Day,
    Hour: hour,
    Minute: minute,
  },nil

}

// Decrease the hours/minutes of a date
func (d *Date) Minus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  hour := d.Hour
  min := d.Minute

  if hour > v.Hour {
    hour -= v.Hour
  }else if hour < v.Hour {
    return nil,errors.New("Hour lesser than input hour")
  }else{
    if min < v.Minute {
      return nil,errors.New("Minute lesser than input minute")
    }else{
      hour -= v.Hour
    }
  }

  if min < v.Minute {
    hour-=1
    min+=60
  }

  min -= v.Minute

  if hour < 0 {
    return nil,errors.New("Date offset overflows to previous day")
  }

  return &Date{
    Day: d.Day,
    Hour: hour,
    Minute: min,
  },nil

}
