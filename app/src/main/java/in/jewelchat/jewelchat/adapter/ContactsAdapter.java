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
			holder.contact_item_invite.setText("INVITED");
		}else if(is_invited == 0){
			holder.contact_item_invite.setVisibility(View.VISIBLE);
			holder.contact_item_invite.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.colorAccent));

		}

		//String x= "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAFA3PEY8MlBGQUZaVVBfeMiCeG5uePWvuZHI////////////////////////////////////////////////////2wBDAVVaWnhpeOuCguv/////////////////////////////////////////////////////////////////////////wAARCAAtAC0DASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwC5TS/ZQTStz16VCZudq4AHFADmcqeTUisCoNQuFdOOvrQ6uIVC/iKBEwdSM7hilqpA6K2MYPoatjmkmMjl3FSF61CsRC7cZz3q1SMcKcYziiwDY4wo96fTMvuYAj2zSjdnkjGaYEU8O8ZX7wp6FguD1qSjFKwBUTbjIG8rOPU808oCSSTz70FBxyePemBHgkHMI5PTP1oKg8eUD+PenFArDluff2pHIVA2CfxoAdECq7cYA6c9akqMRqG6tz7+lOUbVAJz70Af/9k=";
		//String x = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAFA3PEY8MlBGQUZaVVBfeMiCeG5uePWvuZHI////////////////////////////////////////////////////2wBDAVVaWnhpeOuCguv/////////////////////////////////////////////////////////////////////////wAARCABQAFADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwB24L0yTRln6dPWkCqvXk0yaU4wO9IY5pVTuWNRNOxP/wBeoyOAc9aQDJwKdhEnmn3/ADpwkz3Ip8dsCMuae9um35etK6GNGT3/AFp24L3yag5Q4PSnZz9aq19hDmkJPWkyx7mkUVKAB1qRkRNRScmpdoHU5pr4I4FCAhqxBHn5jUGOcVejXEYHemwQpbFJuqJ2ZWwVp65NSMbKuRUGcVYY1WbhjVREx+7vUiFcZJ/CoO1SREbhnvTeoiQ7KWNFcnjgURQbjkkkVYWNUzt4zUjM8ptfaeueKuA4FOeJZByMMOhpmcHDcGhghxbjpQHGDUbOBTVdX6Uihsqk8qagNXD0qo/3qaJYnY1JCNzqKip8bbXB9DViNELgYHApvz5B/OnE00OO3aoADuAXJyTwfaldQ4w350nmDjjninkZFMCo6tGeeR2NIMDmrLYI2sMiqs0TR8qSU/lSHcV3wKgPNLtzznNHsaAGUoop0K5lUe9UI0iAaRUA9PyprDJPzGgLzknNSA7avoKQsR2pNgyOeB29aFByRknigBeopvselIA3cge1OPIpDKbr5UuP4T0odalnR22jaevWjaW6CmBWNS2q5lz6CmyRsvUVLZD5mPbFV0Ef/9k=";
		//byte[] decodedString = Base64.decode(x, Base64.DEFAULT);
		//Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		//holder.contact_image.setImageBitmap(decodedByte);

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
				mClickListener.onItemClick(view, cursor, getAdapterPosition());
		}

		@Override
		public boolean onLongClick(View v) {

			return true;
		}


	}

}
