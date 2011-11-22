package se.axisandandroids.client.service;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CtrlService extends android.app.Service {
	private static final String TAG = CtrlService.class.getSimpleName();
	private final IBinder mBinder = new LocalBinder();
	private static List<Socket> sockets;
	
	public class LocalBinder extends Binder {
		public CtrlService getService() {
			return CtrlService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "CtrlService Bound", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "CtrlService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		
		sockets = new ArrayList<Socket>();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "CtrlService Destroyed", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
	
	/* public methods for client */
	public void setSockets(List<Socket> sockets) {
		CtrlService.sockets = sockets;
	}
	
	public void addSocket(Socket socket) {
			sockets.add(socket);
	}
	
	public void addSocket(String hostname, int port) {
		try {
			sockets.add(new Socket(hostname, port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeSocket(Socket socket) {
		if (sockets.contains(socket))
			sockets.remove(socket);
	}
}