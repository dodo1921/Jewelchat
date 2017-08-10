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
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 01/08/17.
 */

public class PublishUnSubmittedService extends IntentService {

	public PublishUnSubmittedService() {
		super("PublishUnSubmittedService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		JewelChatApp.getJCSocket().getSocket().connect();

		try {

			Uri urimsg0 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			Cursor non_submitted = getContentResolver().query(urimsg0, new String[]{}
					, ChatMessageContract.IS_SUBMITTED + " = ? AND " + ChatMessageContract.CREATOR_ID + " = ? "
					, new String[]{ "0", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0)+"" }, null );


			if (non_submitted.moveToFirst()) {
				do{
					JSONObject p = new JSONObject();

					p.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0));
					p.put("sender_msgid", non_submitted.getInt(non_submitted.getColumnIndex(ChatMessageContract.KEY_ROWID)));
					p.put("name", JewelChatApp.getSharedPref().getString(JewelChatPrefs.NAME, "defaultJCUname") );
					p.put("sender_phone", Long.parseLong(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_PHONE,"") ));
					p.put("receiver_id", non_submitted.getInt(non_submitted.getColumnIndex(ChatMessageContract.CHAT_ROOM)));
					p.put("eventname","new_msg");
					p.put("msg", non_submitted.getString(non_submitted.getColumnIndex(ChatMessageContract.MSG_TEXT)));
					p.put("type", non_submitted.getInt(non_submitted.getColumnIndex(ChatMessageContract.MSG_TYPE)));

					if(JewelChatApp.getJCSocket().getSocket().connected())
						JewelChatApp.getJCSocket().getSocket().emit( "publish", p);

				} while (non_submitted.moveToNext());
			}


		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}


	}
}
