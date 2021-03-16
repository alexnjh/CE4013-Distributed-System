package application;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Connection {
	
	private String hostname;
	private int portnumber;
	
	// Default constructor
	Connection(String host, int port){
		this.hostname = host;
		this.portnumber = port;
	}
	
	// Send Message
	public ReplyMessage sendMessage(byte[] data) throws Exception{
		
		
		for (int i = 0; i < 5; i++){        // recieve data until timeout
            try {
            	
        		System.out.println(Helper.bytesToHex(data));
        		System.out.println(Helper.encryptThisToSHA1(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
        		
        		DatagramSocket socket;

        		InetAddress ip = InetAddress.getByName(hostname);
        			
        			
        		// This receive the return message from the server
        		byte[] buffer = new byte[1024];
        			
        		// Send datagram
        		socket = new DatagramSocket();
        		DatagramPacket request = new DatagramPacket(data,data.length,ip,portnumber);
        		socket.send(request);			
        		
        		// Set time out of 1 sec
        		socket.setSoTimeout(500);
        		
        		// Receive reply
        		DatagramPacket reply = new DatagramPacket(buffer,buffer.length);
        		socket.receive(reply);
        		
        		// Unpack the payload
        		byte[] raw = Arrays.copyOfRange(buffer, 0, reply.getLength());
        		
        		// Close the socket
        		socket.close();
        		return unpack(raw);
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("Timeout reached!!! Retrying again");
            }
        }
		
		throw new Exception("Failed to send packet");
		
		
			
	}

	public Object[] sendMessageRetPort(byte[] data) throws IOException{
		
		DatagramSocket socket;

		InetAddress ip = InetAddress.getByName(hostname);


		// This receive the return message from the server
		byte[] buffer = new byte[1024];

		// Send datagram
		socket = new DatagramSocket();
		DatagramPacket request = new DatagramPacket(data,data.length,ip,portnumber);
		socket.send(request);


		// Receive reply
		DatagramPacket reply = new DatagramPacket(buffer,buffer.length);
		socket.receive(reply);

		// Unpack the payload
		byte[] raw = Arrays.copyOfRange(buffer, 0, reply.getLength());

		// Generate out
		Object[] ret = new Object[2];
		ret[0] = unpack(raw);
		ret[1] = socket.getLocalPort();

		// Close the socket
		socket.close();

		return ret;
	}

	/**
	 * Listen to socket for 5 seconds
	 * throws {@link SocketTimeoutException} if no reply after 5 seconds. This is not an error
	 * @return reply message
	 * @throws IOException I/O error
	 */
	public ReplyMessage listen(int port) throws IOException {
		System.out.println("Listening on " + port);
		DatagramSocket socket = new DatagramSocket(port);

		byte[] buffer = new byte[1024];

		// Listen
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		socket.setSoTimeout(5000); // 5 seconds timeout

		try {
			socket.receive(reply);
			// Unpack the payload
			byte[] raw = Arrays.copyOfRange(buffer, 0, reply.getLength());
			return unpack(raw);
		} catch (IOException e) {
			throw e;
		} finally {
			// Close the socket
			socket.close();
		}

	}
	
	private ReplyMessage unpack(byte[] data) {
		
		int typeLen = data[4];
		System.out.println(Arrays.toString(data));
		byte[] partA = Arrays.copyOfRange(data, 5, 5+typeLen);
		byte[] partB = Arrays.copyOfRange(data, 5+typeLen, data.length);
		System.out.println(Helper.bytesToHex(partB));
		return new ReplyMessage(new String(partA, StandardCharsets.UTF_8), partB);
	}
		
}
