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

public class SendDeliveryAckService extends IntentService{

	public SendDeliveryAckService() {
		super("SendDeliveryAckService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {



		try {

			Uri urimsg0 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ChatMessageContract.SQLITE_TABLE_NAME);
			Cursor non_delivered = getContentResolver().query(urimsg0, new String[]{}
					, ChatMessageContract.IS_DELIVERED + " = ? AND " + ContactContract.IS_GROUP + " = ? AND "
							+ ChatMessageContract.CREATOR_ID + " != ?"
					, new String[]{ "0", "0", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0)+"" }, null );


			if (non_delivered.moveToFirst()) {
				do{
					JSONObject deliveryack = new JSONObject();

					deliveryack.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0));
					deliveryack.put("sender_msgid", non_delivered.getInt(non_delivered.getColumnIndex(ChatMessageContract.KEY_ROWID)));
					deliveryack.put("receiver_id", non_delivered.getInt(non_delivered.getColumnIndex(ChatMessageContract.CHAT_ROOM)));
					deliveryack.put("eventname", "msg_delivery");
					deliveryack.put("chat_id", non_delivered.getInt(non_delivered.getColumnIndex(ChatMessageContract.SENDER_MSG_ID)));

					if(JewelChatApp.getJCSocket().getSocket().connected())
						JewelChatApp.getJCSocket().getSocket().emit( "delivery", deliveryack);

				} while (non_delivered.moveToNext());
			}


		} catch (JSONException e) {
			e.printStackTrace();
			JewelChatApp.appLog(getClass().getSimpleName()+":"+e.toString());
		}


	}
}