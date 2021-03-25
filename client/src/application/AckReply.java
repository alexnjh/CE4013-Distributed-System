package application;
//Send Acknowledgement reply to server
public class AckReply implements RequestMessage{
	//Marshal data to byte stream.
	@Override
	public byte[] Marshal(int invocation) {
		//byte[] payload = Helper.intToBytes(1337);
		return Header.CreateAckHeader(0, invocation);
	}

}
