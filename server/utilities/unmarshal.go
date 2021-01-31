package utilities

import (
  "errors"
  "server/booking"
)

func Unmarshal(data []byte) (Request, error){

  if len(data) == 0 {
    errors.New("Byte array length == 0")
  }

  lenOfType := int(data[0])
  lenOfName := int(data[lenOfType+1])

  reqType := string(data[1:lenOfType+1])
  fctName := string(data[lenOfType+2:lenOfType+lenOfName+2])

  arr := make([]booking.Date, 0)

  for i := lenOfType+lenOfName+2; i < len(data); i=i+3 {
    temp := booking.Date{
      Day: booking.Day(int(data[i])),
      Hour: int(data[i+1]),
      Minute: int(data[i+2]),
    }

    arr = append(arr, temp)

  }

  return Request{
    Type: reqType,
    Name: fctName,
    Dates: arr,
  }, nil

}
