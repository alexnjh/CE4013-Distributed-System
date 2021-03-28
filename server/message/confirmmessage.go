package message

import(
  "server/messagesocket"
)

// Message structure for the Confimation message
// send by the server to the client to notify
// the client the service execution was successful
type ConfirmMessage struct {
  s string
}

// Marshal ConfirmMessage from structure to bytes for transmission
func (e *ConfirmMessage) Marshal() []byte{

  payload := []byte(e.s)
  hdr := messagesocket.CreateConfirmMessageHeader(uint16(len(payload)))

  return append(hdr,payload...)
}

// Create new ConfirmMessage structure
func NewConfirmMessage(s string) ConfirmMessage{
  return ConfirmMessage{
    s:s,
  }
}

// Unmarshal ConfirmMessage from bytes into a structure to be processed by the program
func UnmarshalConfirmMsg(data []byte) (ConfirmMessage,error){
  return ConfirmMessage{
    s: string(data),
  },nil
}
