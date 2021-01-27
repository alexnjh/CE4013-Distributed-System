
public class Request {

	private String type;
	private String name;
	private Date[] dates;
	
	Request(String type, String name, Date[] dates){
		this.setType(type);
		this.setName(name);
		this.setDates(dates);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date[] getDates() {
		return dates;
	}

	public void setDates(Date[] dates) {
		this.dates = dates;
	}
	
}

