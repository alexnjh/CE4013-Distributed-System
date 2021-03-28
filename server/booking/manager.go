package booking

import (
  "errors"
  "fmt"
  "math"
  "server/facility"
  "sort"
)

// The BookingManager manages all booking entries including adding
// deleting, modifying of booking entries
type BookingManager struct {
	BookingList map[Day][]*Booking
}

// Create a generic booking manager
func NewGenericBookingManager() *BookingManager {

  // Create a map to store all the booking list for each day
	list := make(map[Day][]*Booking)

  // Loop through each list and initialize a empty list
	for i := 0; i < 7; i++ {
		list[Day(i)] = make([]*Booking, 0)
	}

	return &BookingManager{
		BookingList: list,
	}
}

// Get a list of available timeslots/dates for a range of days
// Date refers to the date structure
func (b *BookingManager) GetAvailableDates(fac facility.Facility, days ...Day) [][]DateRange {

	result := make([][]DateRange, len(days))

	for idx, d := range days {
		result[idx] = b.getAvailableDates(d, b.getBookings(d, fac))
	}

	return result

}

// Get a list of available timeslots/dates for a single day
func (b *BookingManager) getAvailableDates(day Day, list []*Booking) []DateRange {

	initial := MinDate(day) // Minimum date value possible for a specific day
	end := MaxDate(day) // Maximum date value possible for a specific day
	availDates := make([]DateRange, 0) // Create array to store available dates

	if len(list) != 0 {
    // Booking list is not empty
		if initial.Equal(list[0].Start) && end.Equal(list[0].End) {
			return make([]DateRange, 0)
		}

    // Set the first available date end value to the end date of the first Booking
    // if the start date of the first booking is the same as the minimum start date
    if !initial.Equal(list[0].Start){
      availDates = append(availDates, DateRange{
        Start: initial,
        End:   list[0].Start,
      })
    }

    // Loop through all the bookings and add the list of avaiable dates to the list
    // Available dates are date ranges that does not have a booking attached to it.
    // This date range can be known by taking the current booking entry's end date and
    // the next booking entry's start date. We can do this because the booking list is sorted.
    // The next booking entry on the list is definately later then the current booking entry.
    for idx, d := range list[1:] {
      availDates = append(availDates, DateRange{
        Start: list[idx].End,
        End:   d.Start,
      })
    }

    // If the end date of the last booking is not the maximum date value
    // Create another date to fill the gap from the end date of the last booking
    // to the maximum end date
    if list[len(list)-1].End.LessThan(end) {
      availDates = append(availDates, DateRange{
        Start: list[len(list)-1].End,
        End:   end,
      })
    }

    return availDates

	} else {

    // Booking list is empty (The avaiable range is the MinDate -> MaxDate)
		availDates = append(availDates, DateRange{
			Start: initial,
			End:   end,
		})
		return availDates
	}

}

// Get booking entry based on ConfirmationID (Public method)
func (b *BookingManager) GetBooking(id string) (*Booking, error) {
	return b.getBooking(id)
}

// Get booking entry based on ConfirmationID actual implementation (Private method)
func (b *BookingManager) getBooking(id string) (*Booking, error) {

	for _, x := range b.BookingList {
		for _, y := range x {
      // Check if ConfirmationID matches the one given in id
			if y.ConfirmationID == id {
				return y, nil
			}
		}
	}

	return nil, errors.New("Invalid booking ID")

}

// Get list of booking entries based on Facility and Day
func (b *BookingManager) getBookings(day Day, fac facility.Facility) []*Booking {

	result := make([]*Booking, 0)

	for _, booking := range b.BookingList[day] {
		if booking.Fac == fac {
			result = append(result, booking)
		}
	}

	return result

}

// Add a new booking entry
func (b *BookingManager) AddBooking(
	name string,
	start Date,
	end Date,
	f facility.Facility,
) (*Booking, error) {

  // Create a new booking object to store the details of the booking submitted by the client
	obj := NewBooking(name, start, end, f)

  // Check if the new booking overlaps with other bookings
	cbk, status := b.CheckForConflict(&obj)

	if status {
		return nil, errors.New(fmt.Sprintf("Booking duration [%s => %s] causes conflict with [%s => %s]", obj.Start.String(), obj.End.String(), cbk.Start.String(), cbk.End.String()))
	}

	// Append works on nil slices.
	b.BookingList[start.Day] = append(b.BookingList[start.Day], &obj)

  // Sort the booking list after adding the new booking entry
	sortBooking(b, start)

  // Broadcast to monitoring clients if there is
	GetManager().Broadcast(f, CreateBooking, b, name)

	return &obj, nil

}

// Sorting function for booking list
func sortBooking(b *BookingManager, start Date) {
	// Sort in descending order
	sort.SliceStable(b.BookingList[start.Day], func(i, j int) bool {

		return b.BookingList[start.Day][i].End.LessThan(b.BookingList[start.Day][j].Start)

	})
}

// Check if a booking entry overlap with other booking entries
func (b *BookingManager) CheckForConflict(booking *Booking) (*Booking, bool) {

	list := b.getBookings(booking.Start.Day, booking.Fac)

	for _, v := range list {
		if booking.Start.LessThan(v.End) && v.Start.LessThan(booking.End) && booking.ConfirmationID != v.ConfirmationID {
			return v, true
		}
	}

	return nil, false
}

// UpdateBooking is responsible for the postponing or moving forward of a booking's timeslot
// If offset value is negative, this indicates the user will like to bring forward the booking start time
// If offset value is positive, this indicates the user will like to postpone the booking start time
func (b *BookingManager) UpdateBooking(id string, offset int) error {

	booking, err := b.getBooking(id)

	if err != nil {
		return err
	}

  // Convert minutes to a date object
	offsetDate := MinutesToDate(booking.Start.Day, Abs(offset))

	var s, e *Date

  // Check if offset value is negative
	if math.Signbit(float64(offset)) {

    // Bring forward the start time by subtracting the offset
		s, err = booking.Start.Minus(offsetDate)

		if err != nil {
			return err
		}


    // Bring forward the end time by subtracting the offset
		e, err = booking.End.Minus(offsetDate)

		if err != nil {
			return err
		}

	} else {

    // Postpone the start time by adding the offset
		s, err = booking.Start.Plus(offsetDate)

		if err != nil {
			return err
		}

    // Postpone the end time by adding the offset
		e, err = booking.End.Plus(offsetDate)

		if err != nil {
			return err
		}
	}


	obj := Booking{
		BookerName:     booking.BookerName,
		ConfirmationID: booking.ConfirmationID,
		Start:          *s,
		End:            *e,
		Fac:            booking.Fac,
	}

	// Check for overlapping timeslots first before modifying the actual booking object
	cbk, status := b.CheckForConflict(&obj)

	if status {
		return errors.New(fmt.Sprintf("Booking duration [%s => %s] causes conflict with [%s => %s]", s.String(), e.String(), cbk.Start.String(), cbk.End.String()))
	}

	booking.Start = *s
	booking.End = *e

  // Sort the booking list after changing the time
	sortBooking(b, booking.Start)

  // Broadcast operation to monitoring clients
	GetManager().Broadcast(booking.Fac, UpdateBooking, b, booking.BookerName)

	return nil

}

// UpdateBookingDuration is responsible for the increasing/decreasing the duration of a booking entry
// Increasing of booking duration is accomplish by adding the offset to the end date time
// Decreasing of booking duration is accomplish by subtracting offset from the end date time
func (b *BookingManager) UpdateBookingDuration(id string, offset int) error {

	booking, err := b.getBooking(id)

	if err != nil {
		return err
	}

	offsetDate := MinutesToDate(booking.Start.Day, Abs(offset))

	var s, e *Date

	if math.Signbit(float64(offset)) {

    // Subtract offset from end date if decreasing duration
		e, err = booking.End.Minus(offsetDate)

		if err != nil {
			return err
		}

	} else {

    // Add offset to end date if increasing duration
		e, err = booking.End.Plus(offsetDate)

		if err != nil {
			return err
		}
	}

	// If start is not less than end this duration cannot be used
	if !booking.Start.LessThan(*e) {
		return errors.New("Start time is not less than End time")
	}

	// Create booking object to check for conflict
	obj := Booking{
		BookerName:     booking.BookerName,
		ConfirmationID: booking.ConfirmationID,
		Start:          booking.Start,
		End:            *e,
		Fac:            booking.Fac,
	}

  // Check for overlapping timeslots before committing
	cbk, status := b.CheckForConflict(&obj)

	if status {
		return errors.New(fmt.Sprintf("Booking duration [%s => %s] causes conflict with [%s => %s]", s.String(), e.String(), cbk.Start.String(), cbk.End.String()))
	}

	booking.End = *e

  // Sort the booking list after changing the time
	sortBooking(b, booking.Start)

  // Broadcast operation to monitoring clients
	GetManager().Broadcast(booking.Fac, UpdateDurationBooking, b, booking.BookerName)

	return nil

}

// Remove a booking from booking list
func (b *BookingManager) RemoveBooking(id string) error {

	for _, x := range b.BookingList {
		for idx, v := range x {
      // Find a booking with the same ID and remove it
			if v.ConfirmationID == id {
				d := v.Start.Day
				tmp := b.BookingList[d][idx]
				b.BookingList[d] = RemoveElementFromSlice(b.BookingList[d], idx)

        // Broadcast operation to monitoring clients
				GetManager().Broadcast(tmp.Fac, DeleteBooking, b, tmp.BookerName)
				return nil
			}
		}
	}
	return errors.New("Invalid booking ID (" + id + ")")

}
