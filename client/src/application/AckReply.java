package application;

public class AckReply implements RequestMessage{
	
	@Override
	public byte[] Marshal(int invocation) {
		//byte[] payload = Helper.intToBytes(1337);
		return Header.CreateAckHeader(0, invocation);
	}

}
