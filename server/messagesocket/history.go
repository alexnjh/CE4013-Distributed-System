package messagesocket

import (
)

type HistoryList struct{
  maxQLen int
  list []*HistoryEntry
}

type HistoryEntry struct{
  MessageID string
  Data  []byte
  Processing bool
}

func NewHistoryList(qlen int) *HistoryList{
  return &HistoryList{
    maxQLen: qlen,
    list: []*HistoryEntry{},
  }
}

// Check if message reply in history
func (h *HistoryList) Get(id string) *HistoryEntry {

  for _, x := range h.list{
    if x.MessageID == id {
        return x
    }
  }

  return nil

}

// Check if message in reply in history
func (h *HistoryList) Add(id string) bool{

  if len(h.list) == h.maxQLen {
    h.list = h.list[1:]; // Slice off the element once it is dequeued.
  }
  h.list = append(h.list, &HistoryEntry{id,[]byte{},false});
  return true
}
