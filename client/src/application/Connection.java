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
	
	/*
	 * Sent a message to the server.
	 * If a exception is thrown, this is because the server did not reply after retrying 10 times.
	 * @return ReplyException
	 * @throws Exception 
	 */
	public ReplyMessage sendMessage(byte[] data) throws Exception{


		for (int i = 0; i < 10; i++){        // recieve data until timeout
            try {

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

	/*
	 * Sent a monitoring request to the server.
	 * This is a special method only used by the client to inform the server
	 * on which address and port number to send the updates
	 * @return Object[]
	 * @throws IOException I/O error
	 */
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
	public ReplyMessage listen(int port, int invocation) throws IOException {
		System.out.println("Listening on " + port);
		DatagramSocket socket = new DatagramSocket(port);

		byte[] buffer = new byte[1024];

		// Listen
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		//socket.setSoTimeout(5000); // 5 seconds timeout

		try {
			socket.receive(reply);
			// Unpack the payload
			byte[] raw = Arrays.copyOfRange(buffer, 0, reply.getLength());

			// Reply ACK
			AckReply r = new AckReply();
			byte[] data = r.Marshal(invocation);
			InetAddress ip = InetAddress.getByName(hostname);
			DatagramPacket rep = new DatagramPacket(data,data.length,ip,portnumber);
			socket.send(rep);
			//sendMessage(data);
			System.out.println("Replied");
			return unpack(raw);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("Err replying");
			return null;
		} finally {
			// Close the socket
			socket.close();
		}

	}
	
	private ReplyMessage unpack(byte[] data) {
		
		int typeLen = data[24];
		System.out.println(Arrays.toString(data));
		byte[] partA = Arrays.copyOfRange(data, 25, 25+typeLen);
		byte[] partB = Arrays.copyOfRange(data, 25+typeLen, data.length);
		System.out.println(Helper.bytesToHex(partB));
		return new ReplyMessage(new String(partA, StandardCharsets.UTF_8), partB);
	}
		
}
