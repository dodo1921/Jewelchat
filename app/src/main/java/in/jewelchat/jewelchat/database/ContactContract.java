package in.jewelchat.jewelchat.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by mayukhchakraborty on 04/03/16.
 */

public class ContactContract implements BaseColumns {

	public static final String KEY_ROWID = BaseColumns._ID;
	public static final String JEWELCHAT_ID = "jewelChatId"; // backend Id
	public static final String CONTACT_NUMBER = "contactNumber";
	public static final String CONTACT_NAME = "contactName";
	public static final String PHONEBOOK_CONTACT_NAME = "PhoneBookContactName";
	public static final String IS_GROUP = "isGroup";
	public static final String IMAGE_PHONEBOOK = "imagePhoneBook";
	public static final String IMAGE_PATH = "imagePath";
	public static final String STATUS_MSG = "statusMsg";
	public static final String IS_REGIS = "isRegis";
	public static final String IS_GROUP_ADMIN = "isGroupAdmin";
	public static final String IS_INVITED = "isInvited";
	public static final String IS_BLOCKED = "isBlocked";
	public static final String IS_PHONEBOOK_CONTACT = "isPhonebookContact";
	public static final String UNREAD_COUNT = "unread_count";




	public static final String SQLITE_TABLE_NAME = "Contact";

	private static final String DATABASE_CREATE =
			"CREATE TABLE if not exists " + SQLITE_TABLE_NAME + " (" +
					KEY_ROWID + " integer PRIMARY KEY autoincrement," +
					JEWELCHAT_ID + "  INTEGER" +  ", " +
					CONTACT_NUMBER + "  INTEGER" +  ", " +
					CONTACT_NAME + "  TEXT" +  ", " +
					PHONEBOOK_CONTACT_NAME + "  TEXT" +  ", " +
					IS_GROUP + "  INTEGER DEFAULT 0 " +  ", " +
					STATUS_MSG + "  TEXT" +  ", " +
					IS_REGIS + "  INTEGER" +  ", " +
					IS_GROUP_ADMIN + "  INTEGER" +  ", " +
					IS_INVITED + "  INTEGER DEFAULT 0" +  ", " +
					IS_BLOCKED + "  INTEGER DEFAULT 0 " +  ", " +
					IS_PHONEBOOK_CONTACT + "  INTEGER" +  ", " +
					IMAGE_PHONEBOOK + "  TEXT" +  ", " +
					UNREAD_COUNT + "  INTEGER DEFAULT 0 " +  ", " +
					IMAGE_PATH + "  TEXT , unique ( " + JEWELCHAT_ID + ", " +IS_GROUP+ " ), unique ( "+ CONTACT_NUMBER + " ) )";

	public static void onCreate(SQLiteDatabase db) {
		Log.i("Contact", "OnCreate");
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
	                             int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_NAME);
		onCreate(db);
	}



}
