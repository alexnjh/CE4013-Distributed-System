package application;
public class DateRange {
	//Variable to store the start date and end date with  day,hour and minutes
	private Date start;
	private Date end;
	
	DateRange(Date start, Date end){
		this.setStart(start);
		this.setEnd(end);
	}
	//Method to get the Start Date
	public Date getStart() {
		return start;
	}
	//Method to set the Start Date
	public void setStart(Date start) {
		this.start = start;
	}
	//Method to get the End Date
	public Date getEnd() {
		return end;
	}
	//Method to set the End Date
	public void setEnd(Date end) {
		this.end = end;
	}
	//Method to return in string format.
	@Override
	public String toString() {
		return new String(start.toStringWithoutDay()+"->"+end.toStringWithoutDay());
	}

}
