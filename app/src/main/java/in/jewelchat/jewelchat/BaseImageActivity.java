package in.jewelchat.jewelchat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

import in.jewelchat.jewelchat.screens.ActivityCropImage;

/**
 * Created by mayukhchakraborty on 06/03/16.
 */
public abstract class BaseImageActivity extends BaseNetworkActivity {

	protected final int REQUEST_LOAD_IMAGE = 1;
	protected final int REQUEST_TAKE_PHOTO = 10;
	protected final int ADD_CONTACT_FROM_CHAT = 11;
	protected final int FROM_CREATE_GROUP = 12;
	protected final int REQUEST_CROP_IMAGE = 2;
	protected final int FROM_NEW_POST = 3;
	protected final int FROM_COMMENT = 4;
	protected final int FROM_CHAT = 5;
	protected final int FROM_PROFILE = 6;
	protected final int SUCCESS = 7;
	protected final int ERROR = 8;

	protected String croppedImagePath;
	protected Bitmap croppedBitmap = null;

	protected String bigPicLocal = null;
	protected String smallPicLocal = null;

	private int calledFrom;

	protected void getPhotoFromGallery(int calledFrom) {
		JewelChatApp.appLog(className + ":getPhotoFromGallery");
		this.calledFrom = calledFrom;
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivityForResult(intent, REQUEST_LOAD_IMAGE);
	}

	protected void onImageReturn(Intent data) {
		JewelChatApp.appLog(className + ":ImageReturn");
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
				JewelChatApp.appLog(className + ":onImageReturn:" + e.toString());
			}
		}
	}

	protected void runCropImage(String filePath) {
		JewelChatApp.appLog(className + ":runCropImage");
		Intent intent = new Intent(this, ActivityCropImage.class);
		intent.putExtra(ActivityCropImage.IMAGE_TO_BE_CROPPED_URI, filePath);
		intent.putExtra(ActivityCropImage.CALLED_FROM, calledFrom);
		startActivityForResult(intent, REQUEST_CROP_IMAGE);
	}

	protected void onCropImageReturn(Intent data, int id) {
		JewelChatApp.appLog(className + ":onCropImageReturn");
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
				JewelChatApp.appLog(className + ":onCropImageReturn:" + e.toString());
			}
			smallPicLocal = imageFileManager.getSmallImage(bigPicLocal);


		}
	}


}
