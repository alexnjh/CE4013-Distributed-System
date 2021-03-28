package messagesocket

import (
  "time"
  "crypto/sha1"
  "encoding/hex"
)

// Generate a request ID using the current time and using SHA1 to hash it
func GenerateRequestID() string {
  h := sha1.New()
  h.Write([]byte(time.Now().String()))
  id := hex.EncodeToString(h.Sum(nil))
  return id
}
