package utilities

import(
  "server/booking"
)

type Request struct {
  Type    string
  Name    string
  Dates   []booking.Date
}

type Reply struct {
  Type          string
  DateRanges    []booking.DateRange
}
