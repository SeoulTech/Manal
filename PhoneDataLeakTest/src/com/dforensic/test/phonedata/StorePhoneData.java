package com.dforensic.test.phonedata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class StorePhoneData {

	private File mFile = null;
	private String mFileName = "tempPhoneDataLeakTest.txt";

	public void createExternalStoragePrivateFile(Context ctx, byte[] outData) {
		// Create a path where we will place our private file on external
		// storage.
		getFile(ctx);

		if (outData != null) {
			try {
				OutputStream os = new FileOutputStream(mFile);
				os.write(outData);
				os.close();
			} catch (IOException e) {
				// Unable to create file, likely because external storage is
				// not currently mounted.
				Log.w(Constants.APP_NAME, "Error writing " + mFile, e);
			}
		} else {
			Log.e(Constants.APP_NAME, "no data to store");
		}
	}

	public void deleteExternalStoragePrivateFile(Context ctx) {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		getFile(ctx);
		if (mFile != null) {
			mFile.delete();
		}
	}

	public boolean hasExternalStoragePrivateFile(Context ctx) {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		getFile(ctx);
		if (mFile != null) {
			return mFile.exists();
		}
		return false;
	}
	
	public File getFile(Context ctx) {
		// mFile = new File(Environment.getExternalStorageDirectory(), mFileName);
		// mFile = new File(ctx.getExternalFilesDir(null), mFileName);
		mFile = new File("mnt/sdcard/", mFileName);
		return mFile;
	}
	
}
