package booking

// Remove a element form a slice/list/array
func RemoveElementFromSlice(slice []*Booking, s int) []*Booking {
    return append(slice[:s], slice[s+1:]...)
}

// Abs returns the absolute value of x.
func Abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}
