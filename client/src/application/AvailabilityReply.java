package application;

import javafx.scene.control.TitledPane;

public class AvailabilityReply {
	private DateRange[][] list = new DateRange[7][];
	
	public AvailabilityReply(byte[] data) {
		
		
		int index = 0;
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
