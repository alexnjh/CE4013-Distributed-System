package booking

import(
  // "fmt"
  "sort"
  "server/facility"
)

type BookingManager struct{
  BookingList map[Day][]Booking
}

// Create a generic booking manager
func NewGenericBookingManager() *BookingManager{

  list := make(map[Day][]Booking)

  for i := 0; i < 7; i++ {
    list[Day(i)]=make([]Booking, 0)
	}

  return &BookingManager{
    BookingList: list,
  }
}

func (b *BookingManager) GetAvailableDates(fac facility.Facility,days ...Day) [][]DateRange{

  result := make([][]DateRange, len(days))

  for idx, d := range days {
    result[idx]=b.getAvailableDates(d, b.getBookings(d,fac))
    // fmt.Printf("%v\n",len(b.getBookings(d,fac)))
  }

  return result;

}

// This is based on the assumtion than when a booking is added the array it is always sorted
// Therefore it is not necessary to sort the array
func (b *BookingManager) getAvailableDates(day Day, list []Booking) []DateRange{

  initial := Date{
    Day: day,
    Hour: 0,
    Minute: 0,
  }

  end := Date{
    Day: day,
    Hour: 23,
    Minute: 59,
  }

  availDates := make([]DateRange,len(list))


  if len(list) != 0{

    if initial.Equal(list[0].Start) && end.Equal(list[0].End){
      return make([]DateRange,0)
    }

    availDates[0] = DateRange{
      Start: initial,
      End: list[0].Start,
    }

  }else{
    availDates = append(availDates, DateRange{
      Start: initial,
      End: end,
    })
    return availDates
  }


  for idx, d := range list[1:] {

    availDates[idx+1]=DateRange{
      Start: list[idx].End,
      End: d.Start,
    }
  }

  if list[len(list)-1].End.LessThan(end) {
    availDates = append(availDates,DateRange{
      Start: list[len(list)-1].End,
      End: end,
    })
  }

  return availDates

}

func (b *BookingManager) getBookings(day Day, fac facility.Facility) []Booking{

  result := make([]Booking,0)

  for _, booking := range b.BookingList[day] {
    if booking.Fac == fac {
      result = append(result, booking)
    }
  }

  return result

}

func (b *BookingManager) AddBooking(
  name string,
  start Date,
  end Date,
  f facility.Facility,
  ) error{

  obj := NewBooking(name,start,end, f)

  // Append works on nil slices.
  b.BookingList[start.Day] = append(b.BookingList[start.Day], obj)

  // Sort in descending order
  sort.SliceStable(b.BookingList[start.Day], func(i, j int) bool {

      return b.BookingList[start.Day][i].End.LessThan(b.BookingList[start.Day][j].Start)

  })

  return nil

}

func (b *BookingManager) RemoveBooking(d Day, id string) error{


  for idx, v := range b.BookingList[d] {
    if v.ConfirmationID == id {
      b.BookingList[d] = RemoveElementFromSlice(b.BookingList[d],idx)
    }
  }

  // Sorting is not necessary here as the array was sorted in the first place

  return nil

}
