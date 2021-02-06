import java.nio.charset.StandardCharsets;
import java.util.Arrays;


//
// Mainly for marshalling and unmarshalling messages
//

public class Utilities {

	public static byte[] Marshal(Request req) {
		
		Integer typeLength = req.getType().length();	
		Integer nameLength = req.getName().length();
		Integer datesLength = req.getDates().length;	
		
		
		byte[] data = concatByteArray(new byte[]{typeLength.byteValue()}, req.getType().getBytes());
		data = concatByteArray(data, new byte[]{nameLength.byteValue()});
		data = concatByteArray(data, req.getName().getBytes());
		
		Date[] dates = req.getDates();
		
		for (int i = 0; i < datesLength; i++) {
			
			Integer day = dates[i].getDay().ordinal();	
			Integer hour = dates[i].getHour();
			Integer minute = dates[i].getMinute();
					
			
			byte[] temp = new byte[] {day.byteValue(),hour.byteValue(),minute.byteValue()};
			data = concatByteArray(data, temp);		
		}
		
		return data;
		
	}
	
	public static Reply Unmarshal(byte[] data) {
		
		
		int typeLen=data[0];
		byte[] partA = Arrays.copyOfRange(data, 1, typeLen+1);
		byte[] partB = Arrays.copyOfRange(data, typeLen+1, data.length);
		String repType = new String(partA, StandardCharsets.UTF_8);
		
		DateRange[] dRange = new DateRange[partB.length/6];
		
		for (int i = 0, j = 0; i < partB.length; i=i+6, j++) {
			
			int startd = partB[i];
			int starth = partB[i+1];
			int startm = partB[i+2];			
			int endd = partB[i+3];			
			int endh = partB[i+4];		
			int endn = partB[i+5];
			
			dRange[j] = new DateRange(new Date(Day.values()[startd],starth,startm),new Date(Day.values()[endd],endh,endn));
		}
		
		return new Reply(repType,dRange);
		
	}
	
	
	private static byte[] concatByteArray(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	
}
