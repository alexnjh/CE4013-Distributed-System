package message

import (
	"bytes"
	"encoding/binary"
	"fmt"
)

// Message structure for to inform the server to start a monitoring process
type StartMonitoringMessage struct {
  // Name of the facility that the client want to monitor
	FacilityName string
  // The length of time to monitor
	Duration int64
}

// Unmarshal StartMonitoringMessage from bytes into a structure to be processed by the program
func UnmarshalStartMonitoringMsg(data []byte) (StartMonitoringMessage,error) {
	fmt.Printf("%x\n", data[:8])
	fmt.Printf("%x\n", data[8:])

	var duration int64
	buf := bytes.NewBuffer(data[:8])
	err := binary.Read(buf, binary.BigEndian, &duration)

	if err != nil {
		fmt.Println(err.Error())
	}

	return StartMonitoringMessage{
		Duration: duration,
		FacilityName: string(data[8:]),
	}, nil
}
