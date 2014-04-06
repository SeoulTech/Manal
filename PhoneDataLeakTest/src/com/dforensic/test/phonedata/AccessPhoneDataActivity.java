package com.dforensic.test.phonedata;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class AccessPhoneDataActivity extends Activity {

	private String mDeviceId = null;
	private TextView mDisplayResTxtView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_access_phone_data);

		mDisplayResTxtView = (TextView) findViewById(R.id.display_results);

		readDeviceId();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.access_phone_data, menu);
		return true;
	}

	private void readDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mDeviceId = telephonyManager.getDeviceId();
		if (mDeviceId != null) {
			StorePhoneData storeData = new StorePhoneData();
			storeData.createExternalStoragePrivateFile(this,
					mDeviceId.getBytes());
			File file = storeData.getFile(this);
			if (file != null) {
				SendPhoneData sendData = new SendPhoneData();
				sendData.sendFile(file);
				mDisplayResTxtView.setText(mDeviceId + " is sent by email.");
			} else {
				Log.w(Constants.APP_NAME, "File doesn't exist.");
			}
		} else {
			Log.w(Constants.APP_NAME, "Device ID is not extracted.");
		}
	}

}
