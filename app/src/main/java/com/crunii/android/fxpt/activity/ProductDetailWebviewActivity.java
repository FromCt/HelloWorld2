package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.view.MyWebView;
import com.crunii.android.fxpt.view.MyWebView.OnScrollToTopListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ProductDetailWebviewActivity extends Activity {

	MyWebView wv;
	ProgressBar pb;
	
	View toolbar;
	Button tab1btn, tab2btn, tab3btn;
	ScrollView sv_view;
	LinearLayout ll_product_parameter;

	
	String goodsCode,productCode;
	JSONArray data;
	Context mContext;
	int count = 0;
	LayoutInflater inflater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail_webview);
		mContext = this;
		inflater = LayoutInflater.from(mContext);
		pb = (ProgressBar) findViewById(R.id.progressBar);

		wv = (MyWebView) findViewById(R.id.wv_main);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new MyWebViewClient());
		wv.setWebChromeClient(new MyWebChromeClient());

		toolbar = findViewById(R.id.toolbar);
		tab1btn = (Button) findViewById(R.id.tab1btn);
		tab2btn = (Button) findViewById(R.id.tab2btn);
		tab3btn = (Button) findViewById(R.id.tab3btn);

		sv_view = (ScrollView) findViewById(R.id.sv_view);
		ll_product_parameter = (LinearLayout) findViewById(R.id.ll_product_parameter);

		goodsCode = getIntent().getExtras().getString("goodsCode");
		productCode = getIntent().getExtras().getString("productCode");

		//监听滑动到顶部事件
		wv.setOnScrollToTopLintener(new OnScrollToTopListener() {
            @Override  
            public void onScrollTopListener(boolean isTop) {
            	if(isTop) {
            		count ++;
            		if(count > 10) {
            			count = 0;
            			onBackPressed();
            		}
            	}
            }
        });
		
		loadDetailContent();
	}

	public void doBack(View v) {
		onBackPressed();
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
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(0, R.anim.activity_close);
	}

	private void loadDetailContent() {
		toolbar.setVisibility(View.INVISIBLE);

		new BaseTask<String, String, JSONArray>(this, "请稍后...") {

			@Override
			protected JSONArray doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().detailcontent(ProductDetailWebviewActivity.this, goodsCode,productCode);
			}

			@Override
			protected void onSuccess(JSONArray result) {
				toolbar.setVisibility(View.VISIBLE);
				showData(result);
//				showParameter(result);
			}
		}.execute("");

	}
	
	private void showData(JSONArray result) {

		data = result;
		int size = data.length();
		JSONObject json;
		
		if(size >= 3) {
			json = data.optJSONObject(2);
			tab3btn.setText(json.optString("name"));

			tab3btn.setVisibility(View.VISIBLE);
		} else {
			tab3btn.setVisibility(View.GONE);
		}
		
		if(size >= 2) {
			json = data.optJSONObject(1);
			tab2btn.setText(json.optString("name"));
			
			tab2btn.setVisibility(View.VISIBLE);
		} else {
			tab2btn.setVisibility(View.GONE);
		}
		
		if(size >= 1) {
			json = data.optJSONObject(0);
			tab1btn.setText(json.optString("name"));
			
			doTab1(null);
		} else {
			Toast.makeText(this, "图文详情不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}
	
	public void doTab1(View v) {
		tab1btn.setTextColor(Color.parseColor("#FF0000"));
		tab2btn.setTextColor(Color.parseColor("#333333"));
		tab3btn.setTextColor(Color.parseColor("#333333"));
		loadWebContent(0);
	}

	public void doTab2(View v) {
		tab1btn.setTextColor(Color.parseColor("#333333"));
		tab2btn.setTextColor(Color.parseColor("#FF0000"));
		tab3btn.setTextColor(Color.parseColor("#333333"));
		loadWebContent(1);
	}

	public void doTab3(View v) {
		tab1btn.setTextColor(Color.parseColor("#333333"));
		tab2btn.setTextColor(Color.parseColor("#333333"));
		tab3btn.setTextColor(Color.parseColor("#FF0000"));
		loadWebContent(2);
	}
	
//	private void loadWebContent(int i) {
//		JSONObject content = data.optJSONObject(i);
//		wv.loadDataWithBaseURL(null, content.optString("detail"), "text/html", "utf-8", null);
//	}

	private void loadWebContent(int i) {
		JSONObject content = data.optJSONObject(i);

		boolean  isProductParameter = content.optBoolean("isProductParameter");
		if(isProductParameter){
			sv_view.setVisibility(View.VISIBLE);
			wv.setVisibility(View.GONE);
			doParameter(content.optJSONArray("detail"));
		}else {
			sv_view.setVisibility(View.GONE);
			wv.setVisibility(View.VISIBLE);
			wv.loadDataWithBaseURL(null, content.optString("detail"), "text/html", "utf-8", null);
		}
	}

    public void doDetailView(View v) {
        onBackPressed();
    }


	public void showParameter(JSONArray result){
		data = result;
		int size = data.length();
		JSONObject json;

		if(size >= 3) {
			json = data.optJSONObject(2);
			tab3btn.setText(json.optString("name"));

			tab3btn.setVisibility(View.VISIBLE);
		} else {
			tab3btn.setVisibility(View.GONE);
		}

		if(size >= 2) {
			json = data.optJSONObject(1);
			boolean  isProductParameter = json.optBoolean("isProductParameter");
			if(isProductParameter){
				doParameter(json.optJSONArray("detail"));
			}
			tab2btn.setText(json.optString("name"));

			tab2btn.setVisibility(View.VISIBLE);
		} else {
			tab2btn.setVisibility(View.GONE);
		}

		if(size >= 1) {
			json = data.optJSONObject(0);
			tab1btn.setText(json.optString("name"));

			doTab1(null);
		} else {
			Toast.makeText(this, "图文详情不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}
	}



	public void doParameter(JSONArray jsonArray){
		int count = ll_product_parameter.getChildCount();

		if(count > 0){
			return ;
		}
		for(int i=0;i<jsonArray.length();i++){
			String topicTitle = jsonArray.optJSONObject(i).optString("groupName");
			TextView textTopicTitle = new TextView(mContext);
			textTopicTitle.setTextColor(Color.parseColor("#333333"));
			textTopicTitle.setTextSize(16);
			textTopicTitle.setGravity(Gravity.CENTER);
			textTopicTitle.setText(topicTitle);
			textTopicTitle.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
			textTopicTitle.setHeight(50);
			ll_product_parameter.addView(textTopicTitle);

			JSONArray data = jsonArray.optJSONObject(i).optJSONArray("groupAttrs");
			for(int j=0;j<data.length();j++){
				View view  = inflater.inflate(R.layout.product_parameter,null);
				TextView title = (TextView) view.findViewById(R.id.title);
				TextView value = (TextView) view.findViewById(R.id.value);
				title.setText(data.optJSONObject(j).optString("attrName"));
				value.setText(data.optJSONObject(j).optString("attrValue"));
				ll_product_parameter.addView(view);
			}


		}

	}


}
