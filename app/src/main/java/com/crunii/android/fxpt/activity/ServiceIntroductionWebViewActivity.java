package com.crunii.android.fxpt.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 套餐简介详情
 *
 * Created by ct on 16/2/19.
 */
public class ServiceIntroductionWebViewActivity extends BaseActivity {

    private WebView webView;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_introduction_webview);


        webView = (WebView) findViewById(R.id.webview);

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webView.getSettings().setPluginsEnabled(true);
////        webView.getSettings().setPluginState(PluginState.ON);
//
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("页面加载中，请稍候...");
        progressBar.setMessage("Loading...");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
              /*  progressBar.setProgress(newProgress);
                progressBar.setMessage("Loading..." + newProgress*2 + "%");

                if (progressBar.isShowing()&&newProgress>50) {
                    progressBar.dismiss();
                }*/
                super.onProgressChanged(view, newProgress);
            }
        });

        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("packageId", id);


        sendPost(Constant.URL.PACKAGEINTRODUCEDETIAL, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                String packageUrl = (String) record.get("packageUrl");

                loadUrl(packageUrl);
            }
        });



    }



    public void loadUrl(String packageUrl)
    {
        if(webView != null) {
            Log.i("webView", "html url===" + Constant.CTX_PATH+packageUrl);
           // webView.loadUrl(url);
            progressBar = ProgressDialog.show(this, "页面加载中，请稍候......", "Loading...");

            webView.loadUrl(Constant.CTX_PATH+packageUrl);

           /* webView.loadDataWithBaseURL(null   //加载html源码
                    ,"<body></body>"
                    ,"text/html", "UTF-8", null);*/

            progressBar.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();

    }

    @Override
    public void onBackPressed() {
        if(progressBar!=null){
            progressBar.dismiss();
        }
        super.onBackPressed();

    }
}
