package message

// Message structure for deleteing a booking entry
type DeleteBookingMessage struct {
  // Booking ID
  ConfirmationID string
}

// Unmarshal DeleteBookingMessage from bytes into a structure for processing
func UnmarshalDelBookingMsg(data []byte) (DeleteBookingMessage,error){
  return DeleteBookingMessage{
    ConfirmationID: string(data),
  },nil
}
