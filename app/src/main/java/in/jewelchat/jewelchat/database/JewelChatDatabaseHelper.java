package in.jewelchat.jewelchat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mayukhchakraborty on 04/03/16.
 */
public class JewelChatDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "JewelChatAppDB";
	private static final int DATABASE_VERSION = 1;

	private static JewelChatDatabaseHelper instance;

	JewelChatDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		ContactContract.onCreate(db);
		ChatMessageContract.onCreate(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		ContactContract.onUpgrade(db, oldVersion, newVersion);
		ChatMessageContract.onUpgrade(db, oldVersion, newVersion);

	}

	public static synchronized JewelChatDatabaseHelper getHelper(Context context)
	{
		if (instance == null)
			instance = new JewelChatDatabaseHelper(context);

		return instance;
	}


}
