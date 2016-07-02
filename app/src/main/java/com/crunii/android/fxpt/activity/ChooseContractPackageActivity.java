package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseContractPackageActivity extends Activity {
	JSONArray contractPackageArray = new JSONArray();
	
	RadioGroup contractPackageRg;
	String contractPackageId = "";

	OnCheckedChangeListener packageChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton radio, boolean checked) {
			if(checked) {
				try {
					String jsonString = (String) radio.getTag();
					JSONObject json = new JSONObject(jsonString);
                    contractPackageId = json.optString("id");
					((WebView)findViewById(R.id.desc)).loadDataWithBaseURL(null, json.optString("desc"), "text/html", "utf-8", null);					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_contract_package);
		
		try {
			contractPackageArray = new JSONArray(getIntent().getExtras().getString("contractPackage"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		contractPackageRg = (RadioGroup) findViewById(R.id.contract_package_rg);
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10,10,10,10);
		//产品列表
		LayoutInflater inflater = LayoutInflater.from(this);
		for(int i=0; i<contractPackageArray.length(); i++) {
			JSONObject json = contractPackageArray.optJSONObject(i);
			String name = json.optString("name");
			RadioButton radio = (RadioButton) inflater.inflate(R.layout.radiobutton_contract_package, null);
			radio.setText(name);
			radio.setTag(json.toString());
			radio.setOnCheckedChangeListener(packageChangeListener);
			radio.setLayoutParams(layoutParams);
			contractPackageRg.addView(radio);
		}
		//默认选择第一个产品
		((RadioButton)contractPackageRg.getChildAt(0)).setChecked(true);
		contractPackageId = ((JSONObject)contractPackageArray.optJSONObject(0)).optString("id");

	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doSubmit(View v) {
		Intent i = new Intent();
		i.putExtra("contractPackageId", contractPackageId);
		setResult(RESULT_OK, i);
		finish();
	}

}
