// Error class, used as placeholder for now
package errors

import(
  "server/messagesocket"
)

type MessageError struct {
  s string
}

func (e *MessageError) Error() string{
  return e.s
}

func (e *MessageError) Marshal() []byte{

  payload := []byte(e.s)
  hdr := messagesocket.CreateMessageErrorHeader(uint16(len(payload)))

  return append(hdr,payload...)
}

func New(s string) MessageError{
  return MessageError{
    s:s,
  }
}

func Unmarshal(data []byte) (MessageError,error){
  return MessageError{
    s: string(data),
  },nil
}
