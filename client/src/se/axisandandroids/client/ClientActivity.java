package se.axisandandroids.client;

import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class ClientActivity extends Activity implements OnClickListener {
	private static final String TAG = ClientActivity.class.getSimpleName();

	private CtrlService mService;
	private boolean mBound;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.btnConnect)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.client_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_displays:
			Toast.makeText(this, "Displays", Toast.LENGTH_SHORT);
			onShowDisplays();
			return true;
		case R.id.menu_quit:
			Toast.makeText(this, "Quit", Toast.LENGTH_SHORT);
			onQuit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		TableLayout tl = (TableLayout) findViewById(R.id.tlConnections);

		LayoutInflater inflater = LayoutInflater.from(ClientActivity.this);
		View theInflatedView = inflater.inflate(R.layout.connection_item, tl);

		Button btnDisconnect = (Button) theInflatedView
				.findViewById(R.id.btnDisconnect);
		btnDisconnect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Test",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to CtrlService
		Intent intent = new Intent(this, CtrlService.class);
		boolean res = getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);
		Log.d(TAG, "" + res);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		try {
			unbindService(mConnection);
		} catch (java.lang.IllegalArgumentException e) {
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName compName) {
			mBound = false;
		}
	};

	private void onShowDisplays() {
		Log.d(TAG, "onShowDisplays()");
		Intent intent = new Intent(this, RenderActivity.class);
		startActivity(intent);
	}

	private void onQuit() {
		finish();
	}
}