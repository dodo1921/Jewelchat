package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

import in.jewelchat.jewelchat.BaseImageActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ChatRoomAdapter;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.service.DecrementUnreadCounterService;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.service.GroupChatDownloadService;
import in.jewelchat.jewelchat.service.OneToOneChatDownloadService;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class ActivityChatRoom extends BaseImageActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, ChatRoomAdapter.OnClickHandler,
		ChatRoomAdapter.ItemLongClickListener, Response.Listener<JSONObject>{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jewelchat);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);

		setUpAppbar();

		Intent service = new Intent(getApplicationContext(), DecrementUnreadCounterService.class);
		startService(service);

	}

	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		titleText.setText("Jewel Factory");

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent service = new Intent(getApplicationContext(), DecrementUnreadCounterService.class);
				startService(service);
				onBackPressed();
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();

		Intent service1 = new Intent(getApplicationContext(), GameStateLoadService.class);
		startService(service1);

		Intent service2 = new Intent(getApplicationContext(), OneToOneChatDownloadService.class);
		Bundle b2 = new Bundle();
		b2.putInt("page", 0);
		service2.putExtras(b2);
		startService(service2);

		Intent service3 = new Intent(getApplicationContext(), GroupChatDownloadService.class);
		service3.putExtras(b2);
		startService(service3);


		if(!JewelChatApp.getJCSocket().getSocket().connected()){
			//remove level7 cookie
			JewelChatApp.setGCLB(null);
			JewelChatApp.getJCSocket().getSocket().connect();
		}

	}



	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}


	@Override
	public void onClick(View view) {

	}

	@Override
	public void onItemClick(View view, Cursor cursor) {

	}

	@Override
	public void onItemLongClick(View view, ArrayList<String> arrayList) {

	}

	@Override
	public void onResponse(JSONObject response) {

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
