package confirmmessage

import(
  "server/messagesocket"
)

type ConfirmMessage struct {
  s string
}

func (e *ConfirmMessage) Marshal() []byte{

  payload := []byte(e.s)
  hdr := messagesocket.CreateConfirmMessageHeader(uint16(len(payload)))

  return append(hdr,payload...)
}

func New(s string) ConfirmMessage{
  return ConfirmMessage{
    s:s,
  }
}

func Unmarshal(data []byte) (ConfirmMessage,error){
  return ConfirmMessage{
    s: string(data),
  },nil
}
