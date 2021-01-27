import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Client {
	
	 public static void main(String[] argv) throws UnknownHostException, MalformedURLException {
		 
		 
		 // Ask user for server IP address and port
		 if (argv.length != 2) {
			 System.out.println("Please enter a server address and port number eg. \"java client 127.0.0.1 2222\"");
			 return;
		 }
		 
		 
		 //
		 // 1. Open UDP socket
		 //
		 
		 
		 String addr = argv[0];
		 int port = Integer.parseInt(argv[1]);
		 
		 DatagramSocket socket;
		 InetAddress ip = InetAddress.getByName(addr);  
		 byte[] buffer = new byte[1024];
		 
		 
		 // Marshal request message
		 byte[] data = Utilities.Marshal(new Request("Request","Meeting Room B",new Date[] {
				 new Date(Day.MONDAY,2,5),
				 new Date(Day.FRIDAY,22,5),
				 new Date(Day.THURSDAY,5,40),
				 }));
		 
		 
		 try {
			 
			 socket = new DatagramSocket();
		

			 try {
			 //
			 // 2. Send UDP request to server
			 //
			 DatagramPacket request = new DatagramPacket(data,data.length,ip,port);
			 socket.send(request);
			 System.out.println("Send packet");
			 //
			 // 3. Receive UDP reply from server
			 //
			 DatagramPacket reply = new DatagramPacket(buffer,buffer.length);
			 socket.receive(reply);
	
			 //
			 // 4. Unmarshal reply
			 //
			 byte[] repData = Arrays.copyOfRange(buffer, 0, reply.getLength());
			 Reply rep = Utilities.Unmarshal(repData);
			 
			 //
			 // 5. Print reply
			 //		
			 
			 System.out.println("\n\n");
			 System.out.println("[REPLY]");
			 
			 DateRange[] dr = rep.getDateranges();
			 
			 for (int i = 0; i < dr.length; i++) {
				 System.out.println(dr[i].getStart() + " -> " + dr[i].getEnd());							
			 }
			 
			 
			 } catch (IOException e) {
				 
			 }
		 
		 
		 } catch (SocketException e) {
			 
		 }
		 
	}

}
