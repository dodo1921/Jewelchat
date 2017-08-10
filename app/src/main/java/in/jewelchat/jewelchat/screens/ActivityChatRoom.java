package in.jewelchat.jewelchat.screens;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import in.jewelchat.jewelchat.BaseImageActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.adapter.ChatRoomAdapter;
import in.jewelchat.jewelchat.database.ChatMessageContract;
import in.jewelchat.jewelchat.database.ContactContract;
import in.jewelchat.jewelchat.database.JewelChatDataProvider;
import in.jewelchat.jewelchat.database.database_crud.InsertSelfMessage;
import in.jewelchat.jewelchat.database.database_crud.PickJewelService;
import in.jewelchat.jewelchat.database.database_crud.UpdateContact;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.service.DecrementUnreadCounterService;
import in.jewelchat.jewelchat.service.GameStateLoadService;
import in.jewelchat.jewelchat.service.GroupChatDownloadService;
import in.jewelchat.jewelchat.service.OneToOneChatDownloadService;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

/**
 * Created by mayukhchakraborty on 22/06/17.
 */

public class ActivityChatRoom extends BaseImageActivity implements LoaderManager.LoaderCallbacks<Cursor> , ChatRoomAdapter.OnClickHandler{

	private static ActivityChatRoom mInstance;
	private boolean pickingJewel;

	private int jewelchat_id;
	private int chatroom;
	private boolean is_group;
	private String contact_name;
	private long contact_number;
	private String image_path;

	private RecyclerView recyclerView;
	private ChatRoomAdapter chatRoomAdapter;
	private CursorLoader chatRoomCursorLoader;

	private ImageView chatroom_send;
	private EditText chatroom_text_input;



	public static ActivityChatRoom getInstance() {
		if(mInstance == null)
			mInstance = new ActivityChatRoom();
		return mInstance;
	}

	public void setIsPicking(boolean flag){
		this.pickingJewel = flag;
	}

	public boolean getIsPicking(){
		return this.pickingJewel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		mInstance = this;

		Bundle bundle = getIntent().getExtras();
		this.jewelchat_id = bundle.getInt(ContactContract.JEWELCHAT_ID);
		this.chatroom = bundle.getInt(ChatMessageContract.CHAT_ROOM);
		this.is_group = bundle.getBoolean(ContactContract.IS_GROUP);
		this.contact_name = bundle.getString(ContactContract.CONTACT_NAME);
		this.contact_number = bundle.getLong(ContactContract.CONTACT_NUMBER);
		this.image_path = bundle.getString(ContactContract.IMAGE_PATH);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		setUpAppbar();

		setupChatBottomBar();

		Intent service = new Intent(getApplicationContext(), DecrementUnreadCounterService.class);
		service.putExtra("chatroom", this.chatroom);
		startService(service);

		downloadContact();

		this.recyclerView = (RecyclerView)findViewById(R.id.chat_room_list);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		layoutManager.setReverseLayout(true);
		recyclerView.setLayoutManager(layoutManager);
		this.chatRoomAdapter = new ChatRoomAdapter(this, layoutManager, this);
		this.recyclerView.setAdapter(chatRoomAdapter);

		getSupportLoaderManager().initLoader( 0, null, this );

	}

	protected void setupChatBottomBar(){

		chatroom_send = (ImageView)findViewById(R.id.chatroom_send);
		chatroom_send.setOnClickListener(this);

		chatroom_text_input = (EditText)findViewById(R.id.chatroom_text_input);
		chatroom_text_input.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(chatroom_text_input, InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		ImageView profile_pic = (ImageView) appbarRoot.findViewById(R.id.toolbarImage);
		//profile_pic.setBackgroundColor(getResources().getColor(R.color.gray));

		if(this.contact_name == null)
			titleText.setText("+"+this.contact_number);
		else
			titleText.setText(this.contact_name.substring(0, this.contact_name.length()>16?16:this.contact_name.length() ) );

		//Picasso.with(getApplicationContext()).load(this.image_path)
		//		.placeholder(R.drawable.person)
		//		.error(R.drawable.person)
		//		.into(profile_pic);

		if(image_path==null || image_path.equals("")){
			profile_pic.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.gray));
			profile_pic.setImageResource(R.drawable.person);
		}else{
			byte[] decodedString = Base64.decode(image_path, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			profile_pic.setImageBitmap(decodedByte);
		}

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent service = new Intent(getApplicationContext(), DecrementUnreadCounterService.class);
				service.putExtra("chatroom", chatroom);
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

		Uri uri = Uri.parse(JewelChatDataProvider.SCHEME+"://" + JewelChatDataProvider.AUTHORITY + "/" + ChatMessageContract.SQLITE_TABLE_NAME );
		return new CursorLoader(getApplicationContext(), uri, null
				, ChatMessageContract.CHAT_ROOM+" = ? AND "+ChatMessageContract.IS_GROUP_MSG+ " = ?"
				, new String[]{this.chatroom+"", (this.is_group?1:0)+""}, ChatMessageContract.KEY_ROWID+" DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.chatRoomAdapter.swapCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.chatRoomAdapter.swapCursor(null);
	}


	@Override
	public void onClick(View view) {

		if(view.getId() == R.id.chatroom_send){
			if(!chatroom_text_input.getText().toString().trim().equals("")){
				Log.i("Send","Send");
				Intent msgSend = new Intent(getApplicationContext(), InsertSelfMessage.class);
				msgSend.putExtra("chatroom", this.chatroom);
				msgSend.putExtra("type",1);
				msgSend.putExtra("msg",chatroom_text_input.getText().toString().trim());
				startService(msgSend);
				chatroom_text_input.setText("");
			}
		}

	}

	@Override
	public void onItemClick(View view, Cursor cursor, int adapterPosition) {

		if(view.getId()== R.id.jewel && !pickingJewel){
			Log.i("Click on Jewel", "Click");
			int sum=0;
			for(int i=3; i<18; i++){
				sum += JewelChatApp.getSharedPref().getInt(i+"",0);

			}

			if(sum<25) {

				Intent pickJewel = new Intent(getApplicationContext(), PickJewelService.class);
				pickJewel.putExtra("jewel_type", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE)));
				pickJewel.putExtra("serverId", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.SERVER_ID)));
				pickJewel.putExtra("msg_id", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.KEY_ROWID)));
				pickJewel.putExtra("is_group", cursor.getInt(cursor.getColumnIndex(ChatMessageContract.IS_GROUP_MSG)));
				startService(pickJewel);
				//makeToast(cursor.getInt(cursor.getColumnIndex(ChatMessageContract.JEWEL_TYPE))+"");
			}else{
				makeToast("Jewel Store Full");
			}

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


	@Override
	public void onBackPressed() {
		Intent service = new Intent(getApplicationContext(), DecrementUnreadCounterService.class);
		service.putExtra("chatroom", this.chatroom);
		startService(service);
		super.onBackPressed();
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

	private void downloadContact() {

		Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JewelChatApp.appLog(Log.INFO,"DOWNLOADCONTACTS","DOWNLOADCONTACTS" + ":onResponse");
				//dismissDialog();
				try {

					Boolean error = response.getBoolean("error");
					if(error){
						String err_msg = response.getString("message");
						throw new Exception(err_msg);
					}

					JSONObject packet  = response.getJSONObject("contact");

					image_path = packet.getString("pic");
					contact_name = packet.getString("name");

					TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
					ImageView profile_pic = (ImageView) appbarRoot.findViewById(R.id.toolbarImage);

					titleText.setText(contact_name.substring(0, contact_name.length()>16?16:contact_name.length()));

					if(image_path==null || image_path.equals("")){
						profile_pic.setBackgroundColor(ContextCompat.getColor(JewelChatApp.getInstance().getApplicationContext(), R.color.gray));
						profile_pic.setImageResource(R.drawable.person);
					}else{
						byte[] decodedString = Base64.decode(image_path, Base64.DEFAULT);
						Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
						profile_pic.setImageBitmap(decodedByte);
					}

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
			t.put("id", this.jewelchat_id);
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.DOWNLOADCONTACT, t, response, this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				//createDialog("Please Wait");
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


}
