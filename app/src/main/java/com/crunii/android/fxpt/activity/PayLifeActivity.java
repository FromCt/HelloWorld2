package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.crunii.android.fxpt.R;

public class PayLifeActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paylife);
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doSf(View v) {
		Toast.makeText(this, "建设中...", Toast.LENGTH_SHORT).show();
	}

	public void doDf(View v) {
		Toast.makeText(this, "建设中...", Toast.LENGTH_SHORT).show();
	}

	public void doQf(View v) {
		Toast.makeText(this, "建设中...", Toast.LENGTH_SHORT).show();
	}
	
}
