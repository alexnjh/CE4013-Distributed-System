package application;
//An interface to Marshal data to byte stream.
public interface RequestMessage {
	
		public byte[] Marshal(int invocation) throws Exception;
		
		
}
