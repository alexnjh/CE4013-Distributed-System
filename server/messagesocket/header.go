package messagesocket

import (
  "fmt"
  "encoding/hex"
)

func CreateMessageErrorHeader(lengthOfPayload uint16) []byte {

  typeString := "054572726f72"
  s := "0000"+fmt.Sprintf("%04x", lengthOfPayload+uint16(len(typeString)/2))+typeString
  data, err := hex.DecodeString(s)

  if err != nil {
      panic(err)
  }

  return data
}
