package application;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Booking {
	
	private String name,facname,confID;
	private Date sdate,edate;
	

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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Date getEdate() {
		return edate;
	}


	public void setEdate(Date edate) {
		this.edate = edate;
	}


	public String getConfID() {
		return confID;
	}


	public void setConfID(String confID) {
		this.confID = confID;
	}


	public String getFacname() {
		return facname;
	}


	public void setFacname(String facname) {
		this.facname = facname;
	}


	public Date getSdate() {
		return sdate;
	}


	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}
	
}
