package message

// Message structure for viewing a booking entry
type ViewBookingMessage struct {
  ConfirmationID string
}

// Unmarshal view booking messages from bytes into a structure to be processed by the program
func UnmarshalViewBookingMsg(data []byte) (ViewBookingMessage,error){
  return ViewBookingMessage{
    ConfirmationID: string(data),
  },nil
}
