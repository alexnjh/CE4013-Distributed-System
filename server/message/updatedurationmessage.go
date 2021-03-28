package message

import (
	"bytes"
	"encoding/binary"
	"fmt"
)

// Message structure for updating duration of a booking entry
type UpdateDurationMessage struct {
  ConfirmationID string
  Offset int
}

// Unmarshal UpdateDurationMessage from bytes into a structure to be processed by the program
func UnmarshalUpdateDurationMsg(data []byte) (UpdateDurationMessage,error){

  var offset int32
	buf := bytes.NewBuffer(data[:4])
	err := binary.Read(buf, binary.BigEndian, &offset)

		if err != nil {
		 fmt.Println(err.Error())
		}

  return UpdateDurationMessage{
	ConfirmationID: string(data[4:]),
	Offset: int(offset),
  },nil
}
