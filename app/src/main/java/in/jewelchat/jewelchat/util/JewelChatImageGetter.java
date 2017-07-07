package in.jewelchat.jewelchat.util;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import in.jewelchat.jewelchat.JewelChatApp;

/**
 * Created by mayukhchakraborty on 21/06/17.
 */

public class JewelChatImageGetter implements Html.ImageGetter {
	@Override
	public Drawable getDrawable(String source) {

		int id = 0;

		/*

		if(source.equals("A")){
			id = R.drawable.ic_ac;
		}else if(source.equals("B")){
			id = R.drawable.ic_bc;
		}else if(source.equals("C")){
			id = R.drawable.ic_cc;
		}else if(source.equals("D")){
			id = R.drawable.ic_dc;
		}
		*/
		if(id==0)
			return null;

		Drawable d =  ContextCompat.getDrawable(JewelChatApp.getInstance().getApplicationContext(), id);
		d.setBounds(0, 0, 45, 45);

		return d;
	}
}
