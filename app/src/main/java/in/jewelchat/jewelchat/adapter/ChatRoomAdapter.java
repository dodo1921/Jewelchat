package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mayukhchakraborty on 15/07/17.
 */

public class ChatRoomAdapter extends BaseAdapter<ChatRoomAdapter.ViewHolder> {

	public ChatRoomAdapter(@NonNull OnClickHandler handler, @NonNull LinearLayoutManager manager, Context context) {
		super(handler, manager, context);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

	}

	public interface ItemLongClickListener {

		void onItemLongClick(View view, ArrayList<String> arrayList);
	}

	public class ViewHolder extends BaseAdapter.ViewHolder implements View.OnLongClickListener, View.OnClickListener {



		public ViewHolder(View itemView) {
			super(itemView);

		}

		@Override
		public void onClick(View view) {
			super.onClick(view);

		}

		@Override
		public boolean onLongClick(View v) {

			return true;
		}


	}


}
