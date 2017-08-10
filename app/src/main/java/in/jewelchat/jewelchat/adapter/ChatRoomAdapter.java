package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.screens.ActivityChatRoom;

/**
 * Created by mayukhchakraborty on 15/07/17.
 */

public class ChatRoomAdapter extends BaseAdapter<ChatRoomAdapter.ViewHolder> {

	private Context mContext;
	public ChatRoomAdapter.OnClickHandler mClickListener = null;
	private static ActivityChatRoom mInstance;

	public boolean isPicking;



	public ChatRoomAdapter(@NonNull OnClickHandler handler, @NonNull LinearLayoutManager manager, Context context) {
		super(handler, manager, context);
		this.mContext = context;
		this.mClickListener = handler;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chatroom_element, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		cursor.moveToPosition(position);
		holder.chatroom_msg_date.setVisibility(View.GONE);



		if(cursor.getLong(cursor.getColumnIndex(ChatMessageContract.CREATOR_ID))
				== JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0)){

			holder.jewel_type.setVisibility(View.GONE);
			holder.chatroom_item.setGravity(Gravity.RIGHT);
			holder.chatroom_item_card.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.colorAccent));
			holder.chatroom_item_text.setTextColor(Color.WHITE);
			holder.chatroom_item_time.setText(DateUtils.formatDateTime(this.mContext, cursor.getLong(4), DateUtils.FORMAT_SHOW_TIME));

			holder.chatroom_item_delivery_status.setVisibility(View.VISIBLE);

			int isSubmitted = cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_SUBMITTED));
			int isDelivery = cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_DELIVERED));
			int isRead = cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_READ));

			if(isRead>0)
				holder.chatroom_item_delivery_status.setImageResource(R.drawable.read);
			else if(isDelivery>0)
				holder.chatroom_item_delivery_status.setImageResource(R.drawable.delivered);
			else if(isSubmitted>0)
				holder.chatroom_item_delivery_status.setImageResource(R.drawable.submitted);
			else
				holder.chatroom_item_delivery_status.setImageResource(R.drawable.inserted);


		}else{

			holder.chatroom_item.setGravity(Gravity.LEFT);
			holder.chatroom_item_card.setCardBackgroundColor(Color.WHITE);
			holder.chatroom_item_text.setTextColor(Color.BLACK);

			if(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_JEWEL_PICKED))==1){
				holder.jewel_type.setVisibility(View.VISIBLE);
				holder.jewel.setVisibility(View.GONE);
				holder.jewel.setScaleX(1.0f); holder.jewel.setScaleY(1.0f);
				holder.jewel.setImageResource(getJewelDrawable(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE))));
			}else if(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_JEWEL_PICKED))==2 && ActivityChatRoom.getInstance().getIsPicking()){
				holder.jewel_type.setVisibility(View.VISIBLE);
				holder.jewel.setVisibility(View.VISIBLE);
				holder.jewel.setImageResource(getJewelDrawable(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE))));
				holder.jewel.setScaleX(0.5f); holder.jewel.setScaleY(0.5f);
			}else {
				holder.jewel_type.setVisibility(View.VISIBLE);
				holder.jewel.setVisibility(View.VISIBLE);
				holder.jewel.setImageResource(getJewelDrawable(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE))));
				holder.jewel.setScaleX(1.0f); holder.jewel.setScaleY(1.0f);
			}

			holder.chatroom_item_time.setText(DateUtils.formatDateTime(context, cursor.getLong(4), DateUtils.FORMAT_SHOW_TIME));
			holder.chatroom_item_delivery_status.setVisibility(View.GONE);

			//Log.i("room",cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_JEWEL_PICKED))
			//		+":"+cursor.getInt(cursor.getColumnIndex(ChatMessageContract.KEY_ROWID))
			//		+":"+cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE)));


			int isread = cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_READ));

			if(isread==0) {

				JSONObject readack = new JSONObject();
				try {
					readack.put("sender_id", JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0));
					readack.put("sender_msgid", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.KEY_ROWID)));
					readack.put("receiver_id", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.CHAT_ROOM)));
					readack.put("eventname", "msg_read");
					readack.put("chat_id", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.SENDER_MSG_ID)));

					JSONObject t = new JSONObject();
					t.put("data", readack);

					if (JewelChatApp.getJCSocket().getSocket().connected())
						JewelChatApp.getJCSocket().getSocket().emit("read", readack);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}


		holder.chatroom_msg_date.setVisibility(View.GONE);
		holder.chatroom_item_text.setText(cursor.getString(cursor.getColumnIndex(ChatMessageContract.MSG_TEXT)));




	}

	private int getJewelDrawable(int aid){
		if(aid == 3)
			return R.drawable.t3;
		else if(aid == 4)
			return R.drawable.t4;
		else if(aid == 5)
			return R.drawable.t5;
		else if(aid == 6)
			return R.drawable.t6;
		else if(aid == 7)
			return R.drawable.t7;
		else if(aid == 8)
			return R.drawable.t8;
		else if(aid == 9)
			return R.drawable.t9;
		else if(aid == 10)
			return R.drawable.t10;
		else if(aid == 11)
			return R.drawable.t11;
		else if(aid == 12)
			return R.drawable.t12;
		else if(aid == 13)
			return R.drawable.t13;
		else if(aid == 14)
			return R.drawable.t14;
		else if(aid == 15)
			return R.drawable.t15;
		else if(aid == 16)
			return R.drawable.t16;
		else if(aid == 17)
			return R.drawable.t17;
		else if(aid == 0)
			return R.drawable.diamond_small;
		else if(aid == 1)
			return R.drawable.coin;
		return 0;
	}

	@Override
	protected void sameItemCount(Cursor oldCursor) {
		notifyItemRangeChanged(0, cursor.getCount());
		oldCursor.close();
		notifyDataSetChanged();
	}

	@Override
	protected void itemInserted(Cursor oldCursor) {
		super.itemInserted(oldCursor);
		notifyDataSetChanged();

		layoutManager.scrollToPosition(0);

	}


	public class ViewHolder extends BaseAdapter.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

		public RelativeLayout chatroom_msg_date;
		public TextView msg_date;

		public RelativeLayout chatroom_item;
		public RelativeLayout jewel_type;
		public ImageView jewel;

		public CardView chatroom_item_card;
		public EmojiconTextView chatroom_item_text;
		public TextView chatroom_item_time;
		public ImageView chatroom_item_delivery_status;

		public ViewHolder(View itemView) {
			super(itemView);
			chatroom_msg_date = (RelativeLayout)itemView.findViewById(R.id.chatroom_msg_date);
			msg_date = (TextView)itemView.findViewById(R.id.msg_date);
			chatroom_item = (RelativeLayout)itemView.findViewById(R.id.chatroom_item);
			jewel_type = (RelativeLayout)itemView.findViewById(R.id.jewel_type);
			jewel = (ImageView)itemView.findViewById(R.id.jewel);
			jewel.setOnClickListener(this);

			chatroom_item_card = (CardView)itemView.findViewById(R.id.chatroom_item_card);
			chatroom_item_text = (EmojiconTextView)itemView.findViewById(R.id.chatroom_item_text);
			chatroom_item_time = (TextView)itemView.findViewById(R.id.chatroom_item_time);

			chatroom_item_delivery_status = (ImageView)itemView.findViewById(R.id.chatroom_item_delivery_status);
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
