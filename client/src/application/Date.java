package application;

public class Date {
	private Day day;
	private int hour;
	private int minute;
	
	//constructor of the Date to use the day, hour minute.
	Date(Day day, int hour, int min){
		this.day = day;
		this.hour = hour;
		this.minute = min;
	}
	//Method to get the Day. E.g Monday,Tuesday
	public Day getDay() {
		return day;
	}
	//Method to set the Day.
	public void setDay(Day day) {
		this.day = day;
	}
	//Method to get the hour.
	public int getHour() {
		return hour;
	}
	//Method to set the hour.
	public void setHour(int hour) {
		this.hour = hour;
	}
	//Method to get the Minutes
	public int getMinute() {
		return minute;
	}
	//Method to set the Minutes
	public void setMinute(int minute) {
		this.minute = minute;
	}
	//Saving the day, hour, minutes into string bytes.
	public byte[] getBytes() {
		
		Integer d = day.ordinal();	
		Integer h = hour;
		Integer m = minute;
		
		return new byte[] {d.byteValue(),h.byteValue(),m.byteValue()};
		
	}
	//Method to convert the day, hour, minute to string.
	 @Override
	 public String toString() { 
	     return String.format("%s, %d:%02d",day,hour,minute); 
	 } 
	 
	 //Method of converting to string without stating the day.
	 public String toStringWithoutDay() { 
	     return String.format("%d:%02d",hour,minute); 
	 } 
}
