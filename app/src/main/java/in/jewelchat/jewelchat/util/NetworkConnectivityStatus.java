package in.jewelchat.jewelchat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import in.jewelchat.jewelchat.JewelChatApp;

/**
 * Created by mayukhchakraborty on 24/02/16.
 */
public class NetworkConnectivityStatus {

	public static final int NOT_CONNECTED = 0;
	public static final int CONNECTED = 1;
	public static final int WIFI = 2;
	public static final int MOBILE = 3;

	public static String getConnectivityStatusString() {
		int conn = NetworkConnectivityStatus.getConnectivityStatus();
		String status = null;
		if (conn == NetworkConnectivityStatus.WIFI) {
			status = "Wifi enabled";
		} else if (conn == NetworkConnectivityStatus.MOBILE) {
			status = "Mobile data enabled";
		} else if (conn == NetworkConnectivityStatus.NOT_CONNECTED) {
			status = "Not connected to Internet";
		}
		return status;
	}

	public static int getConnectivityStatus() {
		try {
			ConnectivityManager cm = (ConnectivityManager)
					JewelChatApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				return CONNECTED;
			} else {
				return NOT_CONNECTED;
			}
		} catch (NullPointerException e) {
			JewelChatApp.appLog(NetworkConnectivityStatus.class.getSimpleName() + ":" + e.toString());
		}
		return NOT_CONNECTED;
	}
}
