package application;


// booker name length (1 byte) | booker name (x bytes) | start date/time (7 bytes) | end date/time (7 bytes) | facility name length (1 byte) | facility name (x bytes)
public class BookingRequest implements RequestMessage{
	
	private String name,facname;
	private Date sdate,edate;
	

	BookingRequest(String n, String f, Date s, Date e){
		this.name = n;
		this.facname = f;
		this.sdate = s;
		this.edate = e;
	}
	
	
	@Override
	public byte[] Marshal(int invocation) throws Exception {

		Integer nameLength = name.length();
		Integer facLength = facname.length();
		
		// Fac name should be at most 255 characters
		if (facLength > 255) {
			throw new Exception("Facility name contain too many characters (Max: 255 chars)");
		}
		
		byte[] payload = Helper.ConcatByteArray(new byte[] {nameLength.byteValue()},name.getBytes());
		payload = Helper.ConcatByteArray(payload,sdate.getBytes());
		payload = Helper.ConcatByteArray(payload,edate.getBytes());
		payload = Helper.ConcatByteArray(payload,new byte[] {facLength.byteValue()});
		payload = Helper.ConcatByteArray(payload,facname.getBytes());
		
		byte[] header = Header.CreateAddBookingHeader(payload.length, invocation);
		
		byte[] finalPayload = Helper.ConcatByteArray(header,payload);
		
		return finalPayload;
	}

}
