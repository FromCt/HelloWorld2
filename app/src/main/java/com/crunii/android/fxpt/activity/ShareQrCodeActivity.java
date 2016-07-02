package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.crunii.android.fxpt.R;

public class ShareQrCodeActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_qrcode);

	}

	public void doBack(View v) {
		onBackPressed();
	}

}
