package messagesocket

import (
  "fmt"
  "encoding/hex"
)

func CreateConfirmMessageHeader(lengthOfPayload uint16) []byte {

  typeString := "07436f6e6669726d0a"
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", lengthOfPayload+uint16(8))+typeString
  data, err := hex.DecodeString(s)

  if err != nil {
      panic(err)
  }

  return data
}

func CreateErrorMessageHeader(lengthOfPayload uint16) []byte {

  typeString := "054572726f72"
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", lengthOfPayload+uint16(6))+typeString
  data, err := hex.DecodeString(s)

  if err != nil {
      panic(err)
  }

  return data
}

func CreateQueryFacilityAvailabilityHeader(dataLength uint16) []byte {
  // Add 15 for type string length (1 byte), Availability string (12 bytes), Data String Length (2 bytes)
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", dataLength+15)+"0c417661696c6162696c697479"
  data, err := hex.DecodeString(s)

  if err != nil {
    panic(err)
  }

  return data
}

func CreateMonitorAvailabilityHeader(dataLength uint16) []byte {
  // Add 18 for type string length (1 byte), Availability string (12 bytes), Data String Length (2 bytes)
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", dataLength+18)+"0f4d6f6e417661696c6162696c697479"
  data, err := hex.DecodeString(s)

  if err != nil {
    panic(err)
  }

  return data
}

func CreateAddBookingHeader(dataLength uint16) []byte {
  // Add 11 for type string length (1 byte), AddBooking string (10 bytes)
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", dataLength+13)+"07416464426f6f6b696e67"
  data, err := hex.DecodeString(s)

  if err != nil {
   panic(err)
  }

  return data
}

func CreateBookingDetailHeader(dataLength uint16) []byte {
  s := "0000"+GenerateRequestID()+fmt.Sprintf("%04x", dataLength+14)+"0d426f6f6b696e6744657461696c"
  data, err := hex.DecodeString(s)

  if err != nil {
   panic(err)
  }

  return data
}
