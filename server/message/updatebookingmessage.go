package message

import (
	"bytes"
	"encoding/binary"
	"fmt"
)

type UpdateBookingMessage struct {
  ConfirmationID string
  Offset int
}

func UnmarshalUpdateBookingMsg(data []byte) (UpdateBookingMessage,error){
  fmt.Printf("%s\n", data[4:])
  
  fmt.Printf("%v\n", data[:4])
  
  var offset int32
	buf := bytes.NewBuffer(data[:4])
	err := binary.Read(buf, binary.BigEndian, &offset)

		if err != nil {
		 fmt.Println(err.Error())
		}
	
  return UpdateBookingMessage{
   
	ConfirmationID: string(data[4:]),
	
	Offset: int(offset),
	
	
	
  },nil
}