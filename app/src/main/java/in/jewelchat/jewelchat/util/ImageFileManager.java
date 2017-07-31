package in.jewelchat.jewelchat.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.jewelchat.jewelchat.JewelChatApp;

/**
 * Created by mayukhchakraborty on 23/07/17.
 */

public class ImageFileManager {

	public static final int FILE_TYPE_IMAGE_EXTERNAL = 1;
	public static final int FILE_TYPE_IMAGE_PROFILE = 2;
	public static final int FILE_TYPE_IMAGE_CHAT_SENT = 3;
	public static final int FILE_TYPE_IMAGE_CHAT_RECEIVED = 4;
	public static final int FILE_TYPE_INTERNAL_PERSISTENT = 5;
	public static final int FILE_TYPE_IMAGE_FEED = 6;
	public static final String AVATAR_URI = "p";
	public static final String CHAT_URI = "c";
	public static final String CHAT_SENT_URI = "sent";
	public static final String CHAT_RECEIVE_URI = "receive";
	public static final String FEED_URI = "f_";
	public static final String TEMP_BIG_IMAGE = "temp_big_image.jpeg";
	public static final String TEMP_SMALL_IMAGE = "temp_big_image.jpeg";
	private static ImageFileManager mInstance;
	private Context context;
	private String className;

	private ImageFileManager(Context context) {
		this.context = context;
		className = getClass().getSimpleName();
	}

	public static ImageFileManager getInstance(Context context) {
		if (mInstance == null)
			mInstance = new ImageFileManager(context);
		return mInstance;
	}

	public void deleteFile(String path) {
		if (path != null) {
			File f = new File(path);
			f.delete();
		}
	}

	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public String copy(String src, String dst) {
		InputStream in;
		OutputStream out;
		try {
			in = new FileInputStream(new File(src));
			out = new FileOutputStream(new File(dst));
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			return null;
		}
		return dst;
	}

	public byte[] getByteArrayFromPath(String filePath) {
		return getByteArrayFromBitmap(getBitmapFromFile(filePath));
	}

	public byte[] getByteArrayFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Bitmap smallPic = Bitmap.createScaledBitmap(bitmap, 40, 40, false);
		smallPic.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		return stream.toByteArray();
	}

	public Bitmap getBitmapFromFile(String path) {
		return getBitmapFromFile(path, 720);
	}

	public Bitmap getBitmapFromFile(String path, final int REQUIRED_SIZE) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			BitmapFactory.decodeFile(path, o);
			int width_tmp = o.outWidth;
			int height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inScaled = false;
			return BitmapFactory.decodeFile(path, o2);
		} catch (OutOfMemoryError e) {
			JewelChatApp.appLog(className + ":getBitmapFromFile:" + e.toString());
			return null;
		}

	}

	public String savePicture(byte[] data, int dir_type) {
		Bitmap bitmap = getBitmapFromByteArray(data);
		return savePicture(bitmap, dir_type, null);
	}

	public Bitmap getBitmapFromByteArray(byte[] data) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true; //
			BitmapFactory.decodeByteArray(data, 0, data.length, o);
			final int REQUIRED_SIZE = 720;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inScaled = false;
			return BitmapFactory.decodeByteArray(data, 0, data.length, o2);
		} catch (OutOfMemoryError e) {
			JewelChatApp.appLog(className + ":" + e.toString());
			return null;
		}

	}

	public String savePicture(Bitmap bitmap, int dir_type, String cId) {
		try {
			File file = new File(getAvailableImageFileName(dir_type, cId));
			FileOutputStream fOut = new FileOutputStream(file);
			if (bitmap == null) {
				fOut.flush();
				fOut.close();
				return null;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
			return file.getAbsolutePath();
		} catch (IOException | OutOfMemoryError e) {
			JewelChatApp.appLog(className + ":" + e.toString());
			return null;
		}
	}

	public String getAvailableImageFileName(int file_dir_type, String _id) {
		String time = "" + System.currentTimeMillis();
		if (file_dir_type == FILE_TYPE_IMAGE_PROFILE) {
			File f = new File(getStaDir(file_dir_type), AVATAR_URI + time + ".jpeg");
			return f.getAbsolutePath();
		} else if (file_dir_type == FILE_TYPE_IMAGE_CHAT_SENT) {
			File f = new File(getStaDir(file_dir_type), CHAT_URI + time + ".jpeg");
			return f.getAbsolutePath();
		} else if (file_dir_type == FILE_TYPE_IMAGE_CHAT_RECEIVED) {
			File f = new File(getStaDir(file_dir_type), CHAT_URI + time + ".jpeg");
			return f.getAbsolutePath();
		} else if (file_dir_type == FILE_TYPE_IMAGE_FEED) {
			File f = new File(getStaDir(file_dir_type), FEED_URI + _id + ".jpeg");
			return f.getAbsolutePath();
		} else {
			File f = new File(getStaDir(file_dir_type), FEED_URI + time + ".jpeg");
			return f.getAbsolutePath();
		}
	}

	public File getStaDir(int file_type) {
		File internalCard;
		File externalCard;
		File dir = null;

		internalCard = context.getFilesDir();

		if (isExternalStorageWritable()) {
			externalCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		} else {
			externalCard = internalCard;
		}
		switch (file_type) {
			case FILE_TYPE_IMAGE_PROFILE:
				dir = new File(externalCard, "CitiTalk");
				break;
			case FILE_TYPE_IMAGE_EXTERNAL:
				dir = new File(externalCard, "CitiTalk");
				break;
			case FILE_TYPE_IMAGE_CHAT_SENT:
				dir = new File(externalCard, "CitiTalk");
				break;
			case FILE_TYPE_IMAGE_CHAT_RECEIVED:
				dir = new File(externalCard, "CitiTalk");
				break;
			case FILE_TYPE_IMAGE_FEED:
				dir = new File(externalCard, "CitiTalk");
				break;
			case FILE_TYPE_INTERNAL_PERSISTENT:
				dir = new File(internalCard, "private");
				break;
		}
		if (dir != null)
			if (!dir.exists())
				if (!dir.mkdirs())
					return null;
		return dir;
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public String savePicture(String path, int dir_type, String cId) {
		return savePicture(getBitmapFromFile(path), dir_type, cId);
	}

	public Bitmap resize(Bitmap img, int Width, int Height) {
		int width = img.getWidth();
		int height = img.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) Width) / width;
		float scaleHeight = ((float) Height) / height;
		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}

	public File createTempFile(String fileName, String type) throws IOException {
		return File.createTempFile(fileName, type, getStaDir(FILE_TYPE_IMAGE_EXTERNAL));
	}

	public String getSmallImage(String path) {
		Bitmap selectedPic = null;
		try {
			selectedPic = getBitmapFromFile(path, 360);
		} catch (OutOfMemoryError e) {
			FirebaseCrash.report(e);
		}
		FileOutputStream fos;
		try {
			if (selectedPic != null) {
				fos = context.openFileOutput(TEMP_SMALL_IMAGE, Context.MODE_PRIVATE);
				selectedPic.compress(Bitmap.CompressFormat.JPEG, 75, fos);
				fos.flush();
				fos.close();
				return context.getFilesDir().getAbsolutePath() + File.separator + TEMP_SMALL_IMAGE;
			} else {
				return null;
			}
		} catch (Exception e) {
			FirebaseCrash.report(e);
		}
		return null;
	}


	public String getBase64StringImage(String path){

		Bitmap bitmap = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			bitmap = getBitmapFromFile(path, 50);
			Log.i("BitCount",bitmap.getByteCount()+"");
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

		} catch (OutOfMemoryError e) {
			FirebaseCrash.report(e);
		}

		return Base64.encodeToString(stream.toByteArray(),	Base64.NO_WRAP);

	}

	public String getBigImage(String picPath) throws IOException {
		Bitmap bitmap = getBitmapFromFile(picPath, 720);
		File file = createTempFile();
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			if (bitmap == null) {
				fOut.flush();
				fOut.close();
				return null;
			}
			/*int bytes = bitmap.getByteCount();
			final int MAX_COUNT = 4 * 150 * 1024;
			if (bytes > MAX_COUNT) {
				float ratio = bytes/MAX_COUNT;
				byte quality = (byte) (100/ratio);
				bitmap.compress(CompressFormat.JPEG, quality, fOut);
			}*/
			bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
			return file.getAbsolutePath();
		} catch (IOException e) {
			JewelChatApp.appLog(className + ":" + e.toString());
			return null;
		}
	}

	public File createTempFile() throws IOException {
		return File.createTempFile(TEMP_BIG_IMAGE, ".jpeg", getStaDir(FILE_TYPE_INTERNAL_PERSISTENT));
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}
		return inSampleSize;
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if (cursor == null) {
			return uri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}



}

