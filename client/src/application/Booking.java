package application;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Booking {
	//variables to store the username, facility name , confirmation id , start date and end date.
	private String name,facname,confID;
	private Date sdate,edate;
	
	//Converting the data of the bookername, start/end date and facility and confirmation id into bytes
	Booking(byte[] data){
		
		int nameLen = data[0];
		byte[] bookername = Arrays.copyOfRange(data, 1, 1+nameLen);
		
		// 3 bytes for start date
		byte[] sd = Arrays.copyOfRange(data, 1+nameLen, 4+nameLen);
		
		// 3 bytes for end date
		byte[] ed = Arrays.copyOfRange(data, 4+nameLen, 7+nameLen);
		
		// Get facility name
		int facLen = data[7+nameLen];
		byte[] facname = Arrays.copyOfRange(data, 8+nameLen, 8+nameLen+facLen);
		
		// Get conf ID
		byte[] confID = Arrays.copyOfRange(data, 8+nameLen+facLen, data.length);
		
		
		this.setName(new String(bookername, StandardCharsets.UTF_8));
		this.setFacname(new String(facname, StandardCharsets.UTF_8));
		this.setConfID(new String(confID, StandardCharsets.UTF_8));
		this.setSdate(new Date(
				Day.values()[Byte.toUnsignedInt(sd[0])],
				Byte.toUnsignedInt(sd[1]),
				Byte.toUnsignedInt(sd[2])));
		this.setEdate(new Date(
				Day.values()[Byte.toUnsignedInt(ed[0])],
				Byte.toUnsignedInt(ed[1]),
				Byte.toUnsignedInt(ed[2])));		
	}

	//Method of getting the name and storing it
	public String getName() {
		return name;
	}

	//Method to set the username
	public void setName(String name) {
		this.name = name;
	}

	// Method to get the end date
	public Date getEdate() {
		return edate;
	}

	// Method to set the end date
	public void setEdate(Date edate) {
		this.edate = edate;
	}

	//getting the confirmation id from the server.
	public String getConfID() {
		return confID;
	}

	//Method setting the confirmation id.
	public void setConfID(String confID) {
		this.confID = confID;
	}

	//Method to get the facility name.
	public String getFacname() {
		return facname;
	}

	//Method to set the facility name.
	public void setFacname(String facname) {
		this.facname = facname;
	}

	//Method to get the start date.
	public Date getSdate() {
		return sdate;
	}

	//Method to set the start date.
	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}
	
}
