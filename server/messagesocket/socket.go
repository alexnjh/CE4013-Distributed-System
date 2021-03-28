/*

Connection handler designed to follow the following message format
 _________________________________________________________________________________________________________________________________________________________________________________
|                  |                                       |                             |                         |                                    |                        |
|preamble (2 bytes)| SHA1-HASH of Creation Time (20 bytes) | Length of message (2 bytes) | Length of type (1 byte) | Type string (Length of type bytes) | Message data (y bytes) |
|                  |                                       |                             |                         |                                    |                        |
__________________________________________________________________________________________________________________________________________________________________________________

 if preamble is 0000 mean at least once
 if preamble is 0001 mean at most once
 if preamble is 0002 mean at least once + packet lost at server->client
 if preamble is 0003 mean at most once + packet lost at server->client
 if preamble is 0004 mean at least once + packet lost at client->server
 if preamble is 0005 mean at most once + packet lost at client->server
*/

package messagesocket

import (
  "os"
  "net"
  "fmt"
  "encoding/hex"
  "time"
  "bytes"
  "errors"
  "encoding/binary"
  "math/rand"
  log "github.com/sirupsen/logrus"
)

var(
  // Creates a new history list for storing replies that was sent
  histList = NewHistoryList(100)
)

const(
  // Time to wait before assuming message is lost in transit
  timeOut = 10
  // Size of buffer for saving the byte stream sent by the client
  maxBufferSize = 1024
  // Min value for random number generator
  min = 0
  // Max value for random number generator
  max = 10
)

type Message struct{
  UniqID string // Message request ID
  Addr  net.Addr // Host address of the client that sent this message
  uconn net.PacketConn // Server address including port
  Type  string // Type of message
  Data  []byte // Byte array of the payload
  Lost  bool // True = Simulate message lost, False =  Do not simulate message lost
}

// Send a reply to the client that send the message
func (m *Message) Reply(data []byte) error {

  // Check if reply is already in history list
  obj := histList.Get(m.UniqID)
  if obj != nil && obj.Processing == false{
    fmt.Printf("Reply processed putting data back\n")
    obj.Processing = true
    obj.Data = data
  }

  // Create random seed for random number generation
  rand.Seed(time.Now().UnixNano())

  // If lost simulation is true, do some randomization to simulate lost of packets
  if m.Lost {
    if (rand.Intn(max - min + 1) + min) < 5 {
      fmt.Printf("Server to client packet-lost\n")
      return nil
    }
  }

  // Write the packet's contents back to the client.
  n, err := m.uconn.WriteTo(data, m.Addr)
  if err != nil {
    return err
  }

  fmt.Printf("packet-written: bytes=%d\n", n)

  return nil
}


// Check if two messages are equal
func (m *Message) Equal(a *Message) bool{
  if m.Addr.String() == a.Addr.String() &&
  m.Type == a.Type &&
  bytes.Compare(m.Data, a.Data) == 0{
    return true
  }else{
    return false
  }
}


// Returns a channel that can be used for collecting all unpacked UDP messages.
func NewMessageSocket(host string, port int) <-chan Message{

  msgCh := make(chan Message)
  buffer := make([]byte, maxBufferSize)
  url := fmt.Sprintf("%s:%d",host,port)

  go func(msg chan<-Message) {

    // Listen of port for UDP packets
  	uconn, err := net.ListenPacket("udp", url)
  	if err != nil {
      log.Fatalf(err.Error())
  	}

    defer uconn.Close()

    for{

      // Read bytes from UDP socket
      n, addr, err := uconn.ReadFrom(buffer)
      if err != nil {
        log.Fatalf(err.Error())
        return
      }

      log.Infof("Packet-Received: bytes=%d from=%s\n",n, addr.String())
      log.Infof("%x\n", buffer[:n])

      x,status := checkValidMessage(buffer[:n])
      lengthOfMsg := int(x)

      // Check if message is valid if not drop it
      if status {

        log.Infof("Message is valid")

        dataH := make([]byte, 24)
        data1 := make([]byte, n-24)
        copy(dataH,buffer[:24])
        copy(data1,buffer[24:n])

        // Next check if the whole message is in the buffer
        if (n-24) == lengthOfMsg {

        log.Infof("Whole message is currently in buffer")

        }else if (n-24) > lengthOfMsg{

          // This should not happen but if it does drop the message as it may be corrupted with another message
          log.Errorf("Received invalid message length dropping message")
          continue

        }else{

          var deadlineExceed = false
          remaining := lengthOfMsg-(n-24)

          // Continue reading the remaining message data if buffer cannot store it
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
            log.Infof("%x\n", buffer[:n])

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
        message.Addr = addr
        message.uconn = uconn

        // Simulate reply message lost if needed by checking simulation mode in preamble
        if buffer[1] == uint8(2) || buffer[1] == uint8(4) || buffer[1] == uint8(5) || buffer[1] == uint8(7){
          message.Lost = true
        }else{
          message.Lost = false
        }

        // Drop client packet for simulating packet lost from client by checking simulation mode in preamble
        if buffer[1] == uint8(3) || buffer[1] == uint8(6){
          rand.Seed(time.Now().UnixNano())

          if (rand.Intn(max - min + 1) + min) < 5 {
            fmt.Printf("Client to server packet-lost\n")
            continue
          }

        }

        // Check if message header was succesfully unpacked
        // Should not happen as the message is checked to be valid
        if err != nil {
          log.Errorf("Failed to unpack message header")
          continue
        }else{

            // Get message request ID
            message.UniqID = string(dataH[2:22])

            // Check invocation sementics
            if buffer[1] == uint8(1) || buffer[1] == uint8(5) || buffer[1] == uint8(6) || buffer[1] == uint8(7){

              // If at most once invocation chosen, check if reply is in history list
              fmt.Printf("HEADER: %s\n",hex.EncodeToString(dataH))
              fmt.Printf("At most once chosen, Message ID: %s\n",hex.EncodeToString(dataH[2:22]))

              obj := histList.Get(message.UniqID)
              if obj == nil{
                // if reply does not exist add a placeholder in the history list and set history entry's Processing value to false
                histList.Add(message.UniqID)
              }else{
                // If Processing is true means reply is ready if not wait for reply to be ready before sending to the client
                obj = histList.Get(message.UniqID)
                if obj != nil && obj.Processing == true{
                  fmt.Printf("Found message in history list, resending reply.\n")
                  message.Reply(obj.Data)
                  continue
                }else if obj != nil && obj.Processing == false{
                  fmt.Printf("Found message in history list, but reply still processing.\n")
                  go func(msg Message){
                    for{
                      time.Sleep(2 * time.Second)
                      obj := histList.Get(msg.UniqID)
                      if obj != nil && obj.Processing == true{
                        fmt.Printf("Found message in history list, resending reply.\n")
                        msg.Reply(obj.Data)
                        return
                      }else if obj == nil{
                        // Object deleted from history list stop waiting
                        return
                      }
                    }

                  }(message)
                  continue
                }else{
                  fmt.Printf("OBJ is nil something is wrong.\n")
                }
              }

            }

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
// 1. The preamble/mode should be valid
// 2. There should be a request payload
func checkValidMessage(data []byte) (uint16,bool){

  var lengthOfMsg uint16
  n := len(data)

  // First check if first byte is 0
  if data[0] != uint8(0) {
    return 0,false
  }

  // Next check if valid invokation sementics is given
  if data[1] < uint8(0) && data[1] > uint8(7){
    fmt.Printf("Invalid invocation sementics, %v\n",data[1])
    return 0,false
  }

  // Next check the length of the message
  if n > 24 {
    numBytes := []byte{data[22], data[23]}
    lengthOfMsg = binary.BigEndian.Uint16(numBytes)
  }else{
      return 0,false
  }

  return lengthOfMsg,true

}

// Unpack the message received by stripping the request header
// Error is not needed but leaving it in for future error handling improvements
func unpack(data []byte) (Message,error){

  lengthOfType := uint8(data[0])
  typeString := string(data[1:1+lengthOfType])

  return Message{
    Type: typeString,
    Data: data[1+lengthOfType:],
  },nil


}
