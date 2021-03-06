package se.axisandandroids.testconnection;

/* ---------------------------------------------------
 * Thread for handling ONE client given by socket at
 * construction.
 * 
 * --------------------------------------------------- */

import java.io.IOException;
import java.net.Socket;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;



/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ClientHandler extends Thread {
	Socket clientSocket;
	Axis211A axis;

	public ClientHandler(Socket clientSocket, Axis211A axis) {
		super();
		this.clientSocket = clientSocket;
		this.axis = axis;
	}

	public void run() {
		try {

			// *** --- CHANGE HERE WHAT TO RUN --- ***

			int testcase = 3;

			switch (testcase) {
			case 0: 
				testSendInt(clientSocket);
				break;
			case 1: 
				servConnectionTest(clientSocket);
				break;
			case 2:
				servFakeCam(clientSocket);
				break;
			case 3:
				servFakeCamInteractive(clientSocket);
				break;
			default:
				servConnectionTest(clientSocket);
				servFakeCam(clientSocket);
				servFakeCamInteractive(clientSocket);
			}



			// *** ------------------------------- ***

			clientSocket.close();
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}		
	}

	public void servConnectionTest(Socket sock) throws IOException {
		Connection con = new Connection(sock);

		// Test recvInt()
		System.out.println("\n** Receiving int...");
		int nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);

		// Test sendInt()
		System.out.println("\n** Sending int...");
		nbr = 123123;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);

		// Test recvSyncMode
		System.out.println("\n** Received SyncMode...");
		int cmd = con.recvInt();
		int mode = con.recvSyncMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert (cmd == Protocol.COMMAND.SYNC_MODE);
		assert (mode == Protocol.SYNC_MODE.AUTO);

		// Test recvDisplayMode
		System.out.println("\n** Received DisplayMode...");
		cmd = con.recvInt();
		mode = con.recvDisplayMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert (cmd == Protocol.COMMAND.DISP_MODE);
		assert (mode == Protocol.DISP_MODE.AUTO);


		// Test sendImage
		System.out.println("\n** Sending Image...");
		byte[] c = { 12,43,34,120,21,32,100,34 };				
		//con.sendImage(c);
		con.sendImage(c,0,c.length);


		// Test recvImage
		System.out.println("\n** Receiving Image...");
		cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		System.out.println("Command: " + cmd);			
		byte[] b = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		int len = con.recvImage(b);		
		System.out.printf("Length: %d\n", len);
		for (int i = 0; i < len; ++i) {
			System.out.printf("%d ", b[i]);
			assert(b[i] == c[i]);
		}
		System.out.println();
	}

	public void testSendInt(Socket socket) {
		System.out.println("**Test recv int");

		Connection con = new Connection(socket);
		int recv = -1;
		try {

			for (int i = -1000; i < 1000; ++i) {
				recv = con.recvInt();	
				System.out.printf("Got: %d\n", recv);
				assert(127*i == recv);
			}

			System.out.println("Done: **Test recv int");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}


	public void servFakeCam(Socket sock) throws IOException {
		Connection con = new Connection(sock);
		byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

		if (! axis.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
		}

		// Send image 1
		int len = axis.getJPEG(jpeg, 0);		
		assert(len <= jpeg.length);
		con.sendImage(jpeg, 0, len);
		System.out.println("Sent: " + len + " bytes.");

		try {
			Thread.sleep((long)1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Send image 2
		len = axis.getJPEG(jpeg, 0);		
		assert(len <= jpeg.length);
		con.sendImage(jpeg, 0, len);
		System.out.println("Sent: " + len + " bytes.");

		try {
			Thread.sleep((long)1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		
		// Send image 3
		len = axis.getJPEG(jpeg, 0);		
		assert(len <= jpeg.length);
		con.sendImage(jpeg, 0, len);
		System.out.println("Sent: " + len + " bytes.");
		
		try {
			Thread.sleep((long)1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Send image 4
		len = axis.getJPEG(jpeg, 0);		
		assert(len <= jpeg.length);
		con.sendImage(jpeg, 0, len);
		System.out.println("Sent: " + len + " bytes.");
		

		axis.close();
	}

	public void servFakeCamInteractive(Socket sock) throws IOException {
		Connection con = new Connection(sock);
		byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

		if (! axis.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
		}	

		while (!interrupted()) {

			int cmd;						

//			System.out.println("Waiting for Request");

			cmd = con.recvInt(); // ---> NON-BLOCKING => ERROR SRC

//			System.out.printf("Command: %d \n", cmd);

			switch (cmd) {			
			case Protocol.COMMAND.IMAGE:			
//				System.out.printf("%d. Image Requested", cmd);

//				System.out.println("Fetching from camera,");
				int len = axis.getJPEG(jpeg, 0);		

				//System.out.println("Sending Image...");
				con.sendImage(jpeg, 0, len);

				System.out.println("Sent: " + len + " bytes.");
				break;
			default:
				System.err.printf("Unknown Command %d\n", cmd);
				System.exit(1);
				break;
			}											

		}

		axis.close();		
	}
}
