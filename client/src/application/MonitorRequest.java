package application;

public class MonitorRequest implements RequestMessage{

	// booker name length (1 byte) | booker name (x bytes) | start date/time (7 bytes) | end date/time (7 bytes) | facility name length (1 byte) | facility name (x bytes)
		private String name,facname;
		private Date sdate,edate;
		

		MonitorRequest(String n, String f, Date s, Date e){
			this.name = n;
			this.facname = f;
			this.sdate = s;
			this.edate = e;
		}
		
		
		@Override
		public byte[] Marshal() {

			Integer nameLength = name.length();
			Integer facLength = facname.length();
			
			byte[] payload = Helper.ConcatByteArray(new byte[] {nameLength.byteValue()},name.getBytes());
			payload = Helper.ConcatByteArray(payload,sdate.getBytes());
			payload = Helper.ConcatByteArray(payload,edate.getBytes());
			payload = Helper.ConcatByteArray(payload,new byte[] {facLength.byteValue()});
			payload = Helper.ConcatByteArray(payload,facname.getBytes());
			
			byte[] header = Header.CreateMonitorBookingHeader(payload.length);
			
			byte[] finalPayload = Helper.ConcatByteArray(header,payload);
			
			return finalPayload;
		}

	}


