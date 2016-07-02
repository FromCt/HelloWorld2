package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class SpeedPackageActivity extends Activity {
	String category;
	String modeId;
	String modeName;
	JSONArray list;
	LayoutInflater inflater;
	LinearLayout content_view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speedpackage);

		category = getIntent().getExtras().getString("category");
		modeId = getIntent().getExtras().getString("modeId");
		modeName = getIntent().getExtras().getString("modeName");

		TextView modeNameTv = (TextView) findViewById(R.id.modeName);
		modeNameTv.setText(modeName);

		inflater = LayoutInflater.from(this);
		content_view = (LinearLayout) findViewById(R.id.content_view);
		
		refresh();
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	private void refresh() {

		new BaseTask<String, String, JSONArray>(this, "请稍后...") {

			@Override
			protected JSONArray doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().speedpackage(getApplicationContext(), modeId);
			}

			@Override
			protected void onSuccess(JSONArray result) {
				list = result;
				
				//如果是虚拟套餐，则直接跳转到商品列表界面
				if(isVirtualPackage()) {
					PackageInfo info = getVirtualPackageInfo();
					Intent i = new Intent(SpeedPackageActivity.this, ProductListActivity.class);
					i.putExtra("category", category);
					i.putExtra("mode", modeId);
					i.putExtra("speed", info.getSpeedId());
					i.putExtra("packageId", info.getPackageId());
					i.putExtra("packageName", info.getPackageName());
					startActivity(i);
					
					finish();
				} else {
					showData();
				}
			}

		}.execute("");
	}
	
	private boolean isVirtualPackage() {
		for(int i=0; i<list.length(); i++) {
			JSONObject json = list.optJSONObject(i);
			if(json.optBoolean("virtual")) {
				return true;
			}
		}
		return false;
	}
	
	private PackageInfo getVirtualPackageInfo() { //虚拟套餐只可能有一个速率、一个套餐
		JSONObject json1 = list.optJSONObject(0);
		JSONObject json2 = json1.optJSONArray("package").optJSONObject(0);
		return new PackageInfo(json1.optString("speedId"), json1.optString("speedName"), json2.optString("id"), json2.optString("name"));
	}
	
	private int getSpeedPackageIconId(int line) {
		int iconResId = R.drawable.speedpackage;
		
//		if(category.equals(TabAFragment.category)) { //融合套餐
//			switch(line) {
//			case 0:
//				iconResId = R.drawable.speedpackage_rh01;
//				break;
//			case 1:
//				iconResId = R.drawable.speedpackage_rh02;
//				break;
//			case 2:
//				iconResId = R.drawable.speedpackage_rh03;
//				break;
//			case 3:
//				iconResId = R.drawable.speedpackage_rh04;
//				break;
//			case 4:
//				iconResId = R.drawable.speedpackage_rh05;
//				break;
//			case 5:
//				iconResId = R.drawable.speedpackage_rh06;
//				break;
//			default:
//				break;
//			}
//		} else if(category.equals(TabBFragment.category)) { //宽带套餐
//			switch(line) {
//			case 0:
//				iconResId = R.drawable.speedpackage_kd01;
//				break;
//			case 1:
//				iconResId = R.drawable.speedpackage_kd02;
//				break;
//			case 2:
//				iconResId = R.drawable.speedpackage_kd03;
//				break;
//			case 3:
//				iconResId = R.drawable.speedpackage_kd04;
//				break;
//			case 4:
//				iconResId = R.drawable.speedpackage_kd05;
//				break;
//			case 5:
//				iconResId = R.drawable.speedpackage_kd06;
//				break;
//			default:
//				break;
//			}
//		} else
          	if(category.equals(TabDFragment.category)) { //手机号卡
			switch(line) {
			case 0:
				iconResId = R.drawable.speedpackage_sj01;
				break;
			case 1:
				iconResId = R.drawable.speedpackage_sj02;
				break;
			case 2:
				iconResId = R.drawable.speedpackage_sj03;
				break;
			case 3:
				iconResId = R.drawable.speedpackage_sj04;
				break;
			case 4:
				iconResId = R.drawable.speedpackage_sj05;
				break;
			case 5:
				iconResId = R.drawable.speedpackage_sj06;
				break;
			default:
				break;
			}
		} else if(category.equals(TabEFragment.category)) { //终端设备
			switch(line) {
			case 0:
				iconResId = R.drawable.speedpackage_zd01;
				break;
			case 1:
				iconResId = R.drawable.speedpackage_zd02;
				break;
			case 2:
				iconResId = R.drawable.speedpackage_zd03;
				break;
			case 3:
				iconResId = R.drawable.speedpackage_zd04;
				break;
			case 4:
				iconResId = R.drawable.speedpackage_zd05;
				break;
			case 5:
				iconResId = R.drawable.speedpackage_zd06;
				break;
			default:
				break;
			}
		}
		
		return iconResId;
	}
	
	private void showData() {
		for(int i=0; i<list.length(); i++) {
			int iconResId = getSpeedPackageIconId(i);
			
			int colorBackground;  //背景颜色
			int colorText;  //文字颜色

			colorBackground = 0xFFFFFFFF;
			/*
			switch(i) {
			case 0:
				colorBackground = 0xFF6dc9b4;
				break;
			case 1:
				colorBackground = 0xFF99c46a;
				break;
			case 2:
				colorBackground = 0xFFddc263;
				break;
			case 3:
				colorBackground = 0xFFf3af72;
				break;
			case 4:
				colorBackground = 0xFFdb778f;
				break;
			case 5:
				colorBackground = 0xFF9a88bc;
				break;
			default:
				colorBackground = 0xFFFFFFFF;
				break;
			}
			*/
			
			colorText = 0xFF000000;
			
			JSONObject json = list.optJSONObject(i);
			String speedId = json.optString("speedId");
			String speedName = json.optString("speedName");
			
			View view = inflater.inflate(R.layout.speed_line, null);
			//View speedview = view.findViewById(R.id.speedview);
			//speedview.setBackgroundColor(color1);
			TextView speedtitle = (TextView) view.findViewById(R.id.speedtitle);
			speedtitle.setText(speedName);
			//speedtitle.setTextColor(color2);

			showPackage(speedId, speedName, (LinearLayout) view.findViewById(R.id.packageview), json.optJSONArray("package"), colorBackground, colorText, iconResId);

			content_view.addView(view);
			
		}
	}
	
	private void showPackage(String speedId, String speedName, LinearLayout packageview, JSONArray packagelist, 
			int colorBackground, int colorText, int iconResId) {

		int length = packagelist.length();
		int lines = length/2 + length%2;
		for(int i=0; i<lines; i++) {
			View view = inflater.inflate(R.layout.speedpackage_line, null);
			
			((ImageView) view.findViewById(R.id.speedpackage_icon1)).setImageResource(iconResId);
			((ImageView) view.findViewById(R.id.speedpackage_icon2)).setImageResource(iconResId);
			
			View package1 = view.findViewById(R.id.package1);
			View package2 = view.findViewById(R.id.package2);
			package1.setBackgroundColor(colorBackground);
			package2.setBackgroundColor(colorBackground);
			TextView package1name = (TextView) view.findViewById(R.id.package1name);
			TextView package2name = (TextView) view.findViewById(R.id.package2name);
			package1name.setTextColor(colorText);
			package2name.setTextColor(colorText);
			
			int position1 = i*2;
			JSONObject json1 = packagelist.optJSONObject(position1);
			package1.setTag(new PackageInfo(speedId, speedName, json1.optString("id"), json1.optString("name")));
			package1.setOnClickListener(new PackageOnClickListener());
			package1name.setText(json1.optString("name"));
	
			if(length%2 != 0 && (i+1 == lines)) { //最后一行，单数
				package2.setVisibility(View.INVISIBLE);
			} else {
				int position2 = i*2+1;
				JSONObject json2 = packagelist.optJSONObject(position2);
				package2.setTag(new PackageInfo(speedId, speedName, json2.optString("id"), json2.optString("name")));
				package2.setOnClickListener(new PackageOnClickListener());
				package2name.setText(json2.optString("name"));
			}

			packageview.addView(view);
		}
	}

	private class PackageOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			PackageInfo info = (PackageInfo) v.getTag();			
			Intent i = new Intent(SpeedPackageActivity.this, ProductListActivity.class);
			i.putExtra("category", category);
			i.putExtra("mode", modeId);
			i.putExtra("speed", info.getSpeedId());
			i.putExtra("packageId", info.getPackageId());
			i.putExtra("packageName", info.getPackageName());
			startActivity(i);
		}
	}
	
	private class PackageInfo {
		String speedId;
		String speedName;
		String packageId;
		String packageName;
		
		public PackageInfo(String speedId, String speedName, String packageId, String packageName) {
			this.speedId = speedId;
			this.speedName = speedName;
			this.packageId = packageId;
			this.packageName = packageName;
		}


		public String getSpeedId() {
			return speedId;
		}


		public void setSpeedId(String speedId) {
			this.speedId = speedId;
		}


		public String getSpeedName() {
			return speedName;
		}


		public void setSpeedName(String speedName) {
			this.speedName = speedName;
		}


		public String getPackageId() {
			return packageId;
		}

		public void setPackageId(String packageId) {
			this.packageId = packageId;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
	}
}
