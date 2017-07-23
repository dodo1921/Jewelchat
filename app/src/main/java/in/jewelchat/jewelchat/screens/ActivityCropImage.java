package in.jewelchat.jewelchat.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import in.jewelchat.jewelchat.BaseActivity;
import in.jewelchat.jewelchat.JewelChatApp;
import in.jewelchat.jewelchat.JewelChatPrefs;
import in.jewelchat.jewelchat.R;
import in.jewelchat.jewelchat.util.ImageFileManager;

/**
 * Created by mayukhchakraborty on 23/07/17.
 */

public class ActivityCropImage extends BaseActivity {

	public static final String IMAGE_TO_BE_CROPPED_URI = "imageToBeCroppedURI";
	public static final String IMAGE_CROPPED_URI = "imageCroppedURI";
	public static final String CALLED_FROM = "cropping_called_from";
	private CropImageView cropImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);
		Intent intent = getIntent();
		Uri uri = getImageUri(intent.getStringExtra(IMAGE_TO_BE_CROPPED_URI));
		int calledFrom = intent.getIntExtra(CALLED_FROM, 0);
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		cropImageView.setImageUri(uri);
		if (calledFrom == 5) {
			cropImageView.setFixedAspectRatio(false);
		} else {
			cropImageView.setFixedAspectRatio(true);
		}
		findViewById(R.id.rotateRight).setOnClickListener(this);
		findViewById(R.id.rotateLeft).setOnClickListener(this);
		findViewById(R.id.discard).setOnClickListener(this);
		findViewById(R.id.save).setOnClickListener(this);
	}

	private Uri getImageUri(String path) {
		try {
			File file = new File(path);
			file.deleteOnExit();
			return Uri.fromFile(file);
		} catch (NullPointerException e) {
			JewelChatApp.appLog(getClass().getSimpleName() + ":" + e.toString());
			return null;
		}
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rotateRight:
				cropImageView.rotateImage(90);
				break;
			case R.id.rotateLeft:
				cropImageView.rotateImage(270);
				break;
			case R.id.save:
				String croppedFilePath = ImageFileManager.getInstance(context).savePicture(cropImageView.getCroppedImage(),
						ImageFileManager.FILE_TYPE_INTERNAL_PERSISTENT, JewelChatApp.getSharedPref().getLong(JewelChatPrefs.MY_ID, 0)+"");
				setResult(RESULT_OK, new Intent().putExtra(IMAGE_CROPPED_URI, croppedFilePath));
				finish();
				break;
			case R.id.discard:
				finish();
				break;
			default:
				break;

		}
	}
}
