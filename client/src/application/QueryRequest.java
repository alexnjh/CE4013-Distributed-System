package application;

public class QueryRequest {
	
	private boolean[] day;
	private String facname;
	
	public QueryRequest(boolean[] day, String facname) throws Exception {
		
		
		if (day.length > 7 || day.length < 1) {
			throw new Exception("Invalid day array length");
		}
		
		this.day = day;
		this.facname = facname;
	}
	
	public byte[] Marshal(int invocation) throws Exception {
		
		Integer facLength = facname.length();
		
		// Fac name should be at most 255 characters
		if (facLength > 255) {
			throw new Exception("Facility name contain too many characters (Max: 255 chars)");
		}
		
		
		byte[] payload = Helper.ConcatByteArray(new byte[] {facLength.byteValue()},facname.getBytes());
		
		byte d = 0;

		if (day[Day.MONDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.MONDAY.ordinal()));
		}
		
		if (day[Day.TUESDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.TUESDAY.ordinal()));
		}
		
		if (day[Day.WEDNESDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.WEDNESDAY.ordinal()));
		}
		
		if (day[Day.THURSDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.THURSDAY.ordinal()));
		}
		
		if (day[Day.FRIDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.FRIDAY.ordinal()));
		}
		
		if (day[Day.SATURDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.SATURDAY.ordinal()));
		}
		
		if (day[Day.SUNDAY.ordinal()]) {
			d = (byte) (d ^ (1 << Day.SUNDAY.ordinal()));
		}
		
		payload = Helper.ConcatByteArray(payload,new byte[] {d});
		byte[] header = Header.CreateQueryAvailabilityHeader(payload.length, invocation);
		byte[] finalPayload = Helper.ConcatByteArray(header,payload);
		return finalPayload;
		
	}
}
