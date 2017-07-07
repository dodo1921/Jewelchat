package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import in.jewelchat.jewelchat.BaseActivity;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.SliderPagerAdapter;

/**
 * Created by mayukhchakraborty on 08/06/17.
 */

public class ActivityMobileEntry extends BaseActivity{

	private Button submit;
	private ViewPager vp_slider;
	private LinearLayout ll_dots;
	private View[] dots;
	SliderPagerAdapter sliderPagerAdapter;
	ArrayList<Integer> slider_image_list;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_entry);
		submit = (Button) findViewById(R.id.start);
		submit.setOnClickListener(this);
		init();
		addBottomDots(0);
	}
	
	private void init() {

		vp_slider = (ViewPager) findViewById(R.id.vp_slider);
		ll_dots = (LinearLayout) findViewById(R.id.ll_dots);

		slider_image_list = new ArrayList<Integer>();
		slider_image_list.add(0,R.drawable.t3);
		slider_image_list.add(1,R.drawable.t4);
		slider_image_list.add(2,R.drawable.t5);
		slider_image_list.add(3,R.drawable.t6);


		sliderPagerAdapter = new SliderPagerAdapter(ActivityMobileEntry.this, slider_image_list);
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
	protected void setUpAppbar() {

	}


	@Override
	public void onClick(View view) {

		Intent i = new Intent(this, ActivityRegistration.class);
		startActivity(i);
		finish();


	}


}
