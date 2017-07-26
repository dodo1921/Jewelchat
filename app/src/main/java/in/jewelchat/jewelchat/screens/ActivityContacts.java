package in.jewelchat.jewelchat.screens;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ContactsAdapter;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;
import in.jewelchat.jewelchat.database.database_crud.UpdateContact;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.service.FirstTimeContactDownloadService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class ActivityContacts extends BaseNetworkActivity implements LoaderManager.LoaderCallbacks<Cursor> , ContactsAdapter.OnClickHandler   {

	private ContactsAdapter contactListAdapter;
	private RecyclerView recyclerView;
	private ContactsAdapter contactsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);

		setUpAppbar();

		Log.i(">>>"+JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.CONTACTS_DOWNLOAED, false), "OMG");


		Intent service = new Intent(getApplicationContext(), FirstTimeContactDownloadService.class);
		startService(service);


		this.recyclerView = (RecyclerView)findViewById(R.id.contact_list);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager.setReverseLayout(true);
		recyclerView.setLayoutManager(layoutManager);
		this.contactsAdapter = new ContactsAdapter(this, layoutManager, this);
		this.recyclerView.setAdapter(contactsAdapter);

		getSupportLoaderManager().initLoader(1, null, this);




	}

	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		titleText.setText("Contacts");

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}


	@Override
	public void onClick(View view) {

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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/"+ ContactContract.SQLITE_TABLE_NAME);
		CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
				uri, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.contactsAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.contactsAdapter.swapCursor(null);
	}



	private void sendInvite(Cursor c) {

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO, "SENDINVITE", "SENDINVITE" + ":onResponse");

				try {

					Boolean error = response.getBoolean("error");
					if (error) {
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					int invite = response.getInt("invite");
					boolean is_regis = response.getBoolean("is_regis");

					ContentValues cv = new ContentValues();
					cv.put(ContactContract.IS_INVITED, invite);

					Uri urimsg = Uri.parse(JewelChatDataProvider.SCHEME + "://" + JewelChatDataProvider.AUTHORITY + "/" + ContactContract.SQLITE_TABLE_NAME);
					getContentResolver().update(urimsg, cv, ContactContract.CONTACT_NUMBER + " = ? ", new String[]{ response.getLong("phone")+"" } );

					if(is_regis){
						JSONObject packet  = response.getJSONObject("contact");

						Intent s = new Intent(JewelChatApp.getInstance(), UpdateContact.class);
						s.putExtra("json", packet.toString());
						JewelChatApp.getInstance().startService(s);
					}


					dismissDialog();

				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}
			}

		};

		JSONObject t = new JSONObject();
		try {
			t.put("phone", c.getLong(c.getColumnIndex(ContactContract.CONTACT_NUMBER)));
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.INVITE, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}



	private void downloadContact(Cursor c) {

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO,"DOWNLOADCONTACTS","DOWNLOADCONTACTS" + ":onResponse");
				dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					JSONObject packet  = response.getJSONObject("contact");

					Intent s = new Intent(JewelChatApp.getInstance(), UpdateContact.class);
					s.putExtra("json", packet.toString());
					JewelChatApp.getInstance().startService(s);



				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}

			}
		};



		JSONObject t = new JSONObject();
		try {
			t.put("phone", c.getLong(c.getColumnIndex(ContactContract.CONTACT_NUMBER)));
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.DOWNLOADCONTACT_PHONE, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(View view, Cursor cursor, int adapterPosition) {

		cursor.moveToPosition(adapterPosition);

		Log.i("Invite>>>", "Invite>>>>"+view.getId()+":::"+R.id.contact_item_invite);

		int is_invited = cursor.getInt(cursor.getColumnIndex(ContactContract.IS_INVITED));
		int is_regis = cursor.getInt(cursor.getColumnIndex(ContactContract.IS_REGIS));

		if( is_invited==0 && view.getId() == R.id.contact_item_invite){
			//Log.i("Invite", "Invite");
			sendInvite(cursor);
		}else if( is_regis==0 && is_invited==1 && view.getId() == R.id.contact_element){
			//Log.i("Invite", "Invite");
			downloadContact(cursor);
		}else if(is_regis==1 && view.getId() == R.id.contact_element){
			Log.i("Invite>>>", "Invite>>>>");
			//open chatroom
			Bundle bundle = new Bundle();
			bundle.putInt(ContactContract.JEWELCHAT_ID, cursor.getInt(0));
			bundle.putString(ContactContract.CONTACT_NAME, cursor.getString(1));
			bundle.putLong(ContactContract.CONTACT_NUMBER, cursor.getLong(2));
			bundle.putString(ContactContract.IMAGE_PATH, cursor.getString(3));
			bundle.putInt(ChatMessageContract.CHAT_ROOM, cursor.getInt(17));
			bundle.putBoolean(ContactContract.IS_GROUP, cursor.getInt(5)==1?true:false);

			Intent i = new Intent(this, ActivityChatRoom.class);
			i.putExtras(bundle);
			startActivity(i);
		}


	}



}
