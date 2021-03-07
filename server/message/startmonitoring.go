package message

import (
	"bytes"
	"encoding/binary"
	"fmt"
)

type StartMonitoringMessage struct {
	FacilityName string
	Duration int64
}

func UnmarshalStartMonitoringMsg(data []byte) (StartMonitoringMessage,error) {
	fmt.Printf("%x\n", data[:8])
	fmt.Printf("%x\n", data[8:])

	var duration int64
	buf := bytes.NewBuffer(data[:8])
	binary.Read(buf, binary.BigEndian, &duration)

	return StartMonitoringMessage{
		Duration: duration,
		FacilityName: string(data[8:]),
	}, nil
}