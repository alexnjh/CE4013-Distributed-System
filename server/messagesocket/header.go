package messagesocket

import (
  "fmt"
  "encoding/hex"
)

func CreateErrorMessageHeader(lengthOfPayload uint16) []byte {

  typeString := "054572726f72"
  s := "0000"+fmt.Sprintf("%04x", lengthOfPayload+uint16(len(typeString)/2))+typeString
  data, err := hex.DecodeString(s)

  if err != nil {
      panic(err)
  }

  return data
}

func CreateFacilityAvailabilityQueryHeader(dataLength uint16) []byte {
  // Add 15 for type string length (1 byte), Availability string (12 bytes), Data String Length (2 bytes)
  s := "0000"+fmt.Sprintf("%04x", dataLength+15)+"0c417661696c6162696c697479"+fmt.Sprintf("%04x", dataLength)
  data, err := hex.DecodeString(s)

  if err != nil {
    panic(err)
  }

  return data
}
