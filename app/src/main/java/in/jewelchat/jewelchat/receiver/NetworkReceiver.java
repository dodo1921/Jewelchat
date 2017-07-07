package in.jewelchat.jewelchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 06/07/17.
 */

public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {


		if ( NetworkConnectivityStatus.getConnectivityStatus() == 0 ) {

			JewelChatApp.getBusInstance().post(new NoInternet());

		}

	}
}