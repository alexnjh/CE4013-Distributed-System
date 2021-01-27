package utilities

import(
  "fmt"
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

type Request struct {
  Type    string
  Name    string
  Dates   []Date
}

type Reply struct {
  Type          string
  DateRanges    []DateRange
}

type Date struct {
    Day     Day
    Hour    int
    Minute  int
}

func (d *Date) String() string{
  return fmt.Sprintf("%d/%d/%d",d.Day,d.Hour,d.Minute);
}

type DateRange struct {
    Start     Date
    End       Date
}
