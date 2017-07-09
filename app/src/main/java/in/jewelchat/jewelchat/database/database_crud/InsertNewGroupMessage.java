package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */
public class InsertNewGroupMessage extends IntentService {

	public InsertNewGroupMessage() {
		super("InsertNewGroupMessage");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			JSONObject data = new JSONObject(intent.getStringExtra("json"));

			ContentValues cv = new ContentValues();
			cv.put(ChatMessageContract.SERVER_ID, data.getInt("id"));
			cv.put(ChatMessageContract.IS_GROUP_MSG, 1);
			cv.put(ChatMessageContract.CREATOR_ID, data.getInt("sender_id"));
			cv.put(ChatMessageContract.CHAT_ROOM, data.getInt("group_id"));
			cv.put(ChatMessageContract.SENDER_MSG_ID, data.getInt("sender_msgid"));
			cv.put(ChatMessageContract.SENDER_PHONE, data.getLong("sender_phone"));
			cv.put(ChatMessageContract.SENDER_NAME, data.getString("name"));
			cv.put(ChatMessageContract.JEWEL_TYPE, data.getInt("jeweltype_id"));
			cv.put(ChatMessageContract.CREATED_TIME, data.getLong("created_at"));
			cv.put(ChatMessageContract.MSG_TYPE, data.getInt("type"));

			if(data.getInt("type") == 1){
				cv.put(ChatMessageContract.MSG_TEXT, data.getInt("msg"));
			}else if(data.getInt("type") == 2){
				cv.put(ChatMessageContract.IMAGE_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.IMAGE_PATH_CLOUD, data.getString("path"));
			}else if(data.getInt("type") == 3){
				cv.put(ChatMessageContract.VIDEO_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.VIDEO_PATH_CLOUD, data.getString("path"));
			}else if(data.getInt("type") == 4){
				cv.put(ChatMessageContract.MSG_TEXT, data.getInt("msg"));
				cv.put(ChatMessageContract.IMAGE_BLOB, data.getString("blob"));
				cv.put(ChatMessageContract.IMAGE_PATH_CLOUD, data.getString("path"));
			}

			Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			getContentResolver().insert(urimsg, cv);

			Uri urimsg1 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
			Cursor c = getContentResolver().query(urimsg1, new String[]{ContactContract.UNREAD_COUNT}
					, ContactContract.JEWELCHAT_ID + " = ? AND "+ ContactContract.IS_GROUP + " = ?"
					, new String[]{data.getInt("group_id")+"", "1" }, "ASC" );

			if(c.getCount() == 0){ // insert contact if not present

				ContentValues cv1 = new ContentValues();
				cv1.put(ContactContract.JEWELCHAT_ID, data.getInt("group_id"));
				cv1.put(ContactContract.IS_GROUP, 1);
				cv1.put(ContactContract.IS_REGIS, 1);
				cv1.put(ContactContract.CONTACT_NAME, data.getInt("name"));
				cv1.put(ContactContract.UNREAD_COUNT, 1);

				Uri urimsg3 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
				getContentResolver().insert(urimsg3, cv1);

			}else{

				ContentValues cv2 = new ContentValues();
				cv2.put(ContactContract.UNREAD_COUNT, c.getInt(c.getColumnIndex(ContactContract.UNREAD_COUNT))+1);

				Uri urimsg4 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
				getContentResolver().update(urimsg4, cv2, ContactContract.JEWELCHAT_ID + "= ? AND " + ContactContract.IS_GROUP + "= ?"
						, new String[]{ data.getInt("group_id")+"", "1" }  );

			}

			// SEND group delivery ack

		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}


	}
}
