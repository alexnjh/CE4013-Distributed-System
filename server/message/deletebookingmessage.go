package message

type DeleteBookingMessage struct {
  ConfirmationID string
}

func UnmarshalDelBookingMsg(data []byte) (DeleteBookingMessage,error){
  return DeleteBookingMessage{
    ConfirmationID: string(data),
  },nil
}
