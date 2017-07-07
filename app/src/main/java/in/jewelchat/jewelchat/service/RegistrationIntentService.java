package in.jewelchat.jewelchat.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

import static android.content.ContentValues.TAG;

/**
 * Created by mayukhchakraborty on 04/07/17.
 */

public class RegistrationIntentService extends IntentService  implements Response.ErrorListener, Response.Listener<JSONObject> {

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */



	public RegistrationIntentService() {
		super("RegistrationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.d(TAG, "Refreshed token: " + refreshedToken);

		// TODO: Implement this method to send any registration to your app's servers.
		//sendRegistrationToServer(refreshedToken);

		JSONObject t = new JSONObject();
		try {
			t.put("token", refreshedToken);
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.UPDATE_GCM_TOKEN, t, this, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED)
				JewelChatApp.getRequestQueue().add(req);
		} catch (JSONException e) {
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
			FirebaseCrash.report(e);
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		String errorMessage = error.toString();
		NetworkResponse response = error.networkResponse;

		if(response != null && response.data != null){
			if(response.statusCode == 403){

				SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
				editor.putBoolean(JewelChatPrefs.IS_LOGGED, false);
				editor.putString(JewelChatPrefs.MY_ID, "");
				editor.commit();

				JewelChatApp.getBusInstance().post(new _403NetworkErrorEvent());

			}else if(response.statusCode == 500){

				String json = new String(response.data);
				try{
					JSONObject obj = new JSONObject(json);
					errorMessage = "Please Try Again. Error 500. "+obj.getString("data");
					//makeToast(errorMessage);

				} catch(JSONException e){
					JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
					FirebaseCrash.report(e);
				}

			}else{

				if (error instanceof TimeoutError || error instanceof NoConnectionError) {
					errorMessage = "Connection Timeout";
				}else{
					errorMessage = "Network Error";
				}

			}
		}

		JewelChatApp.appLog("RegistrationIntentService Volley error:"+errorMessage);
		FirebaseCrash.report(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		JewelChatApp.appLog("MyInstanceIDListenerService" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}

			SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();
			editor.putBoolean(JewelChatPrefs.TOKEN_UPLOADED, true);
			editor.commit();


		} catch (JSONException e) {
			FirebaseCrash.report(e);
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}
	}
}
