package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.networking.Connection;


public class ConnectionHandlerStatic {

	private CameraTunnel[] tunnels;
	private int nconnected_cameras;
	private DisplayMonitor disp_monitor;
	
	public ConnectionHandlerStatic(DisplayMonitor disp_monitor, int MAXCAMERAS) {
		this.disp_monitor = disp_monitor;
		tunnels = new CameraTunnel[MAXCAMERAS];
		nconnected_cameras = 0;
	}
	
	public void add(CameraTunnel tunnel) {		
		if (nconnected_cameras == tunnels.length) return;		
		tunnels[nconnected_cameras] = tunnel;
		++nconnected_cameras;
	}
		
	public void remove(int id) {
		disconnect(id);
		tunnels[id] = null;			// Consequences? stop threads, null pointers, etc. ?
		--nconnected_cameras;
	}	
	
	public void disconnect(int id) {
		CameraTunnel c = tunnels[id];
		try {
			c.connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}