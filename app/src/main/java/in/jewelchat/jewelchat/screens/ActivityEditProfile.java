package in.jewelchat.jewelchat.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import in.jewelchat.jewelchat.BaseNetworkActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.JewelChatURLS;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.models.GameStateChangeEvent;
import in.jewelchat.jewelchat.models.NoInternet;
import in.jewelchat.jewelchat.models._403NetworkErrorEvent;
import in.jewelchat.jewelchat.network.JewelChatRequest;
import in.jewelchat.jewelchat.util.NetworkConnectivityStatus;

import static in.jewelchat.jewelchat.JewelChatApp.appLog;

/**
 * Created by mayukhchakraborty on 23/06/17.
 */

public class ActivityEditProfile extends BaseNetworkActivity {

	public static final String CONTENT = "content";
	public static boolean deleting = false;
	private static ActivityEditProfile mInstance;
	private final int EDIT_PROFILE_NAME = 30;
	private final int EDIT_PROFILE_STATUS = 40;
	public ImageView profilePic;
	public ImageButton buttonRemoveProfileImage;
	private TextView profileName;
	private TextView profileStatus;
	private TextView profileNumber;

	protected final int REQUEST_LOAD_IMAGE = 1;
	protected final int REQUEST_CROP_IMAGE = 2;

	protected String croppedImagePath;
	protected Bitmap croppedBitmap = null;

	protected String bigPicLocal = null;
	protected String smallPicLocal = null;


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

		if (JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_AVATAR_PATH, "").equals("")) {
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
				getPhotoFromGallery(6);
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
				deleteProfilePic();
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
			onCropImageReturn(data, 6);
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

					profileName.setText(name);
					JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.NAME, name).apply();

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
			t.put("name", name );
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.UPDATEPROFILENAME, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateStatus(final String tempStatus) {

		final String status = tempStatus.trim();
		if (status.equals("")) {
			makeToast("Enter status");
			return;
		}

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

					profileStatus.setText(status);
					JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.MY_STATUS_MESSAGE, status).apply();

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
			t.put("status", status );
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.UPDATEPROFILESTATUS, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}



	protected void getPhotoFromGallery(int calledFrom) {
		appLog(className + ":getPhotoFromGallery");
		//this.calledFrom = calledFrom;
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//intent.setType("image/* video/*");
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivityForResult(intent, REQUEST_LOAD_IMAGE);
	}

	protected void onImageReturn(Intent data) {
		appLog(className + ":ImageReturn");
		Uri selectedImage = data.getData();
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		String picPath = "";
		if (cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			picPath = cursor.getString(columnIndex);
			cursor.close();
		}
		if (picPath != null && !picPath.isEmpty()) {
			try {
				runCropImage(imageFileManager.getBigImage(picPath));
			} catch (IOException e) {
				appLog(className + ":onImageReturn:" + e.toString());
			}
		}
	}

	protected void runCropImage(String filePath) {
		appLog(className + ":runCropImage");
		Intent intent = new Intent(this, ActivityCropImage.class);
		intent.putExtra(ActivityCropImage.IMAGE_TO_BE_CROPPED_URI, filePath);
		intent.putExtra(ActivityCropImage.CALLED_FROM, 6);
		startActivityForResult(intent, REQUEST_CROP_IMAGE);
	}

	protected void onCropImageReturn(Intent data, int id) {

		appLog(className + ":onCropImageReturn");
		croppedImagePath = data.getStringExtra(ActivityCropImage.IMAGE_CROPPED_URI);

		if (croppedImagePath != null && !croppedImagePath.isEmpty()) {
			croppedBitmap = imageFileManager.getBitmapFromFile(croppedImagePath);
			if (croppedBitmap == null || croppedBitmap.getHeight() < 20) {
				makeToast("Picture has to be more than 20px");
				return;
			}
			try {
				bigPicLocal = imageFileManager.getBigImage(croppedImagePath);
			} catch (IOException e) {
				appLog(className + ":onCropImageReturn:" + e.toString());
			}

			Uri file = Uri.fromFile(new File(bigPicLocal));
			StorageReference profileRef = JewelChatApp.getStorageRef()
					.child("profileLarge/profile"+JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0)+".jpg");

			profileRef.putFile(file)
					.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							createUploadBlob();
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception exception) {
							dismissDialog();
						}
					});

		}
	}


	private void createUploadBlob(){

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


					profilePic.setImageBitmap(BitmapFactory.decodeFile(bigPicLocal));

					JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.MY_AVATAR_PATH, bigPicLocal).apply();
					buttonRemoveProfileImage.setVisibility(View.VISIBLE);
					dismissDialog();

				} catch (JSONException e) {
					FirebaseCrash.report(e);
				} catch (Exception e) {
					FirebaseCrash.report(e);
				}
			}

		};

		JSONObject t = new JSONObject();
		String x = imageFileManager.getBase64StringImage(bigPicLocal);
		try {
			t.put("pic", x);
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.UPDATEPROFILEPIC, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}




	private void deleteProfilePic() {

		Uri file = Uri.fromFile(new File(JewelChatApp.getSharedPref().getString(JewelChatPrefs.MY_AVATAR_PATH,"")));
		StorageReference profileRef = JewelChatApp.getStorageRef()
				.child("profileLarge/profile"+JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID,0)+".jpg");

		profileRef.delete()
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						// File deleted successfully
						eraseBlob();
					}
				}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Uh-oh, an error occurred!
				dismissDialog();
			}
		});


	}


	private void eraseBlob(){

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


					profilePic.setImageResource(R.drawable.default_profile);

					JewelChatApp.getSharedPref().edit().putString(JewelChatPrefs.MY_AVATAR_PATH, "").apply();
					buttonRemoveProfileImage.setVisibility(View.GONE);
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
			t.put("pic", " ");
			JewelChatRequest req = new JewelChatRequest(Request.Method.POST, JewelChatURLS.UPDATEPROFILEPIC, t, response,  this);
			if (NetworkConnectivityStatus.getConnectivityStatus() == NetworkConnectivityStatus.CONNECTED) {
				JewelChatApp.getRequestQueue().add(req);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
