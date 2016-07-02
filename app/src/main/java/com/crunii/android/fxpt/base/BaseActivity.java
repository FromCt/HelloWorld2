package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crunii.android.fxpt.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王春晖 on 2015/11/13.
 */
public abstract class BaseActivity extends Activity implements ActivityExOper {

    protected BaseActivity thisActivity;
    private ProgressDialog loadMask;

    private static final String EXTRA_NAME = "params";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.thisActivity = this;

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView();
    }

    public void setLoadMask(ProgressDialog loadMask) {
        this.loadMask = loadMask;
    }

    protected void showMessage(CharSequence s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param map           要传递到下个activity的参数
     * @param activityClass
     */
    protected void gotoActivity(HashMap<String, Object> map, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(EXTRA_NAME, map);
        startActivity(intent);
    }

    protected HashMap<String, Object> getParams() {
        Intent intent = getIntent();
        HashMap<String, Object> params = (HashMap<String, Object>) intent.getSerializableExtra(EXTRA_NAME);
        return params;
    }

    protected void bindEvent(View view, String methodName, ViewEventType... eventTypes) {
        ActivityExOperUtil.bindEvent(this, view, methodName, eventTypes);
    }

    //ActivityExOper start

    @Override
    public void sendPost(final String url, final Map<String, String> params, final HttpPostProp  httpPostResult) {
        HttpTool.sendPost(this, url, params, httpPostResult);
    }

    @Override
    public void sendPost(final String url, final Object params, final HttpPostProp  httpPostResult) {
        HttpTool.sendPost(this, url, params, httpPostResult);
    }


    @Override
    public void getImg(final String url, final HttpPostProp  httpPostResult) {
        HttpTool.getImg(this, url, httpPostResult);
    }

    public void getImg(String url, final ImageView imageView) {
        getImg(url, new HttpPostProp() {
            @Override
            public void successBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    imageView.setImageResource(R.drawable.ct_test_picture);
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void fail(Exception e) {
                //super.fail(e);
                imageView.setImageResource(R.drawable.ct_test_picture);
            }
        });
    }

    @Override
    public void afterSetContentView() {
        ActivityExOperUtil.initViewEvent(this);
    }

    //ActivityExOper end


    @Override
    public void onBackPressed() {
        if(loadMask != null && loadMask.isShowing()){
            loadMask.dismiss();
        }
        super.onBackPressed();


    }

    public void doBack(View v) {
        onBackPressed();
    }
}
