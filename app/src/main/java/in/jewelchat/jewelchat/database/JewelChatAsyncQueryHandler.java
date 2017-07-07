package in.jewelchat.jewelchat.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;

/**
 * Created by mayukhchakraborty on 04/03/16.
 */
public class JewelChatAsyncQueryHandler extends AsyncQueryHandler {

	private WeakReference<AsyncQueryListener> mListener;

	public interface AsyncQueryListener {
		void onQueryComplete(int token, Object cookie, Cursor cursor);
		void onDeleteComplete(int token, Object cookie, int result);
		void onInsertComplete(int token, Object cookie, Uri uri);
		void onUpdateComplete(int token, Object cookie, int result);
	}

	public JewelChatAsyncQueryHandler(ContentResolver cr, AsyncQueryListener listener) {
		super(cr);
		setQueryListener(listener);
	}

	public void setQueryListener(AsyncQueryListener listener) {
		mListener = new WeakReference<AsyncQueryListener>(listener);
	}

	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		final AsyncQueryListener listener = mListener.get();
		if (listener != null) {
			listener.onQueryComplete(token, cookie, cursor);
		} else if (cursor != null) {
			cursor.close();
		}
	}

	protected void onDeleteComplete(int token, Object cookie, int result){
		final AsyncQueryListener listener = mListener.get();
		if (listener != null) {
			listener.onDeleteComplete(token, cookie, result);
		}
	}
	protected void onInsertComplete(int token, Object cookie, Uri uri){
		final AsyncQueryListener listener = mListener.get();
		if (listener != null) {
			listener.onInsertComplete(token, cookie, uri);
		}
	}
	protected void onUpdateComplete(int token, Object cookie, int result){
		final AsyncQueryListener listener = mListener.get();
		if (listener != null) {
			listener.onUpdateComplete(token, cookie, result);
		}
	}

}
