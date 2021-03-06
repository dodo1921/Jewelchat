package in.jewelchat.jewelchat.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 06/07/17.
 */

public class DownloadBlockedUserService extends IntentService
		implements Response.ErrorListener, Response.Listener<JSONObject>   {

	public DownloadBlockedUserService() {
		super("DownloadBlockedUserService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		JewelChatRequest req = new JewelChatRequest(Request.Method.GET, JewelChatURLS.GETBLOCKEDUSERS, null, this, this);
		if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED)
			JewelChatApp.getRequestQueue().add(req);
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
					e.printStackTrace();

				}

			}else{

				if (error instanceof TimeoutError || error instanceof NoConnectionError) {
					errorMessage = "Connection Timeout";
				}else{
					errorMessage = "Network Error";
				}

			}
		}

		JewelChatApp.appLog("DownloadBlockedUserService Volley error:"+errorMessage);
		FirebaseCrash.report(error);

	}

	@Override
	public void onResponse(JSONObject response) {

		JewelChatApp.appLog("DownloadBlockedUserService" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}

			JSONArray users = response.getJSONArray("users");

			ContentValues[] cv = new ContentValues[users.length()];

			JSONObject user = null;
			for(int i=0; i<users.length(); i++){

				user = users.getJSONObject(i);

				cv[i].put(ContactContract.JEWELCHAT_ID, user.getInt("id"));
				cv[i].put(ContactContract.CONTACT_NAME, user.getString("name"));
				cv[i].put(ContactContract.CONTACT_NUMBER, user.getString("phone"));
				cv[i].put(ContactContract.STATUS_MSG, user.getString("status"));
				cv[i].put(ContactContract.IS_REGIS, 1);
				cv[i].put(ContactContract.IS_BLOCKED, 1);
				cv[i].put(ContactContract.IMAGE_PATH, user.getString("pic"));

			}

			Uri uri = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
			getContentResolver().bulkInsert(uri, cv);

			JewelChatApp.getSharedPref().edit().putBoolean(JewelChatPrefs.BLOCKED_USERS_DOWNLOADED, true).commit();


		} catch (JSONException e) {
			FirebaseCrash.report(e);
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}

	}

}
