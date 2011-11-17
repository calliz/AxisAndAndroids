package se.axisandandroids.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import se.lth.cs.fakecamera.Axis211A;



public class Connection {
	
	private Socket sock;	
	private InputStream is;			// input and output should be independent
	private OutputStream os;
	private BufferedReader in;
	private PrintWriter out;
	
	public static enum COMMANDS { IMAGE, SYNC_MODE, DISP_MODE, CONNECTED, END_MSG }
	public static enum SYNC_MODE { AUTO, SYNC, ASYNC }
	public static enum DISP_MODE { AUTO, IDLE, MOVIE }
	
		
	public Connection(Socket sock) {
		this.sock = sock;		
		connect();
	}
	
	public void connect(Socket sock) {
		this.sock = sock;
		connect();
	}
	
	private void connect() {
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
			out = new PrintWriter(os, true);
			in = new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}		
		System.out.printf("New Connection: %s\n", sock.getInetAddress().toString());
	}
	
	
	public void disconnect() throws IOException {
		System.out.printf("Disconnected: %s\n", sock.getInetAddress().toString());
		// Disconnect Message ???
		sock.close();
		sock = null; // etc...
	}
	
		
	public void sendImage(byte[] data) {		
		try {
			os.write(data, 0, data.length);			
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}			
	}
	
	public byte[] recvImage() {
		
		// Read to a buffer array instead ???
		
		byte[] b = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		int len;
		
		try {
			len = is.read(b, 0, Axis211A.IMAGE_BUFFER_SIZE);			
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}			
		
		return null; // note
	}
	
	
	public void sendDisplayMode(int disp_mode) {
		sendInt(disp_mode); // Sanity Check ?
	}
	
	public int recvDisplayMode() throws IOException { 		
		return recvInt(); // Sanity Check ?
	}
	
	public void sendSyncMode(int sync_mode) {
		sendInt(sync_mode); // Sanity Check ?
	}
	
	public int recvSyncMode() throws IOException { 
		return recvInt();  // Sanity Check ?
	}
				
	public void sendInt(int nbr) {
		/*
		char[] b = new char[4];		
		b[0] = (char) ((nbr >> 24) & 0xFF);
		b[1] = (char) ((nbr >> 16) & 0xFF);
		b[2] = (char) ((nbr >> 8)  & 0xFF);
		b[3] = (char) ( nbr 	   & 0xFF);
		out.print(b);
		out.flush();
		*/
		
		out.write((nbr >> 24) & 0xFF);
		out.write((nbr >> 16) & 0xFF);
		out.write((nbr >> 8)  & 0xFF);
		out.write( nbr 	   & 0xFF);
		out.flush();
	}
	
	public int recvInt() throws IOException {
		/*
		char[] b = new char[4];
		int status = in.read(b, 0, b.length);
		if (status == -1) {
			System.err.println("IO-error - reached EOF.");
		}
		return b[0] << 24 | b[1] << 16 | b[2] << 8 | b[3];
		*/
		
		int b0 = in.read();
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
	}
	
}
