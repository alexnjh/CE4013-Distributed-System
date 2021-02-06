package utilities

import ()

func Marshal(rep Reply) ([]byte,error){

    data := make([]byte, 1)
    data[0] = byte(uint8(len(rep.Type)))
    data = append(data, []byte(rep.Type)...)


    temp := make([]byte, 6)

    for _, v := range rep.DateRanges {

      temp[0] = byte(v.Start.Day)
      temp[1] = byte(v.Start.Hour)
      temp[2] = byte(v.Start.Minute)
      temp[3] = byte(v.End.Day)
      temp[4] = byte(v.End.Hour)
      temp[5] = byte(v.End.Minute)

      data = append(data, temp...)
    }

    return data,nil

}
