package application;

public class UpdateDurationRequest implements RequestMessage{
	private String id;
	private Integer offset;		

	UpdateDurationRequest(String id, int offset){
		this.id = id;
		this.offset = offset;
	}
	
	
	@Override
	public byte[] Marshal(int invocation) {
				
				
				byte[] payload = Helper.intToBytes(offset);
				
				payload = Helper.ConcatByteArray(payload,id.getBytes());
				
				byte[] header = Header.CreateUpdateDurationHeader(payload.length, invocation);
				
				byte[] finalPayload = Helper.ConcatByteArray(header,payload);
				
				return finalPayload;
			}

}
