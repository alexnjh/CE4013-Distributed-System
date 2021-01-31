package booking

func RemoveElementFromSlice(slice []Booking, s int) []Booking {
    return append(slice[:s], slice[s+1:]...)
}
