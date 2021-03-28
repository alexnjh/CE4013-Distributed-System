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

  d:= Day(str[0]) // 1 byte represent Day
  h:=int(str[1]) // 1 byte represent Hour
  m:=int(str[2]) // 1 byte represent Minute

  // Check if Day is valid
  if d < 0 || d > 7 {
    return nil,errors.New("Invalid day given")
  }

  // Check if Hour is valid
  if h < 0 || h > 24 {
    return nil,errors.New("Invalid hour value given")
  }

  // Check if Minute is valid
  if m < 0 || m > 59 {
    return nil,errors.New("Invalid minute value given")
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

// Check if date object is lesser then given date
// Date object = d
// Given Date object = v
func (d *Date) LessThan(v Date) bool{

  // First check if the hour of d < v
  if d.Hour < v.Hour {
    return true
  }else if d.Hour == v.Hour {

    // If hour is equal check if minute value of d < v
    if d.Minute < v.Minute {
        return true
    }else{
      // If minute is the same return false
      return false
    }
  }else{
    // If hour value of d is more than v, return false
    return false
  }

}

// Increase the hours/minutes of a date
func (d *Date) Plus(v Date) (*Date,error){

  // Day should be the same cannot add a date from another day
  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  // Add hour value
  hour := d.Hour + v.Hour
  // Add minute value
  minute := d.Minute + v.Minute

  // If minutes > 60 convert to hour and minutes
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

// Subtract a date object from another date object (Day must be the same)
// Subtract date v from date d
func (d *Date) Minus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  hour := d.Hour
  min := d.Minute

  // Before subtracting make sure date d hour is more than v
  if hour > v.Hour {
    hour -= v.Hour
  }else if hour < v.Hour {
    return nil,errors.New("Hour lesser than input hour")
  }else{
    // If hour value is equal check if d.minute > v.minute
    if min < v.Minute {
      return nil,errors.New("Minute lesser than input minute")
    }else{
      hour -= v.Hour
    }
  }

  // Check if d.minute is less than v.minute.
  // If less than v.minute conver 1 hour to 60 minute
  // This is because we know that if the code reach this point
  // the d.hour must be >= v.hour
  if min < v.Minute {
    hour-=1
    min+=60
  }

  min -= v.Minute

  // If hour is < 0 we know that the date v cannot be subtracted from date d
  if hour < 0 {
    return nil,errors.New("Date offset overflows to previous day")
  }

  return &Date{
    Day: d.Day,
    Hour: hour,
    Minute: min,
  },nil

}
