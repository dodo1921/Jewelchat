package in.jewelchat.jewelchat.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 04/07/17.
 */

public class SendReadAckSecvice extends IntentService {

	public SendReadAckSecvice() {
		super("SendReadAckSecvice");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		int chatroom = intent.getIntExtra("chatroom", 0);

		try {

			Uri urimsg0 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			Cursor non_read = getContentResolver().query(urimsg0, new String[]{}
					,ChatMessageContract.CHAT_ROOM+" = ? AND " + ChatMessageContract.IS_READ + " = ? AND " + ContactContract.IS_GROUP + " = ?"
					, new String[]{ chatroom+"",  "0", "0" }, "ASC" );


			if (non_read.moveToFirst()) {
				do{
					JSONObject readack = new JSONObject();

					readack.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0));
					readack.put("sender_msgid", non_read.getInt(non_read.getColumnIndex(ChatMessageContract.KEY_ROWID)));
					readack.put("receiver_id", non_read.getInt(non_read.getColumnIndex(ChatMessageContract.CREATOR_ID)));
					readack.put("eventname", "msg_read");
					readack.put("chat_id", non_read.getInt(non_read.getColumnIndex(ChatMessageContract.SENDER_MSG_ID)));

					if(JewelChatApp.getJCSocket().getSocket().connected())
						JewelChatApp.getJCSocket().getSocket().emit( "read", readack);

				} while (non_read.moveToNext());
			}


		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}

	}
}