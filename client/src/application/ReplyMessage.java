package application;
//Reply Message. 
public class ReplyMessage {
	
	private String type;
	private byte[] payload;
	
	//Constructor
	ReplyMessage(String t, byte[] pl){
		this.setType(t);
		this.setPayload(pl);
		
	}

	//Method to get type of service in message.
	public String getType() {
		return type;
	}

	//Method to set the type of serice in message.
	public void setType(String type) {
		this.type = type;
	}

	//Method to get the Payload.
	public byte[] getPayload() {
		return payload;
	}

	//Method to set the payload for the services.
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
