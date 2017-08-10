package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */
public class InsertNewMessage extends IntentService implements Response.ErrorListener, Response.Listener<JSONObject>  {

	public InsertNewMessage() {
		super("InsertNewMessage");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {


			JSONObject data = new JSONObject(intent.getStringExtra("json"));


			Uri urimsg0 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
			Cursor blocked = getContentResolver().query(urimsg0, new String[]{ContactContract.KEY_ROWID}
					, ContactContract.JEWELCHAT_ID + " = ? AND " + ContactContract.IS_BLOCKED + " = ?"
					, new String[]{data.getInt("sender_id")+"", "1"}, ContactContract.KEY_ROWID );

			if(blocked.getCount() >= 1)
				return;

			ContentValues cv = new ContentValues();
			cv.put(ChatMessageContract.SERVER_ID, data.getInt("id"));
			cv.put(ChatMessageContract.CREATOR_ID, data.getInt("sender_id"));
			cv.put(ChatMessageContract.CHAT_ROOM, data.getInt("sender_id"));
			cv.put(ChatMessageContract.SENDER_MSG_ID, data.getInt("sender_msgid"));
			cv.put(ChatMessageContract.SENDER_PHONE, data.getInt("sender_phone"));
			cv.put(ChatMessageContract.SENDER_NAME, data.getString("name"));
			cv.put(ChatMessageContract.JEWEL_TYPE, data.getInt("jeweltype_id"));
			Log.i("CREATED_AT", data.getLong("created_at")+"");
			cv.put(ChatMessageContract.CREATED_TIME, data.getLong("created_at"));
			cv.put(ChatMessageContract.MSG_TYPE, data.getInt("type"));

			if(data.getInt("type") == 1){
				cv.put(ChatMessageContract.MSG_TEXT, data.getString("msg"));
				Log.i("CHATMSG", data.getString("msg"));
			}else if(data.getInt("type") == 2){
				cv.put(ChatMessageContract.IMAGE_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.IMAGE_PATH_CLOUD, data.getString("path"));
			}else if(data.getInt("type") == 3){
				cv.put(ChatMessageContract.VIDEO_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.VIDEO_PATH_CLOUD, data.getString("path"));
			}else if(data.getInt("type") == 4){
				cv.put(ChatMessageContract.MSG_TEXT, data.getString("msg"));
				cv.put(ChatMessageContract.IMAGE_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.IMAGE_PATH_CLOUD, data.getString("path"));
			}

			Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			String msg_id = getContentResolver().insert(urimsg, cv).getLastPathSegment();

			if(!msg_id.equals("-1")){
				String stack = JewelChatApp.getSharedPref().getString(JewelChatPrefs.MSG_STACK, "");
				if(stack.equals(""))
					stack = msg_id;
				else
					stack  = stack + ","+msg_id;

				if(StringUtils.countMatches(stack, ",") == 30){
					stack = stack.substring(stack.indexOf(",")+1);
				}

				JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.MSG_STACK, stack).commit();
			}

			if(!msg_id.equals("-1")) {
				Uri urimsg1 = Uri.parse(JewelChatDataProvider.SCHEME + "://" + JewelChatDataProvider.AUTHORITY + "/" + ContactContract.SQLITE_TABLE_NAME);
				Cursor c = getContentResolver().query(urimsg1, new String[]{ContactContract.UNREAD_COUNT}
						, "( "+ContactContract.JEWELCHAT_ID + " = ? AND "+ContactContract.IS_GROUP +" = ? ) OR " + ContactContract.CONTACT_NUMBER + " = ?"
						, new String[]{data.getInt("sender_id") + "", "0" , data.getLong("sender_phone") + ""}, ContactContract.KEY_ROWID);

				if (c.getCount() == 0) { // insert contact if not present
					Log.i("INSERT NEW CONTACT>>>", data.getString("sender_phone")+"::"+data.getInt("sender_id"));
					ContentValues cv1 = new ContentValues();
					cv1.put(ContactContract.JEWELCHAT_ID, data.getInt("sender_id"));
					cv1.put(ContactContract.IS_GROUP, 0);
					cv1.put(ContactContract.CONTACT_NAME, data.getString("name"));
					cv1.put(ContactContract.IS_REGIS, 1);
					cv1.put(ContactContract.CONTACT_NUMBER, data.getString("sender_phone"));
					cv1.put(ContactContract.UNREAD_COUNT, 1);

					Uri urimsg3 = Uri.parse(JewelChatDataProvider.SCHEME + "://" + JewelChatDataProvider.AUTHORITY + "/" + ContactContract.SQLITE_TABLE_NAME);
					getContentResolver().insert(urimsg3, cv1);

				} else {
					c.moveToFirst();
					Log.i("UPDATE CONTACT>>>", data.getString("sender_phone")+"::"+data.getInt("sender_id"));
					ContentValues cv2 = new ContentValues();
					cv2.put(ContactContract.JEWELCHAT_ID, data.getInt("sender_id"));
					cv2.put(ContactContract.CONTACT_NAME, data.getString("name"));
					cv2.put(ContactContract.IS_REGIS, 1);
					cv2.put(ContactContract.CONTACT_NUMBER, data.getString("sender_phone"));
					cv2.put(ContactContract.UNREAD_COUNT, c.getInt(0) + 1);

					Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME + "://" + JewelChatDataProvider.AUTHORITY + "/" + ContactContract.SQLITE_TABLE_NAME);
					getContentResolver().update(urimsg4, cv2, ContactContract.JEWELCHAT_ID + "= ? OR " + ContactContract.CONTACT_NUMBER + "= ?"
							, new String[]{data.getInt("sender_id") + "", data.getLong("sender_phone") + ""});

				}

				c.close();
			}

			if(!msg_id.equals("-1")){

				JSONObject deliveryack = new JSONObject();

				deliveryack.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0));
				deliveryack.put("sender_msgid", msg_id);
				deliveryack.put("receiver_id", data.getInt("sender_id"));
				deliveryack.put("eventname", "msg_delivery");
				deliveryack.put("chat_id", data.getInt("sender_msgid"));

				JSONObject t = new JSONObject();
				t.put("data", deliveryack);

				if(JewelChatApp.getJCSocket().getSocket().connected())
					JewelChatApp.getJCSocket().getSocket().emit( "delivery", deliveryack);
				else{

					JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.DELIVERY, t, this, this);
					if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED)
						JewelChatApp.getRequestQueue().add(req);

				}

			}



		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {



	}

	@Override
	public void onResponse(JSONObject response) {

		JewelChatApp.appLog("Devivery" + ":onResponse");
		try {

			Boolean error = response.getBoolean("error");
			if(error){
				String err_msg = response.getString("message");
				throw new Exception(err_msg);
			}

			ContentValues cv = new ContentValues();
			cv.put(ChatMessageContract.IS_DELIVERED, 1);
			cv.put(ChatMessageContract.TIME_DELIVERED, response.getInt("delivered"));

			Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			getContentResolver().update(urimsg, cv, ChatMessageContract.KEY_ROWID + "= ?", new String[]{ response.getInt("sender_msgid")+"" }  );


		} catch (JSONException e) {
			FirebaseCrash.report(e);
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}

	}


}
