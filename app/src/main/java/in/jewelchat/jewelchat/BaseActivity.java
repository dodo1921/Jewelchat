package in.jewelchat.jewelchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.screens.ActivitySplashScreen;
import in.jewelchat.jewelchat.screens.DialogJewelStore;
import in.jewelchat.jewelchat.screens.DialogJewelStoreFull;
import in.jewelchat.jewelchat.screens.DialogNoInternet;

/**
 * Created by mayukhchakraborty on 06/03/16.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener  {

	protected String className;

	protected AppBarLayout appbarRoot;

	protected TextView jewel_count, LEVEL, LEVEL_SCORE;
	protected ProgressBar XP;
	protected ImageView jewel_store;

	protected RelativeLayout jewel_store_button;

	protected DialogJewelStore jewelStoreDialog;
	protected DialogNoInternet noInternetDialog;

	protected DialogJewelStoreFull jewelStoreFull;

	protected ViewGroup rootLayout;



	public void jewelStoreFull(){
		Snackbar snackbar = Snackbar
				.make(rootLayout, "Jewel Store full", Snackbar.LENGTH_LONG);

		snackbar.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		className = getClass().getSimpleName();
		JewelChatApp.appLog(className + ":onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStop() {
		JewelChatApp.appLog(className + ":onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		JewelChatApp.appLog(className + ":onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		JewelChatApp.appLog(className + ":onPause");
		super.onPause();
		JewelChatApp.getBusInstance().unregister(this);
	}

	@Override
	protected void onResume() {
		JewelChatApp.appLog(className + ":onResume");
		super.onResume();
		JewelChatApp.getBusInstance().register(this);
		JewelChatApp.getBusInstance().post(JewelChatApp.produceJewelChangeEvent());
	}

	@Override
	protected void onStart() {
		JewelChatApp.appLog(className + ":onStart");
		super.onStart();

	}

	protected void initialize() {

	}

	protected void showJewelStoreFullDialog(){

		jewelStoreFull = new DialogJewelStoreFull();
		jewelStoreFull.show(getFragmentManager(), "Jewel Store Full");
		jewelStoreFull.setCancelable(true);
	}

	protected void showNoInternetDialog() {

		noInternetDialog = new DialogNoInternet();
		noInternetDialog.show(getFragmentManager(), "No Internet");
		noInternetDialog.setCancelable(true);

	}



	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		appbarRoot = (AppBarLayout)findViewById(R.id.appbar);

		jewel_store_button = (RelativeLayout)appbarRoot.findViewById(R.id.jewel_store);

		LEVEL =  (TextView)appbarRoot.findViewById(R.id.level_appbar);
		XP = (ProgressBar)appbarRoot.findViewById(R.id.xpbar);
		LEVEL_SCORE = (TextView)appbarRoot.findViewById(R.id.xpbar_value);

		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		//setSupportActionBar(toolbar);

		jewel_store_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				jewelStoreDialog = new DialogJewelStore();
				jewelStoreDialog.show( getFragmentManager(), "Jewel Store");
				jewelStoreDialog.setCancelable(true);

			}
		});

	}


	@Subscribe
	public void OnNoInternetEvent( NoInternet event) {

		AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).create();
		dialog.setTitle("No Internet");
		dialog.setMessage("There is no Internet connection...");
		dialog.setCancelable(true);
		dialog.show();
		return;

	}



	@Subscribe
	public void on_403NetworkErrorEvent( _403NetworkErrorEvent event) {

		AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).create();
		dialog.setTitle("Verification Error");
		dialog.setMessage("Your phone number is registered with another device. Please verify your number.");
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(getApplicationContext(), ActivitySplashScreen.class);
				startActivity(intent);
				finish();
			}
		});
		dialog.show();
		return;

	}

	@Subscribe
	public void onGameStateChangeEvent( GameStateChangeEvent event) {

		Log.i(">>>>>>>", event.TOTAL+" ");
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

	protected void makeToast(String message) {
		if (getApplicationContext() != null)
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public void showPopUp(View v){
		PopupMenu popup = new PopupMenu(this, v);
		popup.setOnMenuItemClickListener(this);
		popup.inflate(R.menu.general_menu);
		popup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return false;
	}


}
