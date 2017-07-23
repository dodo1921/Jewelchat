package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import in.jewelchat.jewelchat.BaseActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;

/**
 * Created by mayukhchakraborty on 23/07/17.
 */

public class ActivityEditProfileName extends BaseActivity {

	private EditText profile_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile_name);


		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		setUpAppbar();

		profile_name = (EditText) findViewById(R.id.edit_profile_name_text);
		profile_name.setText(JewelChatApp.getSharedPref().getString(JewelChatPrefs.NAME, ""));

		Button buttonCancel = (Button) findViewById(R.id.edit_profile_name_cancel);
		buttonCancel.setOnClickListener(this);
		Button buttonSave = (Button) findViewById(R.id.edit_profile_name_save);
		buttonSave.setOnClickListener(this);

	}

	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		titleText.setText("Edit Profile Name");

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
			case R.id.edit_profile_name_cancel:
				JewelChatApp.appLog(className + ":nameCancel");
				intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
				break;
			case R.id.edit_profile_name_save:
				JewelChatApp.appLog(className + ":nameSave");
				intent = new Intent();
				Bundle bundle = new Bundle();
				String name = profile_name.getText().toString();
				bundle.putString(ActivityEditProfile.CONTENT, name);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}

	@Subscribe
	public void onGameStateChangeEvent( GameStateChangeEvent event) {

		ImageView store_image= 	(ImageView)appbarRoot.findViewById(R.id.jewel_store_image);
		if(event.TOTAL==0) {
			store_image.setImageResource(R.drawable.js_empty);
		}else if(event.TOTAL>0 && event.TOTAL<25){
			store_image.setImageResource(R.drawable.js_half);
		}else if(event.TOTAL == 25){
			store_image.setImageResource(R.drawable.js_full);
		}
		LEVEL.setText(event.LEVEL+"");
		XP.setMax(event.LEVEL_XP);XP.setProgress(event.XP);
		LEVEL_SCORE.setText(event.XP+"/"+event.LEVEL_XP);

	}

	@Subscribe
	public void OnNoInternetEvent( NoInternet event) {
		//showNoInternetDialog();
		noInternet();
	}

	@Subscribe
	public void on_403NetworkErrorEvent( _403NetworkErrorEvent event) {
		show403Dialog();
	}
}
