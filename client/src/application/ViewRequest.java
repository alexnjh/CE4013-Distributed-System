package application;

public class ViewRequest{

	private String id;
	
	public ViewRequest(String conid) {
		this.id = conid;
	}

	public byte[] Marshal() {
		byte[] payload = id.getBytes();
		byte[] header = Header.CreateViewBookingHeader(payload.length);
		byte[] finalPayload = Helper.ConcatByteArray(header,payload);
		return finalPayload;
	}

}
