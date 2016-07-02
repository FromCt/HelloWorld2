package com.crunii.android.fxpt.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.Constant;

@SuppressLint("SetJavaScriptEnabled")
public class WebviewActivity extends Activity {

	WebView wv;
	ProgressBar pb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		pb = (ProgressBar) findViewById(R.id.progressBar);

		wv = (WebView) findViewById(R.id.wv_main);
		wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

		wv.setWebViewClient(new MyWebViewClient());
		wv.setWebChromeClient(new MyWebChromeClient());

		String url = getIntent().getExtras().getString("url");
		if(url.startsWith(Constant.CTX_ACTIVITY)) {
			if(url.contains("?")) {
				url = url + "&token=" + CRApplication.getToken(this);
			} else {
				url = url + "?token=" + CRApplication.getToken(this);
			}
		}

		wv.loadUrl(url);
	}

	protected class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			if(url.startsWith("activity://")) { //activity
				Intent intent = new Intent();
				
				String[] array1 = url.split("\\?");
				String[] array2 = array1[0].split("//");
				String activity = array2[1];
				intent.setClassName(WebviewActivity.this, activity);
				
				String[] array3 = array1[1].split("&");
				for(int i=0; i<array3.length; i++) {
					String[] array4 = array3[i].split("=");
					String paramName = array4[0];
					String paramValue = array4[1];
					intent.putExtra(paramName, paramValue);
				}
				startActivity(intent);
				return true;
			}
			
			return false;
		}
	}

	protected class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				pb.setVisibility(View.INVISIBLE);
			} else {
				pb.setVisibility(View.VISIBLE);
			}
			pb.setProgress(newProgress);
		}
	}
	
	public void doPayDone(View v) {
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		//set result
		finish();
	}

}
