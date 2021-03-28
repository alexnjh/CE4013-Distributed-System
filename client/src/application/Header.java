package application;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Header {
	//Create Add Booking Header with invocation and date.
	public static byte[] CreateAddBookingHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+11)+"0A416464426F6F6B696E67";
		return Helper.hexStringToByteArray(header);
	}
	//Create View Booking Header with invocation and date.
	public static byte[] CreateViewBookingHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+12)+"0B56696577426F6F6b696E67";
		return Helper.hexStringToByteArray(header);
	}
	//Create Query Availibility Header with invocation and date .
	public static byte[] CreateQueryAvailabilityHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+18)+"115175657279417661696c6162696c697479";
		return Helper.hexStringToByteArray(header);
	}
	////Create Remove Booking Header with invocation and date.
	public static byte[] CreateRemoveBookingHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+14)+"0D44656c657465426f6f6b696e67";
		return Helper.hexStringToByteArray(header);
	}
	
	//Create Monitoring Booking Header with invocation and date.
	public static byte[] CreateMonitorBookingHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+13)+"0C53746172744d6f6e69746f72";
		return Helper.hexStringToByteArray(header);
	}
	//Update Booking Headerwith invocation and date.
	public static byte[] CreateUpdateBookingHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+14)+"0D557064617465426f6f6b696e67";
		return Helper.hexStringToByteArray(header);
	}
	//Update Booking Duration Header with invocation and date. Duration is to increase/decrease the timings.
	public static byte[] CreateUpdateDurationHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+15)+"0E5570646174654475726174696f6e";
		System.out.println(header);
		return Helper.hexStringToByteArray(header);
	}
	//Create Acknowledgement Header for the Monitoring service with invocation and date.
	public static byte[] CreateAckHeader(int datalength, int invocation) {
		String header = String.format("%04X", invocation)+Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))+String.format("%04X", datalength+7)+"0641636b4d6f6e";
		return Helper.hexStringToByteArray(header);
	}
	
	
	
}
