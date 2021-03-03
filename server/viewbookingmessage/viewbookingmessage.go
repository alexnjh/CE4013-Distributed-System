package viewbookingmessage

import(
  "server/messagesocket"
)

type ViewBookingMessage struct {
  confID string
}

func Unmarshal(data []byte) (ViewBookingMessage,error){
  return ConfirmMessage{
    s: string(data),
  },nil
}
