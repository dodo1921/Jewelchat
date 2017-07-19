package in.jewelchat.jewelchat.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import in.jewelchat.jewelchat.receiver.ScreenOffReceiver;

/**
 * Created by mayukhchakraborty on 19/07/17.
 */

public class ScreenLockReceiveService  extends Service {


	private BroadcastReceiver mReceiver;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Let it continue running until it is stopped.

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new ScreenOffReceiver();
		registerReceiver(mReceiver, filter);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}


}
