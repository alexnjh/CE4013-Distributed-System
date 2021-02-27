package application;

public class ReplyMessage {
	
	private String type;
	private byte[] payload;
	
	
	ReplyMessage(String t, byte[] pl){
		this.setType(t);
		this.setPayload(pl);
		
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public byte[] getPayload() {
		return payload;
	}


	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
