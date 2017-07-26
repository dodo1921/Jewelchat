package in.jewelchat.jewelchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.screens.FragmentAchievements;
import in.jewelchat.jewelchat.screens.FragmentChatList;
import in.jewelchat.jewelchat.screens.FragmentTasks;
import in.jewelchat.jewelchat.service.DownloadBlockedUserService;
import in.jewelchat.jewelchat.service.DownloadGroupsService;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.service.GroupChatDownloadService;
import in.jewelchat.jewelchat.service.OneToOneChatDownloadService;
import in.jewelchat.jewelchat.service.RegistrationIntentService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 20/06/17.
 */

public class JewelChat extends BaseNetworkActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private TabLayout tabLayout;


	private View tasksView;
	private View chatListView;
	private View achievementView;

	@Override
	public void onClick(View view) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jewelchat);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);

		setUpAppbar();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mSectionsPagerAdapter.addFrag( "Chats");
		mSectionsPagerAdapter.addFrag( "Tasks");
		mSectionsPagerAdapter.addFrag( "Achievements");

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(1);


		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);
		tabLayout.getTabAt(JewelChatApp.getSharedPref().getInt(JewelChatPrefs.LAST_TAB, 0)).select();



		tabLayout.addOnTabSelectedListener(
				new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
					@Override
					public void onTabSelected(TabLayout.Tab tab) {
						super.onTabSelected(tab);
						JewelChatApp.getSharedPref().edit().putInt(JewelChatPrefs.LAST_TAB, tab.getPosition()).apply();

					}
		});

		if(!JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.TOKEN_UPLOADED, false)) {
			Intent service = new Intent(getApplicationContext(), RegistrationIntentService.class);
			startService(service);
		}

		if(!JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.GROUPS_DOWNLOADED, false)) {
			Intent service4 = new Intent(getApplicationContext(), DownloadGroupsService.class);
			startService(service4);
		}

		if(!JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.BLOCKED_USERS_DOWNLOADED, false)) {
			Intent service5 = new Intent(getApplicationContext(), DownloadBlockedUserService.class);
			startService(service5);
		}

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


		getFirebaseCustomToken();

	}

	private void getFirebaseCustomToken() {


		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO, "CUSTOM FIREBASE TOKEN", "CUSTOM FIREBASE TOKEN" + ":onResponse");

				try {

					Boolean error = response.getBoolean("error");
					if (error) {
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}


					JewelChatApp.getFirebaseAuth().signInWithCustomToken(response.getString("customToken"));


				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}
			}

		};

		JSONObject t = new JSONObject();

		JewelChatRequest req = new JewelChatRequest(Request.Method.GET, JewelChatURLS.GETCUSTOMTOKEN, null, response,  this);
		if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
			JewelChatApp.getRequestQueue().add(req);
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

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {


		private final List<String> mFragmentTitleList = new ArrayList<>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0)
				return new FragmentChatList();
			else if(position==1)
				return new FragmentTasks(); //FragmentTasks();
			else if(position==2)
				return new FragmentAchievements();// FragmentAchievements();

			return null;

		}

		@Override
		public int getCount() {

			return 3;
		}

		public void addFrag( String title) {

			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}

	}


}
