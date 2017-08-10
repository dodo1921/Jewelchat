package in.jewelchat.jewelchat.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashSet;
import java.util.Set;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;

/**
 * Created by mayukhchakraborty on 21/07/17.
 */

public class FirstTimeContactDownloadService extends IntentService{

	public FirstTimeContactDownloadService() {
		super("FirstTimeContactDownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.i(">>FirstTimeContact","FirstTimeContact");

		JewelChatApp.appLog("FirstTimeContactDownloadService"+":onHandleIntent");



		Set<PhoneBookContact> phoneBookContactsSet = new HashSet<PhoneBookContact>();
		Set<Long> phoneNumbers = new HashSet<>();

		String[] projection = new String[]{
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER ,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY ,
				ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI };

		Cursor cursor = JewelChatApp.getInstance().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC");

		if (cursor == null) {
			return;
		}

		//Log.i("Here","Here");
		Phonenumber.PhoneNumber pn;
		PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		String phoneStr;
		String e164Format;
		while (cursor.moveToNext()) {
			phoneStr = cursor.getString(1);
			try {
				pn = phoneNumberUtil.parse(phoneStr, "IN");
				e164Format = phoneNumberUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
				if (e164Format == null || e164Format.length() < 11)
					continue;

				PhoneBookContact pbc = new PhoneBookContact();
				pbc.contactNumber = Long.parseLong(e164Format.substring(1));
				Log.i("Phone number", pbc.contactNumber+"");
				phoneNumbers.add(Long.parseLong(e164Format.substring(1)));
				pbc.contactName = cursor.getString(2);
				if (cursor.getString(3) != null) {
					pbc.contactImage = cursor.getString(3);
				} else {
					pbc.contactImage = "";
				}
				Log.i("Phone number", pbc.contactNumber+":"+pbc.contactName+":"+pbc.contactImage);
				phoneBookContactsSet.add(pbc);

			} catch (NumberParseException e) {
				FirebaseCrash.report(e);
			}
		}
		cursor.close();


		//ContentValues[] cv = new ContentValues[phoneBookContactsSet.size()+1];
		int count=0;
		for(PhoneBookContact i: phoneBookContactsSet){

			ContentValues cv = new ContentValues();

			cv.put(ContactContract.CONTACT_NUMBER, i.contactNumber);
			cv.put(ContactContract.PHONEBOOK_CONTACT_NAME, i.contactName);
			cv.put(ContactContract.IMAGE_PHONEBOOK, i.contactImage);
			cv.put(ContactContract.IS_PHONEBOOK_CONTACT, 1);
			cv.put(ContactContract.IS_GROUP, 0);
			cv.put(ContactContract.IS_REGIS, 0);
			cv.put(ContactContract.IS_INVITED, 0);
			count++;

			Uri uri = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);

			String msg_id = getContentResolver().insert(uri, cv).getLastPathSegment();

			if(msg_id.equals("-1")){
				ContentValues cvv = new ContentValues();

				cvv.put(ContactContract.PHONEBOOK_CONTACT_NAME, i.contactName);
				cvv.put(ContactContract.IMAGE_PHONEBOOK, i.contactImage);
				cvv.put(ContactContract.IS_PHONEBOOK_CONTACT, 1);


				Uri uri1 = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
				getContentResolver().update(uri1, cvv, ContactContract.CONTACT_NUMBER+" = ?", new String[]{i.contactNumber+""});

			}


		}


	}



}


class PhoneBookContact extends Object{

	public long contactNumber;
	public String contactName;
	public String contactImage;

	public boolean equal(PhoneBookContact pbc){

		if(pbc.contactNumber == contactNumber)
			return true;
		else
			return false;

	}

}
