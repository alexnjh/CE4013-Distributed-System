package application;

public class MonitorRequest implements RequestMessage {

    // duration (8 bytes) | facility (x bytes)
    private String facname;
    private long duration;


    MonitorRequest(String f, long duration) {
        this.facname = f;
        this.duration = duration;
    }


    @Override
    public byte[] Marshal(int invocation) {

        byte[] payload = Helper.longToBytes(duration);
        payload = Helper.ConcatByteArray(payload, facname.getBytes());
        byte[] header = Header.CreateMonitorBookingHeader(payload.length,invocation);
        byte[] finalPayload = Helper.ConcatByteArray(header, payload);

        return finalPayload;
    }

}


