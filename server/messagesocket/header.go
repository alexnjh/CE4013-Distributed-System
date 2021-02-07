package messagesocket

import (
  "fmt"
  "encoding/hex"
)

func CreateMessageErrorHeader(lengthOfPayload uint16) []byte {

  s := "0000"+fmt.Sprintf("%04x", lengthOfPayload+7)+"084572726f720a"
  data, err := hex.DecodeString(s)

  if err != nil {
      panic(err)
  }

  return data
}
