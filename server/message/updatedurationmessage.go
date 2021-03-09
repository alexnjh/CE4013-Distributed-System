package message


type UpdateDurationMessage struct {
  ConfirmationID string
  Offset int
}

func UnmarshalUpdateDurationMsg(data []byte) (UpdateDurationMessage,error){
  return UpdateDurationMessage{
    ConfirmationID: string(data),
	
  },nil
}