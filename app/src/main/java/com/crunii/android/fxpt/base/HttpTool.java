package com.crunii.android.fxpt.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.crunii.android.fxpt.CRApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.crunii.android.fxpt.base.HttpTool.sendPost;

/**
 * Created by 王春晖 on 2015/11/18.
 */
public class HttpTool {


    static class HttpHandler extends Handler {
        HttpPostProp httpPostResult;
        ProgressDialog loadMask;

        public HttpHandler(HttpPostProp httpPostResult, ProgressDialog loadMask) {
            this.httpPostResult = httpPostResult;
            this.loadMask = loadMask;
        }

        @Override
        public void handleMessage(Message msg) {
            if (loadMask.isShowing()) {
                loadMask.dismiss();
            }
            Object msgObj = msg.obj;
            if (msgObj instanceof Exception) {
                httpPostResult.fail((Exception) msgObj);
            } else {
                String strJson = (String) msgObj;
                Log.d("strJson", strJson);
                JSONObject jsonObject = null;
                Boolean flag = null;
                String jsonmsg = null;
                JSONObject jsonrecord = null;
                try {
                    jsonObject = new JSONObject(strJson);
                    flag = jsonObject.getBoolean("success");
                    jsonmsg = jsonObject.getString("msg");
                } catch (JSONException e) {
                    httpPostResult.fail(e);
                    return;
                }
                if (flag) {
                    try {
                        jsonrecord = jsonObject.getJSONObject("record");
                    } catch (JSONException e) {
                        httpPostResult.fail(e);
                    }
                    httpPostResult.successJson(jsonrecord);
                } else {
                    httpPostResult.fail(new RuntimeException(jsonmsg));
                }
            }
            super.handleMessage(msg);
        }
    }

    static LruCache<String, Bitmap> imgCache = new LruCache<String, Bitmap>(100);

    public static void sendPost(Context context, final String url, final Object params, final HttpPostProp httpPostResult) {

        if(params instanceof Map){
            sendPost(context,url,(Map)params,httpPostResult);
            return ;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        Class clazz = params.getClass();
        Field[] fs = clazz.getFields();
        for (Field f : fs) {
            JsonName jn = f.getAnnotation(JsonName.class);
            String fname = f.getName();
            if (jn != null) {
                fname = jn.name();
            }
            Object val = null;
            try {
                val = f.get(params);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            map.put(fname, val);
        }


        sendPost(context, url, map, httpPostResult);
    }

    /**
     * 采取handle模式实现的http交互
     *
     * @param context
     * @param url
     * @param params
     * @param httpPostResult
     */
    public static void sendPost(Context context, final String url, final Map<String, String> params, final HttpPostProp httpPostResult) {

        PackageManager pm = context.getPackageManager();
        try {
            //发送版本信息到服务端
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            params.put("versionCode", Integer.toString(pi.versionCode));
            /**
             * 发送用户登录信息
             */
            params.put("userId", CRApplication.getId(context));
            params.put("token", CRApplication.getToken(context));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final String loadMaskMsg = httpPostResult.getLoadMaskMsg();
        final ProgressDialog loadMask = ProgressDialog.show(context, (CharSequence) null, loadMaskMsg);
        if(context instanceof BaseActivity){
            BaseActivity baseActivity = (BaseActivity) context;
            baseActivity.setLoadMask(loadMask);
        }
        final Integer readTimeOut = httpPostResult.getReadTimeOut();
        httpPostResult.setContext(context);
        final Integer connectionTimeOut = httpPostResult.getConnnectionTimeOut();
        final String encoding = httpPostResult.getEncoding();
        final Handler httpPostResultHandler = new HttpHandler(httpPostResult, loadMask);
        new Thread(new Runnable() {
            public void run() {
                String r = null;
                try {
                    r = sendUrl(url, params, connectionTimeOut, readTimeOut, encoding);
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = e;
                    httpPostResultHandler.sendMessage(msg);
                    e.printStackTrace();
                    return;
                }

                Message msg = new Message();
                msg.obj = r;//可以是基本类型，可以是对象，可以是List、map等；
                httpPostResultHandler.sendMessage(msg);
            }
        }).start();
    }


    public static void getImg(Context context, final String url, final HttpPostProp httpPostResult) {


        final Integer readTimeOut = httpPostResult.getReadTimeOut();
        httpPostResult.setContext(context);
        final Integer connectionTimeOut = httpPostResult.getConnnectionTimeOut();
        final Handler httpPostResultHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Object msgObj = msg.obj;
                if (msgObj instanceof Exception) {
                    httpPostResult.fail((Exception) msgObj);
                } else {
                    httpPostResult.successBitmap((Bitmap) msgObj);
                }
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            public void run() {
                Bitmap r = null;
                try {
                    r = getImg(url, connectionTimeOut, readTimeOut);
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = e;
                    httpPostResultHandler.sendMessage(msg);
                    e.printStackTrace();
                    return;
                }

                Message msg = new Message();
                msg.obj = r;//可以是基本类型，可以是对象，可以是List、map等；
                httpPostResultHandler.sendMessage(msg);
            }
        }).start();
    }

    public static Bitmap getImg(String url, Integer connectTimeout, Integer readTimeOut) throws Exception {
        if (!url.startsWith("http")) {
            throw new RuntimeException("图片url格式错误  url=" + url);
        }
        Bitmap b = imgCache.get(url);

        if (b != null) {
            return b;
        }
        URL sendUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) sendUrl.openConnection();
        conn.setRequestMethod("GET");// 提交模式
        conn.setConnectTimeout(connectTimeout);//连接超时 单位毫秒
        conn.setReadTimeout(readTimeOut);//读取超时 单位毫秒
        InputStream ins = conn.getInputStream();

        Bitmap bitmap = BitmapFactory.decodeStream(ins);
        ins.close();
        conn.disconnect();
        imgCache.put(url, bitmap);
        return bitmap;
    }


    private static String sendUrl(String url, Map<String, String> params, Integer connectTimeout, Integer readTimeOut, String encoding) throws Exception {
        URL sendUrl = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) sendUrl.openConnection();
        // Post请求必须设置允许输出
        conn.setDoOutput(true);
        // Post请求不能使用缓存
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");// 提交模式

        conn.setConnectTimeout(connectTimeout);//连接超时 单位毫秒
        conn.setReadTimeout(readTimeOut);//读取超时 单位毫秒
        OutputStream os = conn.getOutputStream();

        StringBuffer stringBuffer = new StringBuffer();

        Set<String> sets = params.keySet();
        for (String s : sets) {
            stringBuffer.append(s).append("=").append(params.get(s)).append("&");
        }
        Integer stringBufferLen = stringBuffer.length();
        stringBuffer.delete(stringBufferLen - 1, stringBufferLen);//删除最后一个&
        Log.i("url", url + "?" + stringBuffer);

        os.write(stringBuffer.toString().getBytes(encoding));

        InputStream ins = conn.getInputStream();
        InputStreamReader inr = new InputStreamReader(ins, encoding);

        BufferedReader br = new BufferedReader(inr);
        String temp = null;
        StringBuffer sbResult = new StringBuffer();

        while ((temp = br.readLine()) != null) {
            sbResult.append(temp);
        }

        return sbResult.toString();
    }

}
