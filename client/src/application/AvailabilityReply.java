package application;

import javafx.scene.control.TitledPane;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
// Reply Message of Query Availibility service to return a list of date from server.
public class AvailabilityReply {
	private DateRange[][] list = new DateRange[7][];
	private String lastAct = null;
	
	public AvailabilityReply(byte[] data) {
		
		int index = 0;
		while (index  < data.length) {
			
			
			Day day = Day.values()[data[index]];
			int noOfElements = Helper.twoBytetoInt(data[index+1],data[index+2]);
			index += 3;
			
			list[day.ordinal()]= new DateRange[noOfElements];
			
			System.out.println("Data length is "+data.length);
			System.out.println("Number of elements for day "+day+" "+noOfElements);
			System.out.println("Current index is "+index);
			
			int x;
			for (x=0;x<noOfElements;x++) {
				
				Day startd = day;
				int starth = data[index+0];
				int startm = data[index+1];			
				Day endd = day;			
				int endh = data[index+2];		
				int endn = data[index+3];
				
				list[day.ordinal()][x] = new DateRange(new Date(startd,starth,startm),new Date(endd,endh,endn));
				
				index = index+4;
			}
			
			
		}
	}
	//Convert bytes to hexadecimal in a string.
	private static String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for(byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
	//Get the last action.
	public String getLastAct() {
		if (lastAct == null) return "";
		else return lastAct;
	}
	//Get the availiability reply from server.
	public AvailabilityReply(byte[] orig, boolean hasString) {
		// Get the string out first
		int stringLength = orig[0];
		int index = 1;
		byte[] actionString = Arrays.copyOfRange(orig, 1, stringLength+1);
		byte[] data = Arrays.copyOfRange(orig, stringLength+1, orig.length);

		System.out.println("Has String: " + hasString);

		System.out.println("=========");
		System.out.println("String Length: " + stringLength);
		System.out.println("Orig: " + bytesToHex(orig));
		System.out.println("Action: " + bytesToHex(actionString));
		System.out.println("Data: " + bytesToHex(data));
		System.out.println("=========");
		lastAct = new String(actionString, StandardCharsets.UTF_8);

		index = 0;
		while (index  < data.length) {

			Day day = Day.values()[data[index]];
			int noOfElements = data[index+1];
			index += 2;

			list[day.ordinal()]= new DateRange[noOfElements];

			System.out.println("Number of elements for day "+day+" "+noOfElements);
			System.out.println("Current index is "+index);

			int x;
			for (x=0;x<noOfElements;x++) {

				int startd = data[index];
				int starth = data[index+1];
				int startm = data[index+2];
				int endd = data[index+3];
				int endh = data[index+4];
				int endn = data[index+5];

				list[day.ordinal()][x] = new DateRange(new Date(Day.values()[startd],starth,startm),new Date(Day.values()[endd],endh,endn));

				index = index+6;
			}
		}

	}
	//Array of Date Range. 
	public DateRange[] getDateRanges(Day a) {
		return list[a.ordinal()];
	}
	
	public void print() {
		
		int x=0;
		
	    for(DateRange[] b : list) {
	    	System.out.println(x);
	    	if (b != null) {
			    for(DateRange c : b) {
			    	if (c != null)
			    		System.out.println(c);
			    }	
	    	}
	    	x++;
	    }
	}

}
