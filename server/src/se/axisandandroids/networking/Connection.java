package se.axisandandroids.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Connection supplies high level networking to send and receive-threads.
 * Send-methods are only used by ONE SendThread and receive-methods are
 * only used by ONE receive thread by design.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class Connection {

	private String host;
	private int port;
	private static int id;
	private int myId;
	private Socket sock;		

	// Input and output should be independent!
	// Design => one sender and one receiver => thread safe in that regard
	// Alternative is to wrap to private monitors or synchronize on the streams.

	private InputStream is;									
	private OutputStream os;

	private final byte[] sendintbuffer = new byte[4];
	private final byte[] readintbuffer = new byte[4]; 


	/**
	 * Create a new connection
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Connection(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	/**
	 * Creates a new connection at the specified socket
	 * @param sock The socket at which to create a connection.
	 */
	public Connection(Socket sock) {
		this.sock = sock;	
		myId = Connection.id++;
		try {
			sock.setTcpNoDelay(true);
		} catch (SocketException e) {
			System.err.println("Argh! socket slained without delay.");
			e.printStackTrace();
			System.exit(1);
		}
		connect();
	}

	public int getId() {
		return myId;
	}
	/**
	 * Creates input and output streams on a socket.
	 */
	private void connect() {
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
		} catch (IOException e) {
			System.err.println("IO-error");
			e.printStackTrace();
			System.exit(1);
		}		
		System.out.printf("New Connection: %s\n", sock.getInetAddress().toString());
	}

	/**
	 * Close the connection and kill the socket.
	 */
	public void disconnect() {
		System.out.printf("Disconnected: %s\n", sock.getInetAddress().toString());
		try {
			System.out.println("Is null: " + (is == null) +", "+ (os == null) +", "+ (sock == null));			
			is.close();
			os.close();
			sock.close();
		} catch (IOException e) {
			System.err.println("IO-error");
			e.printStackTrace();
			System.exit(1);
		}		
		sock = null; // etc... // other threads
	}

	/** 
	 * Send an image.
	 * @param data A byte vector with the image-data
	 * @throws IOException
	 */
	public void sendImage(byte[] data) throws IOException {		
		sendInt(Protocol.COMMAND.IMAGE);
		sendInt(data.length);				
		//onWrite(data, 0, data.length);	
		os.write(data, 0, data.length);
		os.flush();
	}

	private void onWrite(byte[] data, int offset, int length) {
		try {
			os.write(data, offset, length);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Send an image.
	 * @param data A byte vector with data.
	 * @param a This is the vector index at which the image begins.
	 * @param b This is the vector index at which the image ends.
	 * @throws IOException
	 */
	public void sendImage(byte[] data, int a, int b) throws IOException {
		int len = b - a;		
		sendInt(Protocol.COMMAND.IMAGE);
		sendInt(len);	
		//onWrite(data, a, len);
		os.write(data, a, len);
		os.flush();
	}

	/**
	 * Receive an image.
	 * @param b A byte vector with data.
	 * @return The number of bytes read.
	 * @throws IOException
	 */
	public int recvImage(byte[] b) throws IOException {				
		int len = recvInt();				
		int status = 0;
		int bytes_read = 0;

		try {					
			while (bytes_read < len) {
				status = is.read(b, bytes_read, len - bytes_read);
				/* 	1) Blocking until data available. 
			 		2) -1 if EOF. 
			 		3) 0 if nothing read.			 */
				if (status > 0) {
					bytes_read += status;		
				}
			}
		} catch (IOException e) {
			System.err.println("IO-error");
			e.printStackTrace();
			System.exit(1);
		}		
		return bytes_read;
	}
	
	
	public void sendBytes(byte[] data, int a, int b) throws IOException {
		int len = b - a;		
		os.write(data, a, len);
		os.flush();
	}
	
	public int recvBytes(byte[] b, int len) throws IOException {				
		int status = 0;
		int bytes_read = 0;

		try {					
			while (bytes_read < len) {
				status = is.read(b, bytes_read, len - bytes_read);
				/* 	1) Blocking until data available. 
			 		2) -1 if EOF. 
			 		3) 0 if nothing read.			 */
				if (status > 0) {
					bytes_read += status;		
				}
			}
		} catch (IOException e) {
			System.err.println("IO-error");
			e.printStackTrace();
			System.exit(1);
		}		
		return bytes_read;
	}
	
	/**
	 * Send display mode
	 * @param disp_mode An integer that specifies the display mode.
	 * @throws IOException
	 */
	public void sendDisplayMode(int disp_mode) throws IOException {
		sendInt(Protocol.COMMAND.DISP_MODE);
		sendInt(disp_mode);
	}
	/**
	 * Receive display mode.
	 * @return An integer specifies the display mode.
	 * @throws IOException
	 */
	public int recvDisplayMode() throws IOException { 		
		return recvInt();
	}

	/**
	 * Send sync mode.
	 * @param sync_mode An integer that specifies the sync mode.
	 * @throws IOException
	 */
	public void sendSyncMode(int sync_mode) throws IOException {
		sendInt(Protocol.COMMAND.SYNC_MODE);
		sendInt(sync_mode);
	}
	/**
	 * Receive sync mode.
	 * @return An integer specifies the sync mode.
	 * @throws IOException
	 */
	public int recvSyncMode() throws IOException { 
		return recvInt();
	}
	/**
	 * Sends an integer to the buffer. This is used to send the sync and display modes.
	 * @param nbr The integer that specifies the mode.
	 * @throws IOException
	 */
	public void sendInt(int nbr) throws IOException {
		sendintbuffer[0] = (byte) ( (nbr & 0xff000000) >> 24 );
		sendintbuffer[1] = (byte) ( (nbr & 0x00ff0000) >> 16 );
		sendintbuffer[2] = (byte) ( (nbr & 0x0000ff00) >> 8	 );
		sendintbuffer[3] = (byte) ( (nbr & 0x000000ff) 		 );
		onWrite(sendintbuffer, 0, sendintbuffer.length);
		os.flush();

		/*	os.write( (nbr & 0xff000000) >> 24 	);
		os.write( (nbr & 0x00ff0000) >> 16 	);
		os.write( (nbr & 0x0000ff00) >> 8	);
		os.write( (nbr & 0x000000ff) 		);
		os.flush();								*/
	}
	/**
	 * Reads an integer from the buffer. This is used to receive the sync and display modes.
	 * @return The integer that specifies the mode. 
	 * @throws IOException
	 */
	public int recvInt() throws IOException {	
		int status = 0;
		int bytes_read = 0;

		while(bytes_read < 4) {
			status = is.read(readintbuffer, bytes_read, 4 - bytes_read);
			/* 	1) Blocking until data available. 
			 	2) -1 if EOF. 
			 	3) 0 if nothing read.			 */
			if (status > 0) {
				bytes_read += status;		
			}		
		} 

		return ( ( (int)readintbuffer[0]) << 24 ) & 0xff000000 | 
				( ( (int)readintbuffer[1]) << 16 ) & 0x00ff0000 | 
				( ( (int)readintbuffer[2]) << 8  ) & 0x0000ff00 | 
				(   (int)readintbuffer[3]		  & 0x000000ff ); 

		/*
		// Non-Blocking, return -1 on fail...
		int b0 = is.read();
		int b1 = is.read();
		int b2 = is.read();
		int b3 = is.read();
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
		 */			
	}		
	/**
	 * 
	 * @return A string with the host name
	 */
	public String getHost() { 
		return host; 
	}
	/**
	 * 
	 * @return An integer with the port number.	
	 */
	public int getPort() { 
		return port; 
	}

	@Override 
	public String toString() {
		return host +":"+port;
	}

	public boolean isConnected() {
		return sock != null && !sock.isClosed() && sock.isConnected(); 
	}
}
