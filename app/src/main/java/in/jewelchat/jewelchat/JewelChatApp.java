package in.jewelchat.jewelchat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.picasso.Picasso;

import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.network.JewelChatSocket;
import in.jewelchat.jewelchat.util.JewelChatImageGetter;

/**
 * Created by mayukhchakraborty on 06/06/17.
 */

public class JewelChatApp extends Application {

	private FirebaseAnalytics mFirebaseAnalytics;
	private static JewelChatApp mInstance;
	private static RequestQueue mRequestQueue;
	private static SharedPreferences sharedPref;
	private static String mCookie;
	private static Picasso mPicasso;
	private static JewelChatSocket jcSocket;
	private static JewelChatImageGetter imageGetter;
	private static final Bus BUS = new Bus();

	private static String GCLB = null;


	public static final int CONNECTION_TIMEOUT = 10000;

	public static JewelChatApp getInstance() {
		return mInstance;
	}

	public static void setGCLB( String c){
		GCLB = c;
	}

	public static String getGCLB(){
		return GCLB;
	}


	public static void appLog(int level, String TAG, @NonNull String message) {
		FirebaseCrash.logcat(level, TAG, Thread.currentThread().getName() + ":" + message);
	}

	public static void appLog(@NonNull String message) {
		FirebaseCrash.log(Thread.currentThread().getName() + ":" + message);
	}

	public static SharedPreferences getSharedPref() {
		if (sharedPref == null) {
			setSharedPref();
		}
		return sharedPref;
	}

	private static void setSharedPref() {
		sharedPref = mInstance.getSharedPreferences("in.mayukh.jewelchat", Context.MODE_PRIVATE);
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue == null)
			setRequestQueue();
		return mRequestQueue;
	}

	public static void saveCookie(String cookie) {
		// TODO Auto-generated method stub
		if (cookie == null) {
			//the server did not return a cookie so we wont have anything to save
			return;
		}
		// Save in the preferences
		SharedPreferences.Editor editor = getSharedPref().edit();
		editor.putString("cookie", cookie);
		editor.apply();
	}

	public static String getCookie() {

		String cookie = getSharedPref().getString("cookie", "");
		if (cookie.contains("expires")) {
			/** you might need to make sure that your cookie returns expires when its expired. I also noted that cokephp returns deleted */
			removeCookie();
			return "";
		}
		return cookie;
	}

	public static void removeCookie() {

		SharedPreferences.Editor editor = getSharedPref().edit();
		editor.remove("cookie");
		editor.apply();

	}

	private static void setRequestQueue() {
		mRequestQueue = Volley.newRequestQueue(mInstance);
	}

	public static Bus getBusInstance() {
		return BUS;
	}

	@Produce
	public static GameStateChangeEvent produceJewelChangeEvent() {
		// Provide an initial value for location based on the last known position.
		Log.i("GAMESTATE",">>>>>>>");
		int sum=0;
		for(int i=3; i<30; i++){
			sum += getSharedPref().getInt(i+"",0);
		}

		return new GameStateChangeEvent(sum,
				getSharedPref().getInt(JewelChatPrefs.LEVEL,0),
				getSharedPref().getInt(JewelChatPrefs.XP_MAX,0),
				getSharedPref().getInt(JewelChatPrefs.XP,0),
				false);
	}

	public static JewelChatImageGetter getImageGetter(){

		if(imageGetter == null)
			imageGetter = new JewelChatImageGetter();

		return imageGetter;

	}

	public static JewelChatSocket getJCSocket(){

		if( jcSocket == null )
			jcSocket = new JewelChatSocket();

		return jcSocket;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		mInstance = this;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		getJCSocket().getSocket().disconnect();
	}

}
