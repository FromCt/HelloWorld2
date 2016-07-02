package com.crunii.android.fxpt.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 王春晖 on 2015/11/17.
 */
public class HttpPostProp {


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Integer getConnnectionTimeOut() {
        return connnectionTimeOut;
    }

    public void setConnnectionTimeOut(Integer connnectionTimeOut) {
        this.connnectionTimeOut = connnectionTimeOut;
    }

    private Integer connnectionTimeOut = 5000; //默认连接超时 单位毫秒

    public String getLoadMaskMsg() {
        return loadMaskMsg;
    }

    public void setLoadMaskMsg(String loadMaskMsg) {
        this.loadMaskMsg = loadMaskMsg;
    }

    private String loadMaskMsg = "处理中，请稍候……";

    private Integer readTimeOut = 20000; //默认读取超时 单位毫秒

    public String getEncoding() {
        return encoding;
    }

    private String encoding = "utf-8";

    public void successJson(JSONObject t) {
        Map<String, Object> record = null;
        try {
            record = JsonTool.jsonOjbToMap(t);
        } catch (JSONException e) {
            e.printStackTrace();
            fail(e);
        }
        dealRecord(record);
    }

    public void dealRecord(Map record) {

    }


    public void successBitmap(Bitmap bm) {

    }


    public void fail(Exception e) {
        String errorMsg = "由于网络断开原因，等待连接后再访问:" + e;
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
    }


}
