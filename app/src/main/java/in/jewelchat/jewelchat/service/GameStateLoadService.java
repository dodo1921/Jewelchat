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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class GameStateLoadService extends IntentService
		implements Response.ErrorListener, Response.Listener<JSONObject> {

	public GameStateLoadService() {
		super("GameStateLoad");
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.GETGAMESTATE, null, this, this);
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

		JewelChatApp.appLog("GameStateLoadService Volley error:"+errorMessage);
		FirebaseCrash.report(error);

	}

	@Override
	public void onResponse(JSONObject response) {

		JewelChatApp.appLog("GameStateLoadService" + ":onResponse");

		try {

			Boolean error = response.getBoolean("error");

			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}
			JSONArray scores = response.getJSONArray("scores");
			JSONArray jewels = response.getJSONArray("jewels");

			SharedPreferences.Editor editor = JewelChatApp.getSharedPref().edit();

			editor.putInt(JewelChatPrefs.LEVEL, ((JSONObject)scores.get(0)).getInt("level"));
			editor.putInt(JewelChatPrefs.XP, ((JSONObject)scores.get(0)).getInt("points"));
			editor.putInt(JewelChatPrefs.XP_MAX, ((JSONObject)scores.get(0)).getInt("max_level_points"));

			for(int i=0; i<jewels.length(); i++){
				JSONObject t = (JSONObject) jewels.get(i);
				editor.putInt(t.getInt("jeweltype_id")+"", t.getInt("count"));
			}

			if(editor.commit())
				JewelChatApp.getBusInstance().post(JewelChatApp.produceJewelChangeEvent());

		} catch (JSONException e) {
			Log.i(">>>>>>>>>>>>>>>>>", "GameStateLoadService:"+e);
			FirebaseCrash.report(e);
		} catch (Exception e) {
			Log.i(">>>>>>>>>>>>>>>>>", "GameStateLoadService:"+e);
			FirebaseCrash.report(e);
		}



	}
}
