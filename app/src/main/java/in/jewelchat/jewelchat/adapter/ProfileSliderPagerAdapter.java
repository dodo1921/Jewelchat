package in.jewelchat.jewelchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.jewelchat.jewelchat.R;

/**
 * Created by mayukhchakraborty on 23/06/17.
 */

public class ProfileSliderPagerAdapter extends PagerAdapter {
	private LayoutInflater layoutInflater;
	Activity activity;
	ArrayList<String> image_arraylist;

	public ProfileSliderPagerAdapter(Activity activity, ArrayList<String> image_arraylist) {
		this.activity = activity;
		this.image_arraylist = image_arraylist;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.layout_slider, container, false);
		ImageView im_slider = (ImageView) view.findViewById(R.id.im_slider);
		Picasso.with(activity).load(image_arraylist.get(position))
				.placeholder(R.drawable.person).error(R.drawable.person).into(im_slider);

		container.addView(view);

		return view;
	}

	@Override
	public int getCount() {
		return image_arraylist.size();
	}


	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}
}
