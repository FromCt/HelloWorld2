package com.crunii.android.fxpt.combinationGoodsActivity;

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

import java.util.ArrayList;

public class CombinChooseContractPackageActivity extends Activity {

	RadioGroup contractPackageRg;
	String contractPackageId = "";

	OnCheckedChangeListener packageChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton radio, boolean checked) {
			if(checked) {
					ProductDetailResultDemo.Common common = (ProductDetailResultDemo.Common) radio.getTag();
                    contractPackageId = common.id;
					((WebView)findViewById(R.id.desc)).loadDataWithBaseURL(null, common.desc, "text/html", "utf-8", null);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combin_choose_contract_package);
		ArrayList<ProductDetailResultDemo.Common>  contractPackageArray = (ArrayList<ProductDetailResultDemo.Common>) getIntent().getSerializableExtra("ITV");

		contractPackageRg = (RadioGroup) findViewById(R.id.contract_package_rg);
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10,10,10,10);
		//产品列表
		LayoutInflater inflater = LayoutInflater.from(this);
		for(int i=0; i< contractPackageArray.size(); i++) {
			ProductDetailResultDemo.Common common = contractPackageArray.get(i);
			RadioButton radio = (RadioButton) inflater.inflate(R.layout.radiobutton_contract_package, null);
			radio.setText(common.name);
			radio.setTag(common);
			radio.setOnCheckedChangeListener(packageChangeListener);
			radio.setLayoutParams(layoutParams);
			contractPackageRg.addView(radio);
		}
		//默认选择第一个产品
		if(contractPackageArray.size() > 0){
			((RadioButton)contractPackageRg.getChildAt(0)).setChecked(true);
			contractPackageId = contractPackageArray.get(0).id;
		}


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
