package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 03/07/17.
 */
public class UpdateMessageDelivered extends IntentService {

	public UpdateMessageDelivered() {
		super("UpdateMessageDelivered");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			JSONObject data = new JSONObject(intent.getStringExtra("json"));

			ContentValues cv = new ContentValues();
			cv.put(ChatMessageContract.IS_DELIVERED, 1);
			cv.put(ChatMessageContract.TIME_DELIVERED, data.getLong("created_at"));

			Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			getContentResolver().update(urimsg, cv, ChatMessageContract.KEY_ROWID + "= ?", new String[]{ data.getInt("chat_id")+"" }  );


		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}
	}
}
