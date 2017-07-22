package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.screens.ActivityContacts;

/**
 * Created by mayukhchakraborty on 21/07/17.
 */

public class ContactsAdapter extends BaseAdapter<ContactsAdapter.ViewHolder>  {

	private Context mContext;
	public ContactsAdapter.OnClickHandler mClickListener = null;
	private static ActivityContacts mInstance;

	public ContactsAdapter(@NonNull OnClickHandler handler, @NonNull LinearLayoutManager manager, Context context) {
		super(handler, manager, context);
		this.mContext = context;
		this.mClickListener = handler;
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

		//layoutManager.scrollToPosition(0);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contacts_element, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {

		cursor.moveToPosition(position);
		long contact_number = cursor.getLong(cursor.getColumnIndex(ContactContract.CONTACT_NUMBER));
		String contact_name = cursor.getString(cursor.getColumnIndex(ContactContract.PHONEBOOK_CONTACT_NAME));
		String jewelchat_contact_name = cursor.getString(cursor.getColumnIndex(ContactContract.CONTACT_NAME));
		String image_phonebook = cursor.getString(cursor.getColumnIndex(ContactContract.IMAGE_PHONEBOOK));
		int is_invited = cursor.getInt(cursor.getColumnIndex(ContactContract.IS_INVITED));
		int is_regis = cursor.getInt(cursor.getColumnIndex(ContactContract.IS_REGIS));

		if(is_regis==1) {
			holder.contact_item_invite.setVisibility(View.GONE);
			holder.contact_item_invite.setOnClickListener(null);
		}else if(is_invited==1){
			holder.contact_item_invite.setVisibility(View.VISIBLE);
			holder.contact_item_invite.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.gray));
		}else if(is_invited == 0){
			holder.contact_item_invite.setVisibility(View.VISIBLE);
			holder.contact_item_invite.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.colorAccent));

		}

		if(!(image_phonebook==null) && !image_phonebook.equals(""))
			holder.contact_image.setImageURI(Uri.parse(image_phonebook));
		else {
			holder.contact_image.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.gray));
			holder.contact_image.setImageResource(R.drawable.person);
		}

		if( contact_name==null || contact_name.equals("") ){
			if(jewelchat_contact_name!=null && !jewelchat_contact_name.equals("")){
				holder.contact_name.setText(jewelchat_contact_name);
				holder.contact_name.setText("+" + contact_number);
			}else{
				holder.contact_name.setText("+"+contact_number);
				holder.contact_number.setText("");
			}
		}else{
			holder.contact_name.setText(contact_name);
			holder.contact_number.setText("+" + contact_number);
		}


	}

	public class ViewHolder extends BaseAdapter.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

		public RelativeLayout contact_element;
		public ImageView contact_image;
		public TextView contact_name;
		public TextView contact_number;
		public TextView contact_item_invite;

		public ViewHolder(View itemView) {
			super(itemView);
			contact_element = (RelativeLayout)itemView.findViewById(R.id.contact_element);
			contact_element.setOnClickListener(this);
			contact_image = (ImageView)itemView.findViewById(R.id.contact_image);
			contact_name = (TextView)itemView.findViewById(R.id.contact_name);
			contact_number = (TextView)itemView.findViewById(R.id.contact_number);
			contact_item_invite = (TextView)itemView.findViewById(R.id.contact_item_invite);
			contact_item_invite.setOnClickListener(this);

		}

		@Override
		public void onClick(View view) {
			if (mClickListener != null)
				mClickListener.onItemClick(view, cursor);
		}

		@Override
		public boolean onLongClick(View v) {

			return true;
		}


	}

}
