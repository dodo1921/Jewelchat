package in.jewelchat.jewelchat.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 06/07/17.
 */

public class DecrementUnreadCounterService extends IntentService {

	public DecrementUnreadCounterService() {
		super("DecrementUnreadCounterService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		int chatroom = intent.getIntExtra("chatroom", 1);

		ContentValues cv = new ContentValues();
		cv.put(ContactContract.UNREAD_COUNT, 0);


		Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
		getContentResolver().update(urimsg, cv, ContactContract.JEWELCHAT_ID + "= ?", new String[]{ chatroom+"" }  );

	}

}
