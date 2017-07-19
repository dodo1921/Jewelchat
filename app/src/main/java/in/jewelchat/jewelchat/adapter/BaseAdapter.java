package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by mayukhchakraborty on 17/07/17.
 */

public abstract class BaseAdapter<VH extends BaseAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

	protected final Context context;
	protected Cursor cursor;
	protected WeakReference<OnClickHandler> mClickHandler;
	protected LinearLayoutManager layoutManager;

	public BaseAdapter(@NonNull OnClickHandler handler, @NonNull LinearLayoutManager manager, Context context) {
		this.mClickHandler = new WeakReference<>(handler);
		this.layoutManager = manager;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		if (cursor == null)
			return 0;
		else
			return cursor.getCount();
	}


	public void swapCursor(Cursor cursor) {
		Log.i("swapCursor","swapCursor");
		Cursor oldCursor = this.cursor;
		this.cursor = cursor;

		if (oldCursor != null && cursor != null && cursor.getCount() != 0 && oldCursor.getCount() != 0) {
			if (oldCursor.getCount() == cursor.getCount()) {
				sameItemCount(oldCursor);
			} else if (oldCursor.getCount() < cursor.getCount()) {
				itemInserted(oldCursor);
			} else {
				int removeCount = oldCursor.getCount() - cursor.getCount();
				int removed = 0;
				for (int i = 0; (i < cursor.getCount()) && (removed < removeCount); i++) {
					oldCursor.moveToPosition(i);
					cursor.moveToPosition(i - removed);
					if (!cursor.getString(0).equals(oldCursor.getString(0))) {
						notifyItemRemoved(i);
						removed++;
					}
				}
				if (removed < removeCount) {
					notifyItemRangeRemoved(cursor.getCount(), (removeCount - removed));
					notifyItemChanged(cursor.getCount());
				}
			}
		} else {
			notifyDataSetChanged();
		}
		if (oldCursor != null)
			oldCursor.close();
	}

	protected void sameItemCount(Cursor oldCursor) {

	}

	protected void itemInserted(Cursor oldCursor) {
		Log.i("itemInserted","itemInserted");
		int insertCount = cursor.getCount() - oldCursor.getCount();
		int inserted = 0;
		for (int i = 0; (i < oldCursor.getCount()) && (inserted < insertCount); i++) {
			oldCursor.moveToPosition(i);
			cursor.moveToPosition(i + inserted);
			if (!cursor.getString(0).equals(oldCursor.getString(0))) {
				notifyItemInserted(i);
				inserted++;
			}
		}
	}

	public interface OnClickHandler {

		void onItemClick(View view, Cursor cursor);
	}

	public interface OnLongClickHandler {

		void onItemLongClick(View view, Cursor cursor);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		public ViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public void onClick(View view) {
			cursor.moveToPosition(getAdapterPosition());
			OnClickHandler handler = mClickHandler.get();
			if (handler != null)
				handler.onItemClick(view, cursor);
		}

		@Override
		public boolean onLongClick(View view) {
			cursor.moveToPosition(getAdapterPosition());
			OnClickHandler handler = mClickHandler.get();
			if (handler != null) {
				handler.onItemClick(view, cursor);
				return true;
			}
			return false;
		}

	}
}