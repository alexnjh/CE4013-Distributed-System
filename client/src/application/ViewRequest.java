package application;

public class ViewRequest{

	private String id;
	
	public ViewRequest(String conid) {
		this.id = conid;
	}

	public byte[] Marshal(int invocation) {
		byte[] payload = id.getBytes();
		byte[] header = Header.CreateViewBookingHeader(payload.length, invocation);
		byte[] finalPayload = Helper.ConcatByteArray(header,payload);
		return finalPayload;
	}

}
