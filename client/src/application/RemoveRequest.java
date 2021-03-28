package application;

//Remove Request Message.
public class RemoveRequest implements RequestMessage {

    private String cid;
    //Constructor of Remove Request Message.
    RemoveRequest(String id) {
        this.cid = id;
    }
    //Marshal data to byte stream.
    @Override
    public byte[] Marshal(int invocation) {

        byte[] payload = cid.getBytes();
        byte[] header = Header.CreateRemoveBookingHeader(payload.length, invocation);
        byte[] finalPayload = Helper.ConcatByteArray(header, payload);

        return finalPayload;
    }

}

