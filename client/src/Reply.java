
public class Reply {
	
	private String type;
	private DateRange[] dateranges;
	
	Reply(String type, DateRange[] dater){
		this.setType(type);
		this.setDateranges(dater);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DateRange[] getDateranges() {
		return dateranges;
	}

	public void setDateranges(DateRange[] dateranges) {
		this.dateranges = dateranges;
	}
	
}
