package message

type UpdateBookingMessage struct {
  ConfirmationID string
  Offset int
}

func UnmarshalUpdateBookingMsg(data []byte) (UpdateBookingMessage,error){
  return UpdateBookingMessage{
    ConfirmationID: string(data),
  },nil
}