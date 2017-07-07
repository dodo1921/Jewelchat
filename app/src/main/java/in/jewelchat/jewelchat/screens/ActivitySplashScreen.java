package in.jewelchat.jewelchat.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import in.jewelchat.jewelchat.JewelChat;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;



/**
 * Created by mayukhchakraborty on 28/02/16.
 */
public class ActivitySplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);

		if(JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.IS_LOGGED, false)
				&&  JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.INITIAL_DETAILS_ENTERED, false)
				&& NetworkConnectivityStatus.getConnectivityStatus()>0){

			//Intent service = new Intent(getApplicationContext(), GameStateLoadService.class);
			//startService(service);
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.IS_LOGGED, false)
						&& JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.INITIAL_DETAILS_ENTERED, false)) {

					Intent i = new Intent(ActivitySplashScreen.this, JewelChat.class);
					startActivity(i);
					finish();

				}else if(JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.IS_LOGGED, false)
						&& !JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.INITIAL_DETAILS_ENTERED, false)) {

					Intent i = new Intent(ActivitySplashScreen.this, ActivityInitialDetails.class);
					startActivity(i);
					finish();

				}else {

					Intent i = new Intent(ActivitySplashScreen.this, ActivityMobileEntry.class);
					startActivity(i);
					finish();

				}
			}

		}, 1000);

	}
}
