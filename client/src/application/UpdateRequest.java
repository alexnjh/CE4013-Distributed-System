package application;
//Update Request Message.
public class UpdateRequest implements RequestMessage{
		
		private String id;
		private Integer offset;		
		//Constructor
		UpdateRequest(String id, int offset){
			this.id = id;
			this.offset = offset;
		}
		
		//Marshal data to byte stream.
		@Override
		public byte[] Marshal(int invocation) {
			
			
			byte[] payload = Helper.intToBytes(offset);
			payload = Helper.ConcatByteArray(payload,id.getBytes());
			
			byte[] header = Header.CreateUpdateBookingHeader(payload.length, invocation);
			
			byte[] finalPayload = Helper.ConcatByteArray(header,payload);
			
			return finalPayload;
		}

	}


