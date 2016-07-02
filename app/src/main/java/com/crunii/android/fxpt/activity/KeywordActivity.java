package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.crunii.android.fxpt.R;

public class KeywordActivity extends Activity {
	EditText keyword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyword);
		
		keyword = (EditText)findViewById(R.id.keyword);
		String hint = getIntent().getExtras().getString("hint");
		keyword.setHint(hint);
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doSearch(View v) {
		Intent intent = new Intent();
		intent.putExtra("keyword", keyword.getText().toString());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
