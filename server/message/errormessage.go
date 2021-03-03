// Error class, used as placeholder for now
package message

import(
  "server/messagesocket"
)

type ErrorMessage struct {
  s string
}

func (e *ErrorMessage) Error() string{
  return e.s
}

func (e *ErrorMessage) Marshal() []byte{

  payload := []byte(e.s)
  hdr := messagesocket.CreateErrorMessageHeader(uint16(len(payload)))

  return append(hdr,payload...)
}

func NewErrorMessage(s string) ErrorMessage{
  return ErrorMessage{
    s:s,
  }
}

func UnmarshalErrorMessage(data []byte) (ErrorMessage,error){
  return ErrorMessage{
    s: string(data),
  },nil
}
