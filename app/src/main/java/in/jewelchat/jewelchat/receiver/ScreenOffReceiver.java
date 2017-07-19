package in.jewelchat.jewelchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import in.jewelchat.jewelchat.JewelChatApp;

/**
 * Created by mayukhchakraborty on 19/07/17.
 */

public class ScreenOffReceiver extends BroadcastReceiver {

	public static boolean wasScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {


		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			// DO WHATEVER YOU NEED TO DO HERE
			wasScreenOn = false;
			Log.i("screen off", "screen off");
			JewelChatApp.getJCSocket().getSocket().disconnect();
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			// AND DO WHATEVER YOU NEED TO DO HERE
			wasScreenOn = true;
			Log.i("screen on", "screen on");
		}


	}
}