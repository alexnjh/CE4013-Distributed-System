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

			Integer nameLength = id.length();
			Integer offsetLength = String.valueOf(offset).length();
			
			byte[] payload = Helper.ConcatByteArray(new byte[] {nameLength.byteValue()},id.getBytes());
			payload = Helper.ConcatByteArray(payload,new byte[] {offsetLength.byteValue()});
			
			
			payload = Helper.ConcatByteArray(payload,toByteArray(offset));
			
			byte[] header = Header.UpdateBookingHeader(payload.length);
			
			byte[] finalPayload = Helper.ConcatByteArray(header,payload);
			
			return finalPayload;
		}
		public byte[] toByteArray(int value) {
		    return new byte[] {
		            (byte)(value >> 24),
		            (byte)(value >> 16),
		            (byte)(value >> 8),
		            (byte)value};
		}

	}


