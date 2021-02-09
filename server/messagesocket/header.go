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

func CreateFacilityAvailabilityQueryHeader(dataLength uint16) []byte {
  // Add 15 for type string length (1 byte), AddBooking string (12 bytes), Data String Length (2 bytes)
  s := "0000"+fmt.Sprintf("%04x", dataLength+15)+"0c416464426f6f6b696e67"+fmt.Sprintf("%04x", dataLength)
  data, err := hex.DecodeString(s)

  if err != nil {
    panic(err)
  }

  return data
}
