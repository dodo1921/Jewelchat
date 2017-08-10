package in.jewelchat.jewelchat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import github.ankushsachdeva.emojicon.EmojiconTextView;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class ChatListAdapter extends CursorAdapter {

	public ChatListAdapter(Context context) {
		super(context, null, false);
	}



	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.fragment_chat_element, parent, false);

		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ImageView vContactAvatar = (ImageView) view.findViewById(R.id.contact_image);
		TextView vContactName = (TextView) view.findViewById(R.id.contact_name);
		EmojiconTextView vLastPost = (EmojiconTextView) view.findViewById(R.id.lastpost);
		TextView vMsgTime = (TextView) view.findViewById(R.id.lastposttime);
		TextView vUnreadCounter = (TextView) view.findViewById(R.id.unread_chat_count);

		int jewelchat_id = cursor.getInt(0);
		String image = cursor.getString(3);

		//if(jewelchat_id == JewelChatApp.getSharedPref().getInt(JewelChatPrefs.TEAM_JC_ID,0)) {
		//	vContactAvatar.setImageResource(R.drawable.diamond_small);
		//	vContactName.setText("Team JewelChat");
		//}else{
			if(jewelchat_id == JewelChatApp.getSharedPref().getInt(JewelChatPrefs.TEAM_JC_ID,0)) {
				vContactAvatar.setImageResource(R.drawable.diamond_small);
				//vContactName.setText("Team JewelChat");
			}else if(image == null){
				vContactAvatar.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.gray));
				vContactAvatar.setImageResource(R.drawable.person);
				Log.i("ChatListAdapter IF",image+"");
			}else{
				byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				vContactAvatar.setImageBitmap(decodedByte);
				Log.i("ChatListAdapter ELSE",image+"");
			}
			//vContactAvatar.setImageResource(R.drawable.person);
			vContactName.setText(cursor.getString(1));
		//}

		long time = cursor.getLong(13);
		//Log.i("TIME", time+"");
		vMsgTime.setText(DateUtils.getRelativeTimeSpanString(context, time));

		if(cursor.getInt(10) > 0) {
			vUnreadCounter.setVisibility(View.VISIBLE);
			vUnreadCounter.setText(cursor.getInt(10)+"");
		}else{
			vUnreadCounter.setVisibility(View.GONE);
		}

		int type = cursor.getInt(11);
		if( type==1 ) {
			vLastPost.setText(cursor.getString(12).length()>30?(cursor.getString(12).subSequence(0,29)+"..."):cursor.getString(12));
		}else if( type == 2 || type == 4 )
			vLastPost.setText("Image");
		else if( type ==3 )
			vLastPost.setText("Video");
		else
			vLastPost.setText("");


	}
}
