package in.jewelchat.jewelchat.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;

/**
 * Created by mayukhchakraborty on 04/03/16.
 */
public class JewelChatDataProvider extends ContentProvider {

	// The URI scheme used for content URIs
	public static final String SCHEME = "content";

	// The provider's authority
	public static final String AUTHORITY = "in.jewelchat.jewelchat";

	/**
	 * The DataProvider content URI
	 */
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

	private static final int CHAT_MESSAGE = 1;
	private static final int CONTACT = 2;
	private static final int CHAT_LIST = 3;
	private static final int UNREAD_COUNTER = 4;


	private JewelChatDatabaseHelper dbHelper;
	private static final UriMatcher uriMatcher;

	static {

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, ChatMessageContract.SQLITE_TABLE_NAME , CHAT_MESSAGE );
		uriMatcher.addURI(AUTHORITY, ContactContract.SQLITE_TABLE_NAME , CONTACT );
		uriMatcher.addURI(AUTHORITY, "chatlist" , CHAT_LIST );  //chatlist is join of contact and msg table
		uriMatcher.addURI(AUTHORITY, "unread_counter" , UNREAD_COUNTER );

	}



	@Override
	public boolean onCreate() {
		dbHelper = JewelChatDatabaseHelper.getHelper(getContext());
		return true;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		switch (uriMatcher.match(uri)) {

			case CHAT_MESSAGE:{
				Cursor returnCursor = db.query(
						ChatMessageContract.SQLITE_TABLE_NAME,
						projection,
						selection, selectionArgs, null, null, sortOrder);
				Log.i(">>", "Here");

				returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

				return returnCursor;

			}

			case CONTACT:{
				Cursor returnCursor = db.query(
						ContactContract.SQLITE_TABLE_NAME,
						projection,
						selection, selectionArgs, null, null, sortOrder);

				// Sets the ContentResolver to watch this content URI for data changes
				returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
				return returnCursor;

			}
			case CHAT_LIST:{
				String query = "SELECT "
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.JEWELCHAT_ID + ", " //0
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.CONTACT_NAME + ", " //1
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.CONTACT_NUMBER + ", " //2
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IMAGE_PATH + ", " //3
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_REGIS + ", "//4
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_GROUP + ", "//5
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_GROUP_ADMIN + ", "//6
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_INVITED + ", " //7
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_BLOCKED + ", " //8
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.STATUS_MSG + ", " //9
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.UNREAD_COUNT + ", " //10
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.MSG_TYPE + ", " //11
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.MSG_TEXT + ", " //12
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.CREATED_TIME + ", " //13
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.IS_READ + ", " //14
						+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.KEY_ROWID + ", " // 15
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.CREATOR_ID + ", " //16
						+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.CHAT_ROOM + ", " //17
						+ "MAX("+ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.KEY_ROWID+")" //18
						+ " FROM "
						+ ContactContract.SQLITE_TABLE_NAME + ", "+ ChatMessageContract.SQLITE_TABLE_NAME
						+ " WHERE "+ ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.JEWELCHAT_ID+" = "+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.CHAT_ROOM
						+ " AND " + ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_BLOCKED + " = 0 "
						+ " AND " + ContactContract.SQLITE_TABLE_NAME+"."+ContactContract.IS_REGIS + " = 1 "
						+ " GROUP BY "+ ChatMessageContract.SQLITE_TABLE_NAME+"."+ChatMessageContract.CHAT_ROOM;
				Log.i(">>", query);
				Cursor returnCursor = db.rawQuery(query, null);

				returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
				return returnCursor;

			}

			default: {
				throw new IllegalArgumentException("Query -- Invalid URI:" + uri);
			}
		}


	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id;
		switch (uriMatcher.match(uri)) {
			case CHAT_MESSAGE:{

				try{
					id = db.insertOrThrow(ChatMessageContract.SQLITE_TABLE_NAME, null, values);
					getContext().getContentResolver().notifyChange(uri, null);
					getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist"), null);
					String creatorid = values.getAsString(ChatMessageContract.CREATOR_ID);
					String userid = JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_ID, "");
					if(!creatorid.equals(userid))
						getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/"+creatorid), null);
				}catch(SQLiteConstraintException e){
					Log.i("ConstraintException", "Duplicate row insertion");
					id=-1;
				}catch(SQLException e){
					id=-1;
				}catch(Exception e){
					id=-1;
				}

				break;
			}
			case CONTACT:{
				id = db.insertOrThrow(ContactContract.SQLITE_TABLE_NAME, null, values);
				Log.i("ID>>>>>>>>",""+id);
				getContext().getContentResolver().notifyChange(uri, null);
				getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist"), null);
				break;
			}
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		return Uri.parse(uri + "/#" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int count;
		switch(uriMatcher.match(uri)) {
			case CHAT_MESSAGE:
				count = db.delete(ChatMessageContract.SQLITE_TABLE_NAME,  selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist"), null);
				break;
			case CONTACT:
				count = db.delete(ContactContract.SQLITE_TABLE_NAME, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist" ), null);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int count;
		switch(uriMatcher.match(uri)) {
			case CHAT_MESSAGE:
				count = db.update(ChatMessageContract.SQLITE_TABLE_NAME, values, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist"), null);
				break;
			case CONTACT:
				count = db.update(ContactContract.SQLITE_TABLE_NAME, values, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + "/chatlist" ), null);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}


		return count;
	}

	public void close() {
		dbHelper.close();
	}
}
