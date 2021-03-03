package message

type ViewBookingMessage struct {
  ConfirmationID string
}

func UnmarshalViewBookingMsg(data []byte) (ViewBookingMessage,error){
  return ViewBookingMessage{
    ConfirmationID: string(data),
  },nil
}
