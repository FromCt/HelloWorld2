package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.crunii.android.fxpt.R;

public class PayTelActivity extends Activity {
	RadioButton radio1, radio2;

	EditText number;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paytel);

		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);		
		radio1.setChecked(true);
		
		number = (EditText)findViewById(R.id.number);
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doRadio1(View v) {
		number.setText("");
		number.setHint("手机号码");
		
	}

	public void doRadio2(View v) {
		number.setText("");
		number.setHint("固话号码");

	}
	
	public void doSubmit(View v) {
		
	}
	
}
