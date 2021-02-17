/*

Based on the follow message format
 _________________________________________________________________________________________________________________________________________
|                  |                             |                         |                                    |                        |
|preamble (2 bytes)| Length of message (2 bytes) | Length of type (1 byte) | Type string (Length of type bytes) | Message data (y bytes) |
|                  |                             |                         |                                    |                        |
__________________________________________________________________________________________________________________________________________

*/

package messagesocket

import (
  "os"
  "net"
  "fmt"
  "time"
  "errors"
  "encoding/binary"
  log "github.com/sirupsen/logrus"
)

const(
  timeOut = 10
  maxBufferSize = 1024
)

type Message struct{
  addr  net.Addr
  uconn net.PacketConn
  Type  string
  Data  []byte
}

func (m *Message) Reply(data []byte) error {

  // Write the packet's contents back to the client.
  n, err := m.uconn.WriteTo(data, m.addr)
  if err != nil {
    return err
  }

  fmt.Printf("packet-written: bytes=%d\n", n)

  return nil
}


// Returns a channel that can be used for collecting all unpacked UDP messages.
func NewMessageSocket(host string, port int) <-chan Message{

  msgCh := make(chan Message)
  buffer := make([]byte, maxBufferSize)
  url := fmt.Sprintf("%s:%d",host,port)

  go func(msg chan<-Message) {

  	uconn, err := net.ListenPacket("udp", url)
  	if err != nil {
      log.Fatalf(err.Error())
  	}

    defer uconn.Close()

    for{

      n, addr, err := uconn.ReadFrom(buffer)
      if err != nil {
        log.Fatalf(err.Error())
        return
      }

      log.Infof("Packet-Received: bytes=%d from=%s\n",n, addr.String())

      x,status := checkValidMessage(buffer[:n])
      lengthOfMsg := int(x)

      // Check if message is valid if not drop it
      if status {

        log.Infof("Message is valid")

        data1 := make([]byte, n-4)
        copy(data1,buffer[4:n])

        // Next check if the whole message is in the buffer
        if (n-4) == lengthOfMsg {

        log.Infof("Whole message is currently in buffer")

        }else if (n-4) > lengthOfMsg{

          // This should not happen but if it does drop the message as it may be corrupted with another message
          log.Errorf("Received invalid message length dropping message")
          continue

        }else{

          var deadlineExceed = false
          remaining := lengthOfMsg-(n-4)

          // Continue reading the remaining messages
          for {

            // Set a deadline and drop the packet if deadline reached
            uconn.SetReadDeadline(time.Now().Add(timeOut*time.Second))

            n, addr, err := uconn.ReadFrom(buffer)
            if err != nil {

              if errors.Is(err, os.ErrDeadlineExceeded) {
                log.Errorf(err.Error())
                deadlineExceed = true
                break
              }else{
                log.Fatalf(err.Error())
              }
              return
            }

            log.Infof("Packet-Received: bytes=%d from=%s\n",n, addr.String())

            if n == remaining {
              data1 = append(data1,buffer[:n]...)
              break
            }else{
              data1 = append(data1,buffer[:n]...)
            }
          }

          // Reset deadline to listen for new messages
          uconn.SetReadDeadline(time.Time{})

          if deadlineExceed {
            continue
          }
        }

        // Unpack header from message
        message, err := unpack(data1)
        message.addr = addr
        message.uconn = uconn


        if err != nil {
          // Should not happen as it is not added in yet
          log.Errorf("Failed to unpack message header")
          continue
        }else{
            msg <- message
        }

      }else{
        log.Errorf("Invalid message received")
      }

    }
  }(msgCh)
  return msgCh
}

// Check if a message is valid by checking
// 1. The preamble should be [00 00 00 00]
// 2. The length of the message should be bigger than the header
func checkValidMessage(data []byte) (uint16,bool){

  var lengthOfMsg uint16
  n := len(data)

  // First check if start of new message
  for i := 0; i < 2; i++ {
    if data[i] != uint8(0) {
      return 0,false
    }
  }

  // Next check the length of the message
  if n > 4 {
    numBytes := []byte{data[2], data[3]}
    lengthOfMsg = binary.BigEndian.Uint16(numBytes)
  }else{
      return 0,false
  }

  return lengthOfMsg,true

}

// Unpack the message received
// Error is not needed but leaving it in for future error handling improvements
func unpack(data []byte) (Message,error){

  lengthOfType := uint8(data[0])

  log.Infof("%v %v",data, lengthOfType)

  typeString := string(data[1:1+lengthOfType])

  return Message{
    Type: typeString,
    Data: data[1+lengthOfType:],
  },nil


}
