// Error class, used as placeholder for now
package errors

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

func New(s string) ErrorMessage{
  return ErrorMessage{
    s:s,
  }
}

func Unmarshal(data []byte) (ErrorMessage,error){
  return ErrorMessage{
    s: string(data),
  },nil
}
