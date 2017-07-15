package in.jewelchat.jewelchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.jewelchat.jewelchat.screens.ActivitySplashScreen;
import in.jewelchat.jewelchat.screens.DialogJewelStore;
import in.jewelchat.jewelchat.screens.DialogJewelStoreFull;

/**
 * Created by mayukhchakraborty on 06/03/16.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener  {

	protected String className;

	protected AppBarLayout appbarRoot;

	protected TextView jewel_count, LEVEL, LEVEL_SCORE;
	protected ProgressBar XP;
	protected ImageView jewel_store, toolbar_back;

	protected RelativeLayout jewel_store_button;

	protected DialogJewelStore jewelStoreDialog;
	protected AlertDialog noInternetDialog;
	protected AlertDialog _403Dialog;

	protected DialogJewelStoreFull jewelStoreFull;

	protected ViewGroup rootLayout;



	public void jewelStoreFull(){
		Snackbar snackbar = Snackbar
				.make(rootLayout, "Jewel Store full", Snackbar.LENGTH_LONG);

		snackbar.show();
	}

	public void snackbarMsg(String msg){
		Snackbar snackbar = Snackbar
				.make(rootLayout, msg+"...", Snackbar.LENGTH_LONG);

		snackbar.show();
	}

	public void networkErrorMessage(String msg){
		Snackbar snackbar = Snackbar
				.make(rootLayout, msg+"...", Snackbar.LENGTH_LONG);

		snackbar.show();
	}



	public void noInternet(){
		Snackbar snackbar = Snackbar
				.make(rootLayout, "Internet connection lost.", Snackbar.LENGTH_LONG);

		snackbar.show();
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		className = getClass().getSimpleName();
		JewelChatApp.appLog(Log.INFO, "Activity",className + ":onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStop() {
		JewelChatApp.appLog(Log.INFO, "Activity",className + ":onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		JewelChatApp.appLog(Log.INFO, "Activity",className + ":onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		JewelChatApp.appLog(Log.INFO, "Activity",className + ":onPause");
		super.onPause();
		JewelChatApp.getBusInstance().unregister(this);
	}

	@Override
	protected void onResume() {
		JewelChatApp.appLog(Log.INFO, "Activity",className + ":onResume");
		super.onResume();
		JewelChatApp.getBusInstance().register(this);
		JewelChatApp.getBusInstance().post(JewelChatApp.produceJewelChangeEvent());
	}

	@Override
	protected void onStart() {
		JewelChatApp.appLog(Log.INFO, "Activity", className + ":onStart");
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

		if( noInternetDialog == null ) {
			noInternetDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme)).create();
			noInternetDialog.setTitle("No Internet");
			noInternetDialog.setMessage("There is no internet connection.");
			noInternetDialog.show();
			return;
		}else{
			noInternetDialog.show();
			return;
		}
	}

	protected void show403Dialog() {

		if( _403Dialog == null ) {
			_403Dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme)).create();
			_403Dialog.setTitle("Verification Error");
			_403Dialog.setMessage("Your phone number is registered with another device. Please verify your number.");
			_403Dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(getApplicationContext(), ActivitySplashScreen.class);
					startActivity(intent);
					finish();
				}
			});
			_403Dialog.show();
			return;
		}else{
			_403Dialog.show();
			return;
		}
	}



	protected void setUpAppbar() {
		JewelChatApp.appLog(Log.INFO, "Activity", className + ":setUpAppbar");
		appbarRoot = (AppBarLayout)findViewById(R.id.appbar);

		jewel_store_button = (RelativeLayout)appbarRoot.findViewById(R.id.jewel_store);

		LEVEL =  (TextView)appbarRoot.findViewById(R.id.level_appbar);
		XP = (ProgressBar)appbarRoot.findViewById(R.id.xpbar);
		LEVEL_SCORE = (TextView)appbarRoot.findViewById(R.id.xpbar_value);

		Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		toolbar_back = (ImageView) appbarRoot.findViewById(R.id.toolbar_back);
		toolbar_back.setVisibility(View.GONE);



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

/*

*/


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
