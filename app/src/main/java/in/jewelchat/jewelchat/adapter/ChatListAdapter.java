package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class ChatListAdapter extends CursorAdapter {

	public ChatListAdapter(Context context) {
		super(context, null, false);
	}



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

	}
}
