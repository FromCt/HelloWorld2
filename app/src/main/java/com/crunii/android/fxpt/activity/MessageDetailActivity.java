package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.view.MyWebView;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by speedingsnail on 15/9/14.
 */
public class MessageDetailActivity extends Activity {


    MyWebView wv;
//    WebView wv;
    ProgressBar pb;
    int count = 0;
TextView title;
    String msgId = "";
    String catagory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         msgId = getIntent().getExtras().getString("msgId");
         catagory = getIntent().getExtras().getString("catagory");
        String titleStr = getIntent().getExtras().getString("title");


        setContentView(R.layout.message_detail);
        title = (TextView) findViewById(R.id.title);
        title.setText(titleStr);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        wv = (MyWebView) findViewById(R.id.wv_main_message);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new MyWebViewClient());
        wv.setWebChromeClient(new MyWebChromeClient());
    }

    public void doBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_close);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMsgDetail(catagory, msgId);
    }

    private void getMsgDetail(final String categoryType, final String msgId) {
        new BaseTask<String, String, JSONObject>(this, "正在加载...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().messageDetail(getApplicationContext(), categoryType, msgId);
            }

            @Override
            protected void onSuccess(JSONObject result) {
                String content = result.optString("content");
//                wv.loadData(content, "text/html", "utf-8");
                wv.loadDataWithBaseURL(null,content , "text/html", "utf-8", null);
               // wv.loadDataWithBaseURL("file://", result.optString("content"),"text/html", "utf-8", "about:blank");
            }

        }.execute("");

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


}
