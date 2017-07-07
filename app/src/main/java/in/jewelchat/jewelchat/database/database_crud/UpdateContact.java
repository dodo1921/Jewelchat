package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 04/07/17.
 */

public class UpdateContact extends IntentService {

	public UpdateContact() {
		super("UpdateContact");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			JSONObject data = new JSONObject(intent.getStringExtra("json"));




		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
