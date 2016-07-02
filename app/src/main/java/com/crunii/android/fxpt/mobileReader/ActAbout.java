package com.crunii.android.fxpt.mobileReader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.TextView;

import com.crunii.android.fxpt.R;

public class ActAbout extends Activity {
	private TextView mTvSoftVer;
	private TextView mTvCorp;
	private TextView mTvMail;
	private TextView mTvWeb;
	private TextView mTvTel;
	private TextView mTvSoftVerjar;
	
	      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.act_about2);  
		    
          
		mTvSoftVer = (TextView) findViewById(R.id.tvSoftVer);
		mTvCorp = (TextView) findViewById(R.id.tvCorp);
		mTvSoftVerjar = (TextView) findViewById(R.id.tvSoftVerjar);
		
//		mTvMail = (TextView) findViewById(R.id.tvMail);
	//	mTvWeb = (TextView) findViewById(R.id.tvWeb);
	//	mTvTel = (TextView) findViewById(R.id.tvTel);
		 
	//	mTvTel.setText(mTvTel.getText() + "86-0533-3588123");
		//mTvSoftVer.getText() + getSoftVer()
		mTvSoftVer.setText("软件版本:A_V1.0 150727_S1");
		mTvSoftVerjar.setText("支撑库版本:A_V1.0 150727");		

	}
  
	            
	private String getSoftVer() {     
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		String strSoftVer = null;         
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			String packageName = info.packageName;// ����
  
			int versionCode = info.versionCode;          
         
			strSoftVer = info.versionName;  
//			//ȥ�������. 
//			String str[]=strSoftVer.split(" ");
//			String ver=str[0];
//			int n=ver.lastIndexOf(".");    
//			   
//			String v= ver.substring(0, n);  
//			     
//			strSoftVer=v+" "+str[1];        
		} catch (NameNotFoundException e) {          
			// TODO Auto-generated catch block   
			e.printStackTrace();    
		}       

		return strSoftVer; 
	}
        
	          
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {   
				finish();			  
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getMIEI() {

		Context context = getWindow().getContext();
		TelephonyManager telephonemanage = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonemanage.getDeviceId();

	}
}
