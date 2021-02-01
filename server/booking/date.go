package booking

import(
  "fmt"
  "errors"
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
    Hour: 23,
    Minute: 59,
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

func (d *Date) Plus(v Date) (*Date,error){

  if d.Day != v.Day {
    return nil,errors.New("Invalid Day, Day should be the same")
  }

  hour := d.Hour + v.Hour
  minute := d.Minute + v.Minute

  if minute > 59 {
    hour +=  minute / 60
    minute = minute % 60
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

    return &Date{
      Day: d.Day,
      Hour: hour,
      Minute: min,
    },nil

  }

  return nil,errors.New("Hour lesser than input hour")

}
