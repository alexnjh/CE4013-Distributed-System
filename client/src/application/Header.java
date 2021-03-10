package application;

public class Header {

	public static byte[] CreateAddBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] CreateViewBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+12)+"0B56696577426F6F6b696E67";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] CreateQueryAvailabilityHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+18)+"115175657279417661696c6162696c697479";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] CreateRemoveBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+14)+"0D44656c657465426f6f6b696e67";
		return Helper.hexStringToByteArray(header);
	}
	
	
	public static byte[] CreateMonitorBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+13)+"0C53746172744d6f6e69746f72";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] CreateUpdateBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+14)+"0D557064617465426f6f6b696e67";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] CreateUpdateDurationHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+15)+"0E5570646174654475726174696f6e";
		System.out.println(header);
		return Helper.hexStringToByteArray(header);
	}
	
}
