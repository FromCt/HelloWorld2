package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;

import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2016/4/14.
 */
public class AdBannerShow extends Activity{
    private AsynImageLoaderWithSDCache imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_c_bannershow);
        imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), this);
        initView();
    }

    private void initView() {
        ImageView imageView= (ImageView) findViewById(R.id.img_abannershow);
        TextView  textView= (TextView) findViewById(R.id.text_abannershow);
        Intent i=this.getIntent();
        if (i!=null) {
            String img=i.getStringExtra("bgImg");
            String  desc=i.getStringExtra("desc");
            imageLoader.showImageAsyn(new SoftReference<Object>(imageView),img,R.drawable.adbanner);
            textView.setText(desc);
        }
    }

    public void doBack(View v) {
        onBackPressed();
    }
}
