/**
 * Copyright 2011-2012. Chongqing CRun Information Industry Co.,Ltd.
 * All rights reserved. <a>http://www.crunii.com</a>
 */
package com.crunii.android.fxpt.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author Administrator
 * @date 2012-8-29
 */
public class ImageHelper {

	public static String getPath(String photoName) {
		String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdir();
		}

		return dirPath + "/" + photoName;
	}

	public static void showImagePreview(Activity activity, File imageFile, ImageView iv) {

		Bitmap resizedBitmap = null;
		ImageHelper ih = new ImageHelper();
		Display display = activity.getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width > height) {
			width = height;
		}

		HashMap<String, Object> mediaData = ih.getImageBytesForPath(imageFile.getPath(), activity);

		if (mediaData == null) {
			Toast.makeText(activity, "无法预览", Toast.LENGTH_SHORT).show();
			return;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		byte[] bytes = (byte[]) mediaData.get("bytes");
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		float conversionFactor = 0.8f;

		byte[] finalBytes = ih.createThumbnail(bytes,
				String.valueOf((int) (width * conversionFactor)),
				(String) mediaData.get("orientation"), true);

		resizedBitmap = BitmapFactory.decodeByteArray(finalBytes, 0,
				finalBytes.length);
		iv.setImageBitmap(resizedBitmap);
	}

	/**
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2byte(Bitmap bitmap) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] array = baos.toByteArray();
			baos.flush();
			baos.close();
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static byte[] createThumbnailH(byte[] bytes, String sMaxImageHight, String orientation, boolean tiny) {
		// creates a thumbnail and returns the bytes

		int finalW = 0;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

		int hight = opts.outHeight;

		int finalHight = 500; // default to this if there's a problem
		// Change dimensions of thumbnail

		if (tiny) {
			finalHight = 150;
		}

		byte[] finalBytes;

		if (sMaxImageHight.equals("Original Size")) {
			finalBytes = bytes;
		} else {
			finalHight = Integer.parseInt(sMaxImageHight);
			if (finalHight > hight) {
				// don't resize
				finalBytes = bytes;
			} else {
				int sample = 0;

				// float fWidth = width;
				sample = hight / Integer.parseInt(sMaxImageHight);

				Log.e("sample", "sample=" + hight + "/" + sMaxImageHight + "=" + sample);
				// Double.valueOf(FloatMath.ceil(fWidth / 1200)).intValue();

				// if (sample == 3) {
				// sample = 4;
				// } else if (sample > 4 && sample < 8) {
				// sample = 8;
				// }

				opts.inSampleSize = sample;
				opts.inJustDecodeBounds = false;

				try {
					bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
					if (bm == null) {
						return null;
					}
				} catch (OutOfMemoryError e) {
					// out of memory
					return null;
				}

				float percentage = (float) finalHight / bm.getHeight();
				float proportionateW = bm.getWidth() * percentage;
				finalW = (int) Math.rint(proportionateW);

				float scaleWidth = ((float) finalW) / bm.getWidth();
				float scaleHeight = ((float) finalHight) / bm.getHeight();

				float scaleBy = Math.min(scaleWidth, scaleHeight);

				// Create a matrix for the manipulation
				Matrix matrix = new Matrix();
				// Resize the bitmap
				matrix.postScale(scaleBy, scaleBy);
				if ((orientation != null)
				    && (orientation.equals("90") || orientation.equals("180") || orientation.equals("270"))) {
					matrix.postRotate(Integer.valueOf(orientation));
				}

				Bitmap resized = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

				Log.e("bm size", resized.getWidth() + "," + resized.getHeight());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				resized.compress(Bitmap.CompressFormat.JPEG, 50, baos);

				bm.recycle(); // free up memory
				resized.recycle();

				finalBytes = baos.toByteArray();
			}
		}

		return finalBytes;

	}

	public byte[] createThumbnail(byte[] bytes, String sMaxImageWidth, String orientation, boolean tiny) {
		// creates a thumbnail and returns the bytes

		int finalHeight = 0;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

		int width = opts.outWidth;

		int finalWidth = 500; // default to this if there's a problem
		// Change dimensions of thumbnail

		if (tiny) {
			finalWidth = 150;
		}

		byte[] finalBytes;

		if (sMaxImageWidth.equals("Original Size")) {
			finalBytes = bytes;
		} else {
			finalWidth = Integer.parseInt(sMaxImageWidth);
			if (finalWidth > width) {
				// don't resize
				finalBytes = bytes;
			} else {
				int sample = 0;

				// float fWidth = width;
				sample = width / Integer.parseInt(sMaxImageWidth);
				// Double.valueOf(FloatMath.ceil(fWidth / 1200)).intValue();

				// if (sample == 3) {
				// sample = 4;
				// } else if (sample > 4 && sample < 8) {
				// sample = 8;
				// }

				opts.inSampleSize = sample;
				opts.inJustDecodeBounds = false;

				try {
					bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
				} catch (OutOfMemoryError e) {
					// out of memory
					return null;
				}

				float percentage = (float) finalWidth / bm.getWidth();
				float proportionateHeight = bm.getHeight() * percentage;
				finalHeight = (int) Math.rint(proportionateHeight);

				float scaleWidth = ((float) finalWidth) / bm.getWidth();
				float scaleHeight = ((float) finalHeight) / bm.getHeight();

				float scaleBy = Math.min(scaleWidth, scaleHeight);

				// Create a matrix for the manipulation
				Matrix matrix = new Matrix();
				// Resize the bitmap
				matrix.postScale(scaleBy, scaleBy);
				if ((orientation != null)
				    && (orientation.equals("90") || orientation.equals("180") || orientation.equals("270"))) {
					matrix.postRotate(Integer.valueOf(orientation));
				}

				Bitmap resized = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

				Log.e("bm size", resized.getWidth() + "," + resized.getHeight());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				resized.compress(Bitmap.CompressFormat.JPEG, 50, baos);

				bm.recycle(); // free up memory
				resized.recycle();

				finalBytes = baos.toByteArray();
			}
		}

		return finalBytes;

	}

	public String getExifOrientation(String path, String orientation) {
		// get image EXIF orientation if Android 2.0 or higher, using reflection
		// http://developer.android.com/resources/articles/backward-compatibility.html
		Method exif_getAttribute;
		Constructor<ExifInterface> exif_construct;
		String exifOrientation = "";

		int sdk_int = 0;
		try {
			sdk_int = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (Exception e1) {
			sdk_int = 3; // assume they are on cupcake
		}
		if (sdk_int >= 5) {
			try {
				exif_construct = android.media.ExifInterface.class.getConstructor(new Class[] { String.class });
				Object exif = exif_construct.newInstance(path);
				exif_getAttribute = android.media.ExifInterface.class.getMethod("getAttribute", new Class[] { String.class });
				try {
					exifOrientation = (String) exif_getAttribute.invoke(exif, android.media.ExifInterface.TAG_ORIENTATION);
					if (exifOrientation != null) {
						if (exifOrientation.equals("1")) {
							orientation = "0";
						} else if (exifOrientation.equals("3")) {
							orientation = "180";
						} else if (exifOrientation.equals("6")) {
							orientation = "90";
						} else if (exifOrientation.equals("8")) {
							orientation = "270";
						}
					} else {
						orientation = "0";
					}
				} catch (InvocationTargetException ite) {
					/* unpack original exception when possible */
					orientation = "0";
				} catch (IllegalAccessException ie) {
					System.err.println("unexpected " + ie);
					orientation = "0";
				}
				/* success, this is a newer device */
			} catch (NoSuchMethodException nsme) {
				orientation = "0";
			} catch (IllegalArgumentException e) {
				orientation = "0";
			} catch (InstantiationException e) {
				orientation = "0";
			} catch (IllegalAccessException e) {
				orientation = "0";
			} catch (InvocationTargetException e) {
				orientation = "0";
			}

		}
		return orientation;
	}

	public HashMap<String, Object> getImageBytesForPath(String filePath, Context ctx) {
		Uri curStream = null;
		String[] projection;
		HashMap<String, Object> mediaData = new HashMap<String, Object>();
		String title = "", orientation = "";
		byte[] bytes;
		if (filePath != null) {
			if (!filePath.contains("content://"))
				curStream = Uri.parse("content://media" + filePath);
			else
				curStream = Uri.parse(filePath);
		}
		if (curStream != null) {
			if (filePath.contains("video")) {
				int videoID = Integer.parseInt(curStream.getLastPathSegment());
				projection = new String[] { Video.Thumbnails._ID, Video.Thumbnails.DATA };
				ContentResolver crThumb = ctx.getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 16;
				Bitmap videoBitmap = MediaStore.Video.Thumbnails.getThumbnail(crThumb, videoID,
				    MediaStore.Video.Thumbnails.MINI_KIND, options);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					videoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					bytes = stream.toByteArray();
					title = "Video";
					videoBitmap = null;
				} catch (Exception e) {
					return null;
				}

			} else {
				projection = new String[] { Images.Thumbnails._ID, Images.Thumbnails.DATA, Images.Media.ORIENTATION };

				String path = "";
				Cursor cur = ctx.getContentResolver().query(curStream, projection, null, null, null);
				File jpeg = null;
				if (cur != null) {
					String thumbData = "";

					if (cur.moveToFirst()) {

						int dataColumn, orientationColumn;

						dataColumn = cur.getColumnIndex(Images.Media.DATA);
						thumbData = cur.getString(dataColumn);
						orientationColumn = cur.getColumnIndex(Images.Media.ORIENTATION);
						orientation = cur.getString(orientationColumn);
					}

					if (thumbData == null) {
						return null;
					}

					jpeg = new File(thumbData);
					path = thumbData;
				} else {
					path = filePath.toString().replace("file://", "");
					jpeg = new File(path);

				}

				title = jpeg.getName();

				try {
					bytes = new byte[(int) jpeg.length()];
				} catch (Exception e) {
					return null;
				} catch (OutOfMemoryError e) {
					return null;
				}

				DataInputStream in = null;
				try {
					in = new DataInputStream(new FileInputStream(jpeg));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				try {
					in.readFully(bytes);
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				title = jpeg.getName();
				if (orientation == "") {
					orientation = getExifOrientation(path, orientation);
				}
			}

			mediaData.put("bytes", bytes);
			mediaData.put("title", title);
			mediaData.put("orientation", orientation);

			return mediaData;

		} else {
			return null;
		}

	}

}
