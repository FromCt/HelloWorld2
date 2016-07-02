package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王春晖 on 2015/11/13.
 */
public abstract class BaseListActivity extends ListActivity implements ActivityExOper {

    protected ListActivity thisActivity;

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

    protected void showMessage(CharSequence s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param map           要传递到下个activity的参数
     * @param activityClass
     */
    protected void gotoActivity(HashMap<String, Object> map, Class<? extends  Activity> activityClass) {
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
    public void getImg(final String url, final HttpPostProp  httpPostResult) {
         HttpTool.getImg(this, url, httpPostResult);
    }

    @Override
    public void afterSetContentView() {
        ActivityExOperUtil.initViewEvent(this);
    }

    //ActivityExOper end

}
