package booking

import(
  "fmt"
  "errors"
)

type Day int

const(
    Monday Day      = iota
    Tuesday
    Wednesday
    Thursday
    Friday
    Saturday
    Sunday
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

func MinDate(day Day) Date{
  return Date{
    Day: day,
    Hour: 0,
    Minute: 0,
  }
}

func MaxDate(day Day) Date{
  return Date{
    Day: day,
    Hour: 24,
    Minute: 00,
  }
}

func MinutesToDate(d Day,i int) Date{
  hour := i/60
  minute := i % 60

  return Date{
    Day: d,
    Hour: hour,
    Minute: minute,
  }
}

func FromString(str []byte) Date {

  return Date{
    Day: Day(str[0]),
    Hour: int(str[1]),
    Minute: int(str[2]),
  }
}

func (d *Date) String() string{
  // Always 7 bytes
  return fmt.Sprintf("%d,%.2d:%.2d",d.Day,d.Hour,d.Minute)
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

func (d *Date) Plus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  hour := d.Hour + v.Hour
  minute := d.Minute + v.Minute

  if minute > 60 {
    hour +=  minute / 60
    minute = minute % 60
  }

  if hour > 24 {
    return nil,errors.New("Date offset overflows to next day")
  }

  return &Date{
    Day: d.Day,
    Hour: hour,
    Minute: minute,
  },nil

}

func (d *Date) Minus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  if d.Hour > v.Hour {

    hour := d.Hour
    hour -= v.Hour

    min := d.Minute

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

  return nil,errors.New("Hour lesser than input hour")

}
