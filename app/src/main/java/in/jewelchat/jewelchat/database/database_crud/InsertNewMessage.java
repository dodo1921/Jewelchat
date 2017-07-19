package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */
public class InsertNewMessage extends IntentService {

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
				stack  = stack + ","+msg_id;
				if(StringUtils.countMatches(stack, ".") == 30){
					stack = stack.substring(stack.indexOf(",")+1);
				}

				JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.MSG_STACK, stack).commit();
			}


			Uri urimsg1 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
			Cursor c = getContentResolver().query(urimsg1, new String[]{ContactContract.UNREAD_COUNT}
					, ContactContract.JEWELCHAT_ID + " = ? "
					, new String[]{data.getInt("sender_id")+""}, ContactContract.KEY_ROWID );

			if(c.getCount() == 0){ // insert contact if not present

				ContentValues cv1 = new ContentValues();
				cv1.put(ContactContract.JEWELCHAT_ID, data.getInt("sender_id"));
				cv1.put(ContactContract.CONTACT_NAME, data.getString("name"));
				cv1.put(ContactContract.IS_REGIS, 1);
				cv1.put(ContactContract.CONTACT_NUMBER, data.getInt("sender_phone"));
				cv1.put(ContactContract.UNREAD_COUNT, 1);

				Uri urimsg3 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
				getContentResolver().insert(urimsg3, cv1);

			}else{
				c.moveToFirst();
				ContentValues cv2 = new ContentValues();
				cv2.put(ContactContract.UNREAD_COUNT, c.getInt(c.getColumnIndex(ContactContract.UNREAD_COUNT))+1);

				Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
				getContentResolver().update(urimsg4, cv2, ContactContract.JEWELCHAT_ID + "= ?", new String[]{ data.getInt("sender_id")+"" }  );

			}

			JSONObject deliveryack = new JSONObject();

			deliveryack.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0));
			deliveryack.put("sender_msgid", msg_id);
			deliveryack.put("receiver_id", data.getInt("sender_id"));
			deliveryack.put("eventname", "msg_delivery");
			deliveryack.put("chat_id", data.getInt("sender_msgid"));

			if(JewelChatApp.getJCSocket().getSocket().connected())
				JewelChatApp.getJCSocket().getSocket().emit( "delivery", deliveryack);

		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}

	}
}
