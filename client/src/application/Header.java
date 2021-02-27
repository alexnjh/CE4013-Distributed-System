package application;

public class Header {

	public static byte[] CreateAddBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	
}
