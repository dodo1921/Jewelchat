package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 15/07/17.
 */

public class InsertSelfMessage extends IntentService {

	public InsertSelfMessage() {
		super("InsertSelfMessageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i("Service","InsertSelfMessage");

		int type = intent.getIntExtra("type", 0 );
		String msg = intent.getStringExtra("msg");
		long chatroom = intent.getIntExtra("chatroom",0);

		ContentValues cv = new ContentValues();

		cv.put(ChatMessageContract.CREATOR_ID, JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0));
		cv.put(ChatMessageContract.CHAT_ROOM, chatroom);
		cv.put(ChatMessageContract.CREATED_TIME, System.currentTimeMillis());
		cv.put(ChatMessageContract.MSG_TYPE, type);
		cv.put(ChatMessageContract.MSG_TEXT, msg);
		/*
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
		*/
		Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
		String msg_id = getContentResolver().insert(urimsg, cv).getLastPathSegment();

		Log.i(">>>After insert",msg_id.toString());

		JSONObject p = new JSONObject();
		try {
			p.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0));
			p.put("sender_msgid", Integer.parseInt(msg_id));
			//p.put("name", JewelChatApp.getSharedPref().getString(JewelChatPrefs.NAME,"Mayukh") );
			p.put("sender_phone", Long.parseLong(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_PHONE,"") ));
			p.put("receiver_id", chatroom);
			p.put("eventname","new_msg");
			p.put("msg", msg);
			p.put("type", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(JewelChatApp.getJCSocket().getSocket().connected())
			JewelChatApp.getJCSocket().getSocket().emit( "publish", p);

	}
}
