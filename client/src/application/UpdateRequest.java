package application;

public class UpdateRequest implements RequestMessage{
		
		private String id;
		private Integer offset;		

		UpdateRequest(String id, int offset){
			this.id = id;
			this.offset = offset;
		}
		
		
		@Override
public byte[] Marshal() {
			
			
			byte[] payload = Helper.intToBytes(offset);
			payload = Helper.ConcatByteArray(id.getBytes(),payload);
			
			byte[] header = Header.CreateUpdateDurationHeader(payload.length);
			
			byte[] finalPayload = Helper.ConcatByteArray(header,payload);
			
			return finalPayload;
		}

	}


