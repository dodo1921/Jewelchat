package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.screens.ActivityChatRoom;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 18/07/17.
 */

public class PickJewelService extends IntentService implements Response.ErrorListener, Response.Listener<JSONObject> {

	public PickJewelService() {
		super("PickJewelService");
	}

	private int msg_id;
	private int jewel_type;


	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i("Service","PickJewelService");

		jewel_type = intent.getIntExtra("jewel_type", -1 );
		int serverId = intent.getIntExtra("serverId",0);
		msg_id = intent.getIntExtra("msg_id",0);
		int is_group = intent.getIntExtra("is_group",0);

		ActivityChatRoom.getInstance().setIsPicking(true);

		ContentValues cv2 = new ContentValues();
		cv2.put(ChatMessageContract.IS_JEWEL_PICKED, 2);

		Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
		getContentResolver().update(urimsg4, cv2, ChatMessageContract.KEY_ROWID + "= ?", new String[]{ msg_id+"" } );


		JSONObject t = new JSONObject();
		try {

			t.put("jeweltype", jewel_type);
			t.put("is_group", is_group);
			t.put("msg_id", serverId);

			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.PICKJEWEL, t, this, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				//createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}



	}

	@Override
	public void onErrorResponse(VolleyError error) {

		Log.i("Pick Jewel", "Error"+error);

		ActivityChatRoom.getInstance().setIsPicking(false);
		ContentValues cv2 = new ContentValues();
		cv2.put(ChatMessageContract.IS_JEWEL_PICKED, 0);

		Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
		getContentResolver().update(urimsg4, cv2, ChatMessageContract.KEY_ROWID + "= ?", new String[]{ msg_id+"" } );


	}

	@Override
	public void onResponse(JSONObject response) {

		Log.i("Pick Jewel", "Success");

		ActivityChatRoom.getInstance().setIsPicking(false);

		ContentValues cv2 = new ContentValues();
		cv2.put(ChatMessageContract.IS_JEWEL_PICKED, 1);

		Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
		getContentResolver().update(urimsg4, cv2, ChatMessageContract.KEY_ROWID + "= ?", new String[]{ msg_id+"" } );

		int count = JewelChatApp.getSharedPref().getInt(jewel_type+"",0);
		JewelChatApp.getSharedPref().edit().putInt(jewel_type+"", count+1).commit();

		JewelChatApp.getBusInstance().post(JewelChatApp.produceJewelChangeEvent());

	}


}
