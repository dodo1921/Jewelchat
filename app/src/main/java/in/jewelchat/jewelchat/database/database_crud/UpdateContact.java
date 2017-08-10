package in.jewelchat.jewelchat.database.database_crud;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.database.ContactContract;
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

		JewelChatApp.appLog(Log.INFO, "UpdateContact", "UpdateContact");

		Log.i("UpdateContact>>>", "Update Contact Service");

		try {
			JSONObject data = new JSONObject(intent.getStringExtra("json"));

			ContentValues cv = new ContentValues();
			cv.put(ContactContract.JEWELCHAT_ID, data.getInt("id"));
			cv.put(ContactContract.IMAGE_PATH, data.getString("pic").equals("null")?null:data.getString("pic"));
			cv.put(ContactContract.CONTACT_NAME, data.getString("name"));
			cv.put(ContactContract.STATUS_MSG, data.getString("status"));
			cv.put(ContactContract.IS_GROUP, 0);
			cv.put(ContactContract.IS_REGIS, 1);


			Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
			int x = getContentResolver().update(urimsg, cv, ContactContract.CONTACT_NUMBER + "= ?", new String[]{ data.getLong("phone")+"" }  );

			Log.i(">>>>>UpdateCount", x+"");

		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}catch (Exception e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}
	}
}
