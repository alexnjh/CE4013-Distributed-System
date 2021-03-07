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
    public byte[] Marshal() {

        byte[] payload = Helper.longToBytes(duration);
        payload = Helper.ConcatByteArray(payload, facname.getBytes());
        byte[] header = Header.CreateMonitorBookingHeader(payload.length);
        byte[] finalPayload = Helper.ConcatByteArray(header, payload);

        return finalPayload;
    }

}


