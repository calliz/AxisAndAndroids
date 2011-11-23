package se.axisandandroids.client.service.networking;

import java.io.IOException;
import java.util.ArrayList;
import se.axisandandroids.client.CameraTunnel;
import se.axisandandroids.networking.Connection;


public class ConnectionHandler {

	private ArrayList<CameraTunnel> tunnels;
	
	
	public ConnectionHandler() {
		tunnels = new ArrayList<CameraTunnel>();
	}
	
	public void add(Connection c) {		
		CameraTunnel ct = new CameraTunnel(c);
		tunnels.add(ct);	
	}
	
	public void remove(int id) {
		disconnect(id);
		tunnels.remove(id);
	}	
	
	public void disconnect(int id) {
		CameraTunnel c = tunnels.get(id);
		try {
			c.connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
