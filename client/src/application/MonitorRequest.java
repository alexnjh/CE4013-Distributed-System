package application;
//Monitor Request Message.
public class MonitorRequest implements RequestMessage {

    // duration (8 bytes) | facility (x bytes)
    private String facname;
    private long duration;

    //Constructor
    MonitorRequest(String f, long duration) {
        this.facname = f;
        this.duration = duration;
    }

    //Marshal data to byte stream.
    @Override
    public byte[] Marshal(int invocation) throws Exception {

		// Fac name should be at most 255 characters
		if (facname.length() > 255) {
			throw new Exception("Facility name contain too many characters (Max: 255 chars)");
		}
    	
        byte[] payload = Helper.longToBytes(duration);
        payload = Helper.ConcatByteArray(payload, facname.getBytes());
        byte[] header = Header.CreateMonitorBookingHeader(payload.length,invocation);
        byte[] finalPayload = Helper.ConcatByteArray(header, payload);

        return finalPayload;
    }

}


