package application;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Connection {
	
	private String hostname;
	private int portnumber;
	
	// Default constructor
	Connection(String host, int port){
		this.hostname = host;
		this.portnumber = port;
	}
	
	// Send Message
	public ReplyMessage sendMessage(byte[] data) throws IOException{
		
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
		
		// Close the socket
		socket.close();
		return unpack(raw);

	}

	/**
	 * Listen to socket for 5 seconds
	 * throws {@link SocketTimeoutException} if no reply after 5 seconds. This is not an error
	 * @return reply message
	 * @throws IOException I/O error
	 */
	public ReplyMessage listen() throws IOException {
		DatagramSocket socket = new DatagramSocket();

		byte[] buffer = new byte[1024];

		// Listen
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		socket.setSoTimeout(5000); // 5 seconds timeout

		socket.receive(reply);
		// Unpack the payload
		byte[] raw = Arrays.copyOfRange(buffer, 0, reply.getLength());

		// Close the socket
		socket.close();
		return unpack(raw);
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
