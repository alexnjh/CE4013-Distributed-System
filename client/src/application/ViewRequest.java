package application;

public class ViewRequest{

	private String id;
	

	ViewRequest(String id){
			this.id = id;
	}
	
	
	public byte[] Marshal() {

		Integer idLength = id.length();
		
		byte[] payload = Helper.ConcatByteArray(new byte[] {idLength.byteValue()},id.getBytes());
		
//		payload = Helper.ConcatByteArray(payload,new byte[] {facLength.byteValue()});
//		payload = Helper.ConcatByteArray(payload,facname.getBytes());
//		
		byte[] header = Header.CreateAddBookingHeader(payload.length);
		
		byte[] finalPayload = Helper.ConcatByteArray(header,payload);
		
		return finalPayload;
	}

}
