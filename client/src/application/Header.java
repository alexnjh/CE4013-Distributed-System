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
		String header = "0000"+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	
	
	public static byte[] CreateMonitorBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	
	public static byte[] UpdateBookingHeader(int datalength) {
		String header = "0000"+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	
}
