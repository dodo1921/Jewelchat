package in.jewelchat.jewelchat.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.otto.Subscribe;

import in.jewelchat.jewelchat.BaseImageActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;

/**
 * Created by mayukhchakraborty on 23/06/17.
 */

public class ActivityEditProfile extends BaseImageActivity {

	public static final String CONTENT = "content";
	public static boolean deleting = false;
	private static ActivityEditProfile mInstance;
	private final int EDIT_PROFILE_NAME = 30;
	private final int EDIT_PROFILE_STATUS = 40;
	private final int EDIT_PROFILE_ADDRESS = 50;
	public ImageView profilePic;
	public ImageButton buttonRemoveProfileImage;
	private TextView profileName;
	private TextView profileStatus;
	private TextView profileAddress;
	private TextView profileNumber;


	public static ActivityEditProfile getInstance() {
		return mInstance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		//setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

		rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		setUpAppbar();

		mInstance = this;
		//rootLayout = (ScrollView) findViewById(R.id.edit_profile_root);
		rootLayout.findViewById(R.id.edit_profile_image).setOnClickListener(this);
		profilePic = (ImageView) rootLayout.findViewById(R.id.profile_image);
		profileName = (TextView) rootLayout.findViewById(R.id.edit_profile_name);
		profileStatus = (TextView) rootLayout.findViewById(R.id.edit_profile_status);

		profileNumber = (TextView) rootLayout.findViewById(R.id.profile_number);

		ImageButton buttonEditName = (ImageButton) rootLayout.findViewById(R.id.edit_name_button);
		buttonEditName.setOnClickListener(this);
		ImageButton buttonEditStatus = (ImageButton) rootLayout.findViewById(R.id.edit_status_button);
		buttonEditStatus.setOnClickListener(this);

		buttonRemoveProfileImage = (ImageButton) rootLayout.findViewById(R.id.remove_profile_image);
		buttonRemoveProfileImage.setOnClickListener(this);
	}


	@Override
	protected void setUpAppbar() {
		JewelChatApp.appLog(className + ":setUpAppbar");
		super.setUpAppbar();
		//Toolbar toolbar = (Toolbar) appbarRoot.findViewById(R.id.jewelchat_toolbar);
		TextView titleText = (TextView) appbarRoot.findViewById(R.id.toolbarTitle);
		titleText.setText("Edit Profile");

		toolbar_back.setVisibility(View.VISIBLE);
		toolbar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
	}

	@Override
	protected void onResume() {
		super.onResume();
		String path = JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_AVATAR_PATH, "");
		if (!path.equals("")) {
			Bitmap bitmap = imageFileManager.getBitmapFromFile(path);
			profilePic.setImageBitmap(bitmap);
		} else {
			profilePic.setImageResource(R.drawable.default_profile);
		}
		profileName.setText(JewelChatApp.getSharedPref().getString(JewelChatPrefs.NAME, ""));
		profileStatus.setText(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_STATUS_MESSAGE, ""));
		profileNumber.setText(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_PHONE, ""));

		if (JewelChatApp.getSharedPref().getBoolean(JewelChatPrefs.PROFILE_PIC_EMPTY, true)) {
			buttonRemoveProfileImage.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		JewelChatApp.appLog(className + ":onClick");
		Intent intent;
		switch (view.getId()) {
			case R.id.edit_profile_image:
				JewelChatApp.appLog(className + ":onClick:editImage");
				getPhotoFromGallery(FROM_PROFILE);
				break;
			case R.id.edit_name_button:
				JewelChatApp.appLog(className + ":onClick:editButton");
				intent = new Intent(this, ActivityEditProfileName.class);
				startActivityForResult(intent, EDIT_PROFILE_NAME);
				break;
			case R.id.edit_status_button:
				JewelChatApp.appLog(className + ":onClick:editStatus");
				intent = new Intent(this, ActivityEditProfileStatus.class);
				startActivityForResult(intent, EDIT_PROFILE_STATUS);
				break;
			case R.id.remove_profile_image:
				JewelChatApp.appLog(className + ":onClick:removeProfileImage");
				createOptionDialog();
				break;
		}
	}
	private void createOptionDialog() {
		JewelChatApp.appLog(className + ":createChooseOptionsDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to remove?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleting = true;
				createDialog("Removing...");
				//deleteAWS();
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
			onImageReturn(data);
		} else if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK && data != null) {
			onCropImageReturn(data, FROM_PROFILE);
			createDialog("Uploading...");
		} else if (requestCode == EDIT_PROFILE_NAME && resultCode == RESULT_OK && data != null) {
			Bundle res = data.getExtras();
			String result = res.getString(CONTENT);
			updateName(result);
		} else if (requestCode == EDIT_PROFILE_STATUS && resultCode == RESULT_OK && data != null) {
			Bundle res = data.getExtras();
			String result = res.getString(CONTENT);
			updateStatus(result);
		}
	}

	private void updateName(String tempName) {
		final String name = tempName.trim();
		if (name.equals("")) {
			makeToast("Enter name");
			return;
		}
		/*
		nameContainer.startShimmerAnimation();
		Map<String, String> jsonParams = new HashMap<>();
		jsonParams.put("name", name);
		Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					response.getString("Error");
					nameContainer.stopShimmerAnimation();
					makeToast("Name update failed");
				} catch (JSONException error) {
					try {
						response.getString("status");
						editor.putString(CitiTalkPrefs.getMyName(), name);
						if (editor.commit()) {
							if (Pattern.compile("\\s").matcher(name).find()) {
								CitiTalk.logEvent("ProfileEdit", "Name", "Multiple-Words");
							} else {
								CitiTalk.logEvent("ProfileEdit", "Name", "Single-Word");
							}
							profileName.setText(name);
							nameContainer.stopShimmerAnimation();
							makeToast("Name update successful");
							editor.putLong(CitiTalkPrefs.getNameChangeTime(), System.currentTimeMillis());
							editor.commit();
							return;
						}
						nameContainer.stopShimmerAnimation();
						makeToast("Name update failed");
					} catch (JSONException e) {
						CitiTalk.appLog(className + ":nameUpdateError:" + e.toString());
						nameContainer.stopShimmerAnimation();
						makeToast("Name update failed");
					}
				}
			}
		};

		CitiTalkRequest request = new CitiTalkRequest(Request.Method.POST, CitiTalkURLS.UPDATE_NAME,
				new JSONObject(jsonParams), responseListener, this);
		if (!addRequest(request)) {
			nameContainer.stopShimmerAnimation();
		}
		*/
	}

	private void updateStatus(String tempStatus) {
		final String status = tempStatus.trim();
		if (status.equals("")) {
			makeToast("Enter status");
			return;
		}
		/*
		statusContainer.startShimmerAnimation();
		Map<String, String> jsonParams = new HashMap<>();
		jsonParams.put("statusmsg", status);
		Respose.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					response.getString("Error");
					statusContainer.stopShimmerAnimation();
					makeToast("Status update failed");
				} catch (JSONException error) {
					try {
						response.getString("status");
						editor.putString(CitiTalkPrefs.getMyStatusMessage(), status);
						if (editor.commit()) {
							if (status.contains("/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g")) {
								CitiTalk.logEvent("ProfileEdit", "Status", "Emoticon");
							} else {
								CitiTalk.logEvent("ProfileEdit", "Status", "Simple");
							}
							profileStatus.setText(status);
							statusContainer.stopShimmerAnimation();
							makeToast("Status update successful");
							return;
						}
						statusContainer.stopShimmerAnimation();
						makeToast("Status update failed");
					} catch (JSONException e) {
						CitiTalk.appLog(className + ":statusUpdateError:" + e.toString());
						statusContainer.stopShimmerAnimation();
						makeToast("Status update failed");
					}
				}
			}
		};

		CitiTalkRequest request = new CitiTalkRequest(Request.Method.POST, CitiTalkURLS.UPDATE_STATUS_MSG,
				new JSONObject(jsonParams), responseListener, this);
		if (!addRequest(request)) {
			statusContainer.stopShimmerAnimation();
		}

		*/
	}





	public void onAwsSuccess() {

		/*
		Map<String, String> jsonParams = new HashMap<>();
		jsonParams.put("smallpicpath", smallPicCloud);
		jsonParams.put("largepicpath", bigPicCloud);
		Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					response.getString("Error");
					dismissDialog();
					makeToast("Image update failed");
				} catch (JSONException error) {
					try {
						response.getString("status");
						String path = imageFileManager.savePicture(bigPicLocal, ImageFileManager.FILE_TYPE_IMAGE_PROFILE, preferences.getString(CitiTalkPrefs.getMyId(), ""));
						editor.putString(CitiTalkPrefs.getMyAvatarPath(), path);
						if (editor.commit()) {
							Bitmap bitmap = imageFileManager.getBitmapFromFile(path);
							if (bitmap != null) {
								CitiTalk.logEvent("ProfileEdit", "Pic", "Added");
								profilePic.setImageBitmap(bitmap);
								dismissDialog();
								makeToast("Image update successful");
								String pathInvalidate = "file:" + preferences.getString(CitiTalkPrefs.getMyAvatarPath(), "");
								Picasso.with(context).invalidate(pathInvalidate);
								editor.putBoolean(CitiTalkPrefs.getProfilePicEmpty(), false);
								editor.commit();
								buttonRemoveProfileImage.setVisibility(View.VISIBLE);
								return;
							}
						}
						dismissDialog();
						makeToast("Image update failed");
					} catch (JSONException e) {
						CitiTalk.appLog(className + ":picUpdateError:" + e.toString());
						dismissDialog();
						makeToast("Image update failed");
					}
				}
			}
		};

		CitiTalkRequest request = new CitiTalkRequest(Request.Method.POST, CitiTalkURLS.UPDATE_PROF_PIC,
				new JSONObject(jsonParams), responseListener, this);
		addRequest(request);

		*/
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		return id == R.id.action_settings || super.onOptionsItemSelected(item);
	}

	*/


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
