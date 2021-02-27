package application;

public class Date {
	private Day day;
	private int hour;
	private int minute;
	
		
	Date(Day day, int hour, int min){
		this.day = day;
		this.hour = hour;
		this.minute = min;
	}

	public Day getDay() {
		return day;
	}
	public void setDay(Day day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public byte[] getBytes() {
		
		Integer d = day.ordinal();	
		Integer h = hour;
		Integer m = minute;
		
		return new byte[] {d.byteValue(),h.byteValue(),m.byteValue()};
		
	}

	 @Override
	 public String toString() { 
	     return String.format(day + "/" + hour + "/" + minute); 
	 } 
}
