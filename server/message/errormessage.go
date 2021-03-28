// Error class, used as placeholder for now
package message

import(
  "server/messagesocket"
)

// Message structure for returning a error
type ErrorMessage struct {
  // The reason for the error
  s string
}

// Return the error message
func (e *ErrorMessage) Error() string{
  return e.s
}

// Marshal ErrorMessage to bytes for transmission
func (e *ErrorMessage) Marshal() []byte{

  payload := []byte(e.s)
  hdr := messagesocket.CreateErrorMessageHeader(uint16(len(payload)))

  return append(hdr,payload...)
}

// Create a new ErrorMessage
func NewErrorMessage(s string) ErrorMessage{
  return ErrorMessage{
    s:s,
  }
}

// Un-marshal ErrorMessage to struct for processing
func UnmarshalErrorMessage(data []byte) (ErrorMessage,error){
  return ErrorMessage{
    s: string(data),
  },nil
}
