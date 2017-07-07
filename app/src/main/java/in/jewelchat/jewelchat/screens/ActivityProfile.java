package in.jewelchat.jewelchat.screens;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ProfileSliderPagerAdapter;

/**
 * Created by mayukhchakraborty on 23/06/17.
 */

public class ActivityProfile extends BaseNetworkActivity implements Response.Listener<JSONObject> {

	private ViewPager vp_slider;
	private LinearLayout ll_dots;
	private View[] dots;
	ProfileSliderPagerAdapter sliderPagerAdapter;
	ArrayList<String> slider_image_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		setUpAppbar();
	}

	@Override
	public void onResponse(JSONObject response) {
		slider_image_list = new ArrayList<String>();
		init();
		addBottomDots(0);
	}


	private void init() {

		vp_slider = (ViewPager) findViewById(R.id.vp_slider);
		ll_dots = (LinearLayout) findViewById(R.id.ll_dots);


		sliderPagerAdapter = new ProfileSliderPagerAdapter(ActivityProfile.this, this.slider_image_list);
		vp_slider.setAdapter(sliderPagerAdapter);

		vp_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				addBottomDots(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	private void addBottomDots(int currentPage) {
		dots = new ImageView[slider_image_list.size()];

		ll_dots.removeAllViews();
		for (int i = 0; i < dots.length; i++) {
			ImageView t = new ImageView(this);
			t.setImageResource(R.drawable.indicator_b);
			dots[i] = t;
			ll_dots.addView(dots[i]);

		}

		if (dots.length > 0)
			((ImageView)dots[currentPage]).setImageResource(R.drawable.indicator_w);
	}


	@Override
	public void onClick(View view) {

	}


}
