package com.crunii.android.fxpt.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.Encryption;

import org.apache.http.util.EncodingUtils;

import java.util.Date;

@SuppressLint("SetJavaScriptEnabled")
public class WebPayActivity extends Activity {

	WebView wv;
	ProgressBar pb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webpay);

		pb = (ProgressBar) findViewById(R.id.progressBar);

		wv = (WebView) findViewById(R.id.wv_main);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new MyWebViewClient());
		wv.setWebChromeClient(new MyWebChromeClient());

		String acceptId = getIntent().getExtras().getString("orderId");
		String time = String.valueOf(new Date().getTime());
		String key= Encryption.MD5(Constant.localKeyStr + time + acceptId);

		String postData =  "acceptId=" + acceptId + "&time=" + time + "&key=" + key;
		wv.postUrl(Constant.URL.BESTPAYWAPPOST, EncodingUtils.getBytes(postData, "BASE64"));

	}

	protected class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("crun://")) {
				Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
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
		//无法判断支付结果，此返回值仅表明用户支付动作的结束
		setResult(RESULT_OK);
		finish();
	}

}
