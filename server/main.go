package main

import (
  "net"
  "time"
  "flag"
  log "github.com/sirupsen/logrus"
  util "server/utilities"
)

const(
  maxBufferSize   = 1024
)

func main(){

  addrPtr := flag.String("a", "127.0.0.1:2222", "Address to listen to")
  flag.Parse()

  // Same as using net.ResolveUDPAddr and netListenUDP
	uconn, err := net.ListenPacket("udp", *addrPtr)
	if err != nil {
    log.Fatalf(err.Error())
	}

  defer uconn.Close()

  doneCh := make(chan error, 1)
  buffer := make([]byte, maxBufferSize)

  go func() {
    for {
      n, addr, err := uconn.ReadFrom(buffer)
      if err != nil {
        doneCh <- err
        return
      }

      log.Infof("Packet-Received: bytes=%d from=%s\n",n, addr.String())

      // Unmarshal Request
      req, err := util.Unmarshal(buffer[:n])

      if err != nil {
        log.Fatalf(err.Error())
      }

      deadline := time.Now().Add(10*time.Second)
      err = uconn.SetWriteDeadline(deadline)
      if err != nil {
        doneCh <- err
        return
      }

      log.Infof("Facility: %s",req.Name)
      log.Infof("[DateList]")

      dateranges := make([]util.DateRange,len(req.Dates))

      for idx , v := range req.Dates {
          log.Infof("%d) %d/%d/%d",idx+1,v.Day,v.Hour,v.Minute)
          dateranges[idx] = util.DateRange{
            Start: v,
            End: v,
          }
      }

      rep := util.Reply{
        Type: "Reply",
        DateRanges: dateranges,
      }

      // Marshal Reply
      data, err := util.Marshal(rep)

      // Write the packet's contents back to the client.
      n, err = uconn.WriteTo(data, addr)
      if err != nil {
        doneCh <- err
        return
      }

      log.Infof("Packet-Written: bytes=%d to=%s\n", n, addr.String())
    }
  }()

  log.Infof("Listening on UDP at %s\n",*addrPtr)

  if err := <-doneCh; err != nil {
    log.Infof("error: %s\n", err.Error())
    return
  }

}
