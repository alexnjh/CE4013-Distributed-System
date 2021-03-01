package application;
//sent the confirmation id to the server 
public class RemoveRequest implements RequestMessage{
		
		private String cid;
		
		
		RemoveRequest(String id){
			this.cid = id;
		}
		
		@Override
		public byte[] Marshal() {

			
			Integer conID = cid.length();
			
			byte[] payload = Helper.ConcatByteArray(new byte[] {conID.byteValue()}, cid.getBytes());
						
			byte[] header = Header.CreateAddBookingHeader(payload.length);
			
			byte[] finalPayload = Helper.ConcatByteArray(header,payload);
			
			return finalPayload;
		}

	}

