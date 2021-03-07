package application;

//sent the confirmation id to the server
public class RemoveRequest implements RequestMessage {

    private String cid;

    RemoveRequest(String id) {
        this.cid = id;
    }

    @Override
    public byte[] Marshal() {

        byte[] payload = cid.getBytes();
        byte[] header = Header.CreateRemoveBookingHeader(payload.length);
        byte[] finalPayload = Helper.ConcatByteArray(header, payload);

        return finalPayload;
    }

}

