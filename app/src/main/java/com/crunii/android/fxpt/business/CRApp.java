package com.crunii.android.fxpt.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;
import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.HttpResponseException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.http.HttpClient;
import com.crunii.android.base.http.HttpResponse;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.activity.MyProfitActivity;
import com.crunii.android.fxpt.activity.OrderAFragment;
import com.crunii.android.fxpt.activity.OrderBFragment;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.ErrorCodeHelper;
import com.crunii.android.fxpt.util.FileProgressListener;

public class CRApp {
    private static final String TAG = "CRApp";

    private static HttpClient client;

    public CRApp() {
        CRApp.client = new HttpClient();
    }

    public HttpClient getHttpClient() {
        return CRApp.client;
    }

    public Version checkNewVersion(Context context) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pkg", context.getPackageName());
        try {
            params.put("ver", Integer.toString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode));
        } catch (Exception e) {
            params.put("ver", "0");
        }

        HttpResponse rp = client.post(Constant.URL.UPDATE, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                Version result = new Version(jsonObject.getJSONObject("record"));
                return result;
            } else {
                throw new TaskResultException(jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject postFileUpload(File file, Map<String, Object> params,
                                     FileProgressListener progressListener) throws HttpException,
            IOException {

        MultipartEntity mpEntity = new MultipartEntity();

        //文本信息
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                StringBody par = new StringBody((String) entry.getValue());
                mpEntity.addPart(entry.getKey(), par);
            }
        }

        //文件数据
        if (file != null) {
            FileBody fileBody = new FileBody(file);
            mpEntity.addPart("file", fileBody);
        }

        HttpPost post = new HttpPost(Constant.URL.VERIFY_IMAGE_URL);

        post.setEntity(mpEntity);

        org.apache.http.HttpResponse response = client.getClient().execute(post);

        try {
            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }

    }


    public JSONObject verifyImage(File file, Map<String, Object> params,
                                  FileProgressListener progressListener) throws HttpException,
            IOException {

        MultipartEntity mpEntity = new MultipartEntity();

        //文本信息
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                StringBody par = new StringBody((String) entry.getValue());
                mpEntity.addPart(entry.getKey(), par);
            }
        }

        //文件数据
        if (file != null) {
            FileBody fileBody = new FileBody(file);
            mpEntity.addPart("file", fileBody);
        }

        HttpPost post = new HttpPost(Constant.URL.VERIFY_IMAGE_URL);

        post.setEntity(mpEntity);

        org.apache.http.HttpResponse response = client.getClient().execute(post);

        try {
            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), e);
        }

    }

    public String download(String url, String path) throws HttpException, IOException, TaskResultException {
        HttpResponse rp = client.get(url);
        Long length = rp.getContentLength();
        InputStream is = rp.asStream();

        File saveFile = new File(path);
        if (saveFile.getParentFile().exists() && saveFile.getParentFile().isFile()) {
            saveFile.getParentFile().delete();
        }
        saveFile.getParentFile().mkdirs();

        FileOutputStream os = new FileOutputStream(saveFile);

        try {
            Long count = 0L;
            Integer numread = -1;
            byte buf[] = new byte[1024];

            while ((numread = is.read(buf)) != -1) {
                os.write(buf, 0, numread);
                count += numread;
            }
            os.flush();

            if (!count.equals(length)) {
                throw new TaskResultException("文件下载不完整");
            }
        } finally {
            try {
                os.close();
            } finally {
                is.close();
            }
        }

        return null;
    }

    public Boolean getCode(Context context, String mobile) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);

        HttpResponse rp = client.get(Constant.URL.GETCODE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject login(Context context, String username, String password) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", username);
        params.put("code", password);

        HttpResponse rp = client.get(Constant.URL.LOGIN, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            // MyLog.i("ct","CRApp  select vlaue="+record.getString("selectValue"));
            return jsonObject;

        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject loginChoose(Context context, String id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);

        HttpResponse rp = client.get(Constant.URL.LOGIN_CHOOSE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            // MyLog.i("ct","CRApp  select vlaue="+record.getString("selectValue"));
            return jsonObject;

        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public Boolean logout(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.LOGOUT, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONObject home(Context context,String imsi) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("imsi", imsi);
        if(Constant.TEST_FLAG) getURL("home",Constant.URL.HOME,params);
        HttpResponse rp = client.get(Constant.URL.HOME, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject myprofit(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        if(Constant.TEST_FLAG) getURL("myprofitNew",Constant.URL.MYPROFITNEW,params);
        HttpResponse rp = client.get(Constant.URL.MYPROFITNEW, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject profitdetail(Context context, String date, String type, String userId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("date", date);
        if (type.equals(MyProfitActivity.ProfitDetailType.partner.toString())) {//收益伙伴详情
            params.put("userId", userId);
        } else {
            params.put("userId", CRApplication.getId(context));
        }
        params.put("type", type);

        if(Constant.TEST_FLAG) getURL("profitdetailNew",Constant.URL.PROFITDETAILNEW,params);
        HttpResponse rp = client.get(Constant.URL.PROFITDETAILNEW, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONArray profitlist(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        if(Constant.TEST_FLAG) getURL("profitlistNew",Constant.URL.PROFITLISTNEW,params);
        HttpResponse rp = client.get(Constant.URL.PROFITLISTNEW, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONArray("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage orderlist(Context context, int pageindex, String keyword,String category,String queryType) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("pageindex", pageindex);
        params.put("pagesize", 20);
        params.put("keyword", keyword);
        params.put("category", category);
        params.put("queryType", queryType);

        if(Constant.TEST_FLAG) getURL("orderlist",Constant.URL.ORDERLIST,params);
        HttpResponse rp = client.post(Constant.URL.ORDERLIST, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage messagelist(Context context, String category, int pageindex) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("category", category);
        params.put("pageindex", pageindex);
        params.put("pagesize", 20);
        if(Constant.TEST_FLAG) getURL("messagelist",Constant.URL.MESSAGE,params);
        HttpResponse rp = client.get(Constant.URL.MESSAGE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    private void getURL(String urlName ,String url,Map<String, Object> params){
        StringBuffer outUrl = new StringBuffer(url+"?");
        if(!params.isEmpty()){
            params.keySet();
            for(String key : params.keySet()){
                outUrl.append(key+"=");
                outUrl.append(params.get(key));
                outUrl.append("&");
            }
        }
        Log.i(urlName,outUrl.toString());
    }


    public boolean readmessage(Context context,String catagoryType,String msgId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("catagoryType", catagoryType);
        params.put("msgId", msgId);
            if(Constant.TEST_FLAG) getURL("readmessage",Constant.URL.READMESSAGE,params);
        HttpResponse rp = client.post(Constant.URL.READMESSAGE, params);

        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject messageDetail(Context context,String catagoryType,String msgId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("category", catagoryType);
        params.put("msgId", msgId);

        HttpResponse rp = client.post(Constant.URL.MESSAGEDETAIL, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject personalinfo(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.PERSONALINFO, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }



    public Void savecrmnum(Context context, String crm_num) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("crm_num", crm_num);

        HttpResponse rp = client.get(Constant.URL.SAVECRMNUM, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return null;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Void saveActivityAddress(Context context, String name, String addr, String phone) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("name", name);
        params.put("addr", addr);
        params.put("phone", phone);

        HttpResponse rp = client.get(Constant.URL.SAVEACTIVITYADDRESS, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return null;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject accountinfo(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.ACCOUNTINFO, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean accountCode(Context context) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.ACCOUNTCODE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public Boolean accountupdate(Context context, String bank, String card, String name, String citizenId, String code) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("bank", bank);
        params.put("card", card);
        params.put("name", name);
        params.put("citizenId", citizenId);
        params.put("code", code);
        if(Constant.TEST_FLAG) getURL("accountupdate",Constant.URL.ACCOUNTUPDATE,params);
        HttpResponse rp = client.post(Constant.URL.ACCOUNTUPDATE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject ePayConfirm(Context context,boolean isTelecom) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("isTelecom", isTelecom);

        HttpResponse rp = client.get(Constant.URL.EPAYCONFIRM, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage infoquery(Context context, String query, String type, int pageindex) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("type", type);
        params.put("query", query);
        params.put("pageindex", pageindex);
        params.put("pagesize", 20);

        HttpResponse rp = client.get(Constant.URL.INFOQUERY, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONObject addressquery(Context context, String query,String category) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("query", query);
        params.put("category", category);

        HttpResponse rp = client.get(Constant.URL.ADDRESSQUERY, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject gqQuery(Context context, String word  ) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("word", word);

        HttpResponse rp = client.get(Constant.URL.GQQUERY, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {

                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean cancelOrder(Context context, String orderId, String reason) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);
        params.put("reason", reason);

        HttpResponse rp = client.get(Constant.URL.CANCELORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean refundOrder(Context context, String orderId, String reason) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);
        params.put("reason", reason);

        HttpResponse rp = client.get(Constant.URL.REFUNDORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean returnOrder(Context context, String orderId, String reason) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);
        params.put("reason", reason);

        HttpResponse rp = client.get(Constant.URL.RETURNORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public Boolean confirmOrder(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);

        HttpResponse rp = client.get(Constant.URL.CONFIRMORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean addressDel(Context context, String id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("id", id);

        HttpResponse rp = client.get(Constant.URL.ADDRESSDEL, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public Boolean addressAdd(Context context, String name, String addr, String phone, boolean isDefault) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("name", name);
        params.put("addr", addr);
        params.put("phone", phone);
        if (isDefault) {
            params.put("isDefault", "1");
        } else {
            params.put("isDefault", "0");
        }

        HttpResponse rp = client.get(Constant.URL.ADDRESSADD, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean addressUpdate(Context context, String name, String addr, String phone, boolean isDefault, String id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("name", name);
        params.put("addr", addr);
        params.put("phone", phone);
        if (isDefault) {
            params.put("isDefault", "1");
        } else {
            params.put("isDefault", "0");
        }
        params.put("id", id);

        HttpResponse rp = client.get(Constant.URL.ADDRESSUPDATE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONArray mode(Context context, String category) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("category", category);
        if(Constant.TEST_FLAG) getURL("mode",Constant.URL.MODE,params);
        HttpResponse rp = client.get(Constant.URL.MODE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONArray("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONArray speedpackage(Context context, String mode) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("mode", mode);

        HttpResponse rp = client.get(Constant.URL.SPEEDPACKAGE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONArray("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage product(Context context, int pageindex, String mode, String speed, String packageId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("pageindex", pageindex);
        params.put("pagesize", 20);
        params.put("mode", mode);
        params.put("speed", speed);
        params.put("package", packageId);

        HttpResponse rp = client.get(Constant.URL.PRODUCT, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject productdetail(Context context, String category, String mode, String speed, String packageId, String goodsCode) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("category", category);
        params.put("mode", mode);
        params.put("speed", speed);
        params.put("packageId", packageId);
        params.put("goodsCode", goodsCode);
        Log.d("url", params.toString());
        if (Constant.TEST_FLAG) {
            Log.d("url", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.PRODUCTDETAIL, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                Log.e("productdetail=", jsonObject.getJSONObject("record").toString());
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONArray detailcontent(Context context, String goodsCode, String productCode) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("goodsCode", goodsCode);
        params.put("productCode", productCode);
        Log.i("detailcontent", params.toString());
        HttpResponse rp = client.get(Constant.URL.DETAILCONTENT, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONArray("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean submitcardinfo(Context context, String number, String puk, String name, String citizenId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("number", number);
        params.put("puk", puk);
        params.put("name", name);
        params.put("citizenId", citizenId);

        HttpResponse rp = client.get(Constant.URL.SUBMITCARDINFO, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject verifyNumber(Context context, String type, String number) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("type", type);
        params.put("number", number);

        HttpResponse rp = client.get(Constant.URL.VERIFYNUMBER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject createOrder(Context context, Map<String, Object> params) throws HttpException,
            IOException, TaskResultException {
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        if(Constant.TEST_FLAG) getURL("createOrder",Constant.URL.CREATEORDER,params);
        HttpResponse rp = client.post(Constant.URL.CREATEORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject changePayType(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("token", CRApplication.getToken(context));
        if(Constant.TEST_FLAG) getURL("changePayType",Constant.URL.CHANGEPAYTYPE,params);
        HttpResponse rp = client.post(Constant.URL.CHANGEPAYTYPE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject bindImage(Context context, String orderId, String image1Id, String image2Id, String image3Id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);
        params.put("image1Id", image1Id);
        params.put("image2Id", image2Id);
        params.put("image3Id", image3Id);

        HttpResponse rp = client.get(Constant.URL.BINDIMAGE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject payStatus(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);

        HttpResponse rp = client.get(Constant.URL.PAYSTATUS, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject goodsprice(Context context, String productCode, String salemode, String goodsCode, String contractId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("productCode", productCode);
        params.put("salemode", salemode);
        params.put("goodsCode", goodsCode);
        params.put("contractId", contractId);

        HttpResponse rp = client.get(Constant.URL.GOODSPRICE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject partner(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.PARTNER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean closeaccount(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.CLOSEACCOUNT, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage partnerlist(Context context, int pageindex, String keyword) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("pageindex", pageindex);
        params.put("pagesize", 20);
        params.put("query", keyword);
        if (Constant.TEST_FLAG) {
            Log.d("partnerlist", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.PARTNERLIST, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean removePartner(Context context, String id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("id", id);

        HttpResponse rp = client.get(Constant.URL.REMOVEPARTNER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean addPartner(Context context,
                              String code, String name, String phone, String citizenId, String channelId, String companyId,
                              String stationId, String profitView, String commissionMode, String commissionControl, String commissionPercent)
            throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("code", code);
        params.put("name", name);
        params.put("phone", phone);
        params.put("citizenId", citizenId);
        params.put("channelId", channelId);
        params.put("companyId", companyId);
        params.put("stationId", stationId);
        params.put("profitView", profitView);
        params.put("commissionMode", commissionMode);
        params.put("commissionControl", commissionControl);
        params.put("commissionPercent", commissionPercent);

        HttpResponse rp = client.get(Constant.URL.ADDPARTNER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean modifyPartner(Context context,
                                 String id, String channelId, String companyId, String stationId, String profitView,
                                 String commissionMode, String commissionControl, String commissionPercent)
            throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("id", id);
        params.put("channelId", channelId);
        params.put("companyId", companyId);
        params.put("stationId", stationId);
        params.put("profitView", profitView);
        params.put("commissionMode", commissionMode);
        params.put("commissionControl", commissionControl);
        params.put("commissionPercent", commissionPercent);

        HttpResponse rp = client.get(Constant.URL.MODIFYPARTNER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public Boolean partnerVerify(Context context, String phone) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("phone", phone);

        HttpResponse rp = client.get(Constant.URL.PARTNERVERIFY, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JsonPage choosenumber(Context context, int pageindex, String areaId, String section, String query, String databaseId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("pageindex", pageindex);
        params.put("pagesize", 15);
        params.put("areaId", areaId);
        params.put("section", section);
        params.put("query", query);
        params.put("databaseId", databaseId);

        HttpResponse rp = client.get(Constant.URL.CHOOSENUMBER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONArray contractpackage(Context context, String goodsCode, String contractId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("goodsCode", goodsCode);
        params.put("contractId", contractId);

        HttpResponse rp = client.get(Constant.URL.CONTRACTPACKAGE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONArray("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean IDCardValidate(Context context, String custName, String custId) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("custName", custName);
        params.put("custId", custId);

        HttpResponse rp = client.post(Constant.URL.IDCARDVALIDATE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean cheakCardNum(Context context, String num) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("num", num);

        HttpResponse rp = client.get(Constant.URL.CHECKCARDNUM, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject transcribe(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.TRANSCRIBE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject isSupportTranscribe(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.ISSUPPORTTRANSCRIBE, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject isUploadImage(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.ISUPLOADIMAGE, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONObject isPayOrNumberTranscribe(Context context,String handlePointId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("handlePointId", handlePointId);

        HttpResponse rp = client.get(Constant.URL.ISPAYORNUMBERTRANSCRIBE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject submitTranscribe(Context context, Map<String, Object> map) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.putAll(map);
        System.out.println(params);
        HttpResponse rp = client.post(Constant.URL.SUBMITTRANSCRIBE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JsonPage transcribeOrderList(Context context, int pageindex, String keyword,String category,String queryType ,String queryTime,String queryPartnerId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("pageindex", pageindex);
        params.put("pagesize", 10);
        params.put("keyword", keyword);
        params.put("category", category);
        if(category.equals(OrderBFragment.Category.mySelfOrder.toString())){
            params.put("queryType", queryType);
        }else if(category.equals(OrderAFragment.Category.myPartnerOrder.toString())){
            params.put("queryTime", queryTime);
            params.put("queryPartnerId", queryPartnerId);
        }
        Log.i("transcribeOrderList",params.toString());
        HttpResponse rp = client.get(Constant.URL.TRANSCRIBEORDERLIST, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("data");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, pageindex, json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public Boolean blOrderCancel(Context context, String orderId, String reason) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", CRApplication.getId(context));
        params.put("token", CRApplication.getToken(context));
        params.put("orderId", orderId);
        params.put("reason", reason);

        HttpResponse rp = client.get(Constant.URL.BLCANCELORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject cardSaleSaveOrder(Context context, String goodsCode, String countNumber, String productCode) throws HttpException, IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("goodCode", goodsCode);
        params.put("number", countNumber);
        params.put("productCode", productCode);
        if (Constant.TEST_FLAG) {
            Log.d("cardSaleSaveOrder", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.CARDSALESAVEORDER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject cardList(Context context, String orderId, String goodsId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("goodsId", goodsId);
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.CARDLIST, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject calServiceFee(Context context, Map<String, Object> params) throws HttpException,
            IOException, TaskResultException {
        params.put("token", CRApplication.getToken(context));
        if(Constant.TEST_FLAG){
            getURL("calServiceFee",Constant.URL.CALSERVICEFEE,params);
        }
        HttpResponse rp = client.get(Constant.URL.CALSERVICEFEE, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

//	token       //token
//  id          //id
//	queryType   //type=first第一次请求,company分公司，substation支局，member支局成员，distributor分销商下属
//	queryKey    //查询字段：姓名、手机、类别
//	pageindex   //(必填) 当前页码
//	pagesize    //(必填) 分页单位

    public JSONObject distributionSearch(Context context, String id, String queryType, String queryKey, String queryWord, int pageindex) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("id", id);
        params.put("queryType", queryType);
        params.put("queryKey", queryKey);
        params.put("queryWord", queryWord);
        params.put("pageindex", pageindex);
        params.put("pagesize", 100);
        if (Constant.TEST_FLAG) {
            Log.d("distributionSearch", params.toString());
        }
        HttpResponse rp = client.post(Constant.URL.DISTRIBUTIONSEARCH, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject distributorDetail(Context context, String id) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("id", id);
        if (Constant.TEST_FLAG) {
            Log.i("distributorDetail", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.DISTRIUBTIONDETAIL, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject checkPartnerSystem(Context context, String phoneNumber, String code, String type, String userId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("phoneNumber", phoneNumber);
        params.put("code", code);
        params.put("type", type);
        params.put("userId", userId);
        if (Constant.TEST_FLAG) {
            Log.d("checkPartnerSystem", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.CHECKPARTNERSYSTEM, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public boolean isBuildPartner(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        if (Constant.TEST_FLAG) {
            Log.d("isBuildPartner", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.ISBUILDPARTNER, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return true;

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public boolean savePartnerMsg(Context context, String phoneNumber, String name, String id, String type, String commissionControlValue, int commissionPercent) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("phoneNumber", phoneNumber);
        params.put("name", name);
        params.put("id", id);
        params.put("type", type);
        params.put("commissionControl", commissionControlValue);
        params.put("commissionPercent", commissionPercent);
        if (Constant.TEST_FLAG) {
            Log.d("savePartnerMsg", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.SAVEPARTNERMSG, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return true;

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }
    public boolean savePartnerSystem(Context context, String phoneNumber, String code, String partnerName, String channelId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("phoneNumber", phoneNumber);
        params.put("code", code);
        params.put("partnerName",partnerName);
        params.put("channelId", channelId);
        if (Constant.TEST_FLAG) {
            Log.d("savePartnerSystem", params.toString());
        }
        HttpResponse rp = client.get(Constant.URL.SAVEPARTNERSYSTEM, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return true;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public boolean buildPartnerCode(Context context, String phoneNumber) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("phoneNumber", phoneNumber);
        HttpResponse rp = client.get(Constant.URL.BUILDPARTNERCODE, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return true;

            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject cheakbuyLimit(Context context, String goodsCode) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("goodsCode", goodsCode);
        HttpResponse rp = client.get(Constant.URL.CHECKBUYLIMIT, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject isToDistributor(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        HttpResponse rp = client.get(Constant.URL.ISTODISTRIBUTOR, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject myPartnerList(Context context, String type, int year, String mouth,String userId,int pageindex) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));
        params.put("type", type);
        params.put("year", year);
        params.put("mouth", mouth);
        params.put("userId", userId);
        params.put("pageindex", pageindex);
        params.put("pagesize", 15);
        HttpResponse rp = client.get(Constant.URL.MYPARTNERLISTNEW, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    //orderDetial
    public JSONObject orderDetial(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.post(Constant.URL.ORDETIAL, params);
        // Log.i("ct","RESponse="+rp.asString());
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                Log.i("ct","jsonObject success=============");
                JSONObject json = jsonObject.getJSONObject("record");

                return json;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.i("ct","JSONException======");
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    //logisticDetial
    public JSONObject logisticDetial(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.LOGISTICDETIAL, params);
        // Log.i("ct","RESponse="+rp.asString());
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                Log.i("ct","jsonObject success=============");
                JSONObject json = jsonObject.getJSONObject("record");

                return json;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.i("ct","JSONException======");
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    //exeposeDetial
    public JSONObject exeposeDetial(Context context, String orderId) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.EXEPOSEDETIAL, params);
        // Log.i("ct","RESponse="+rp.asString());
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                //  Log.i("ct","jsonObject success=============");
                JSONObject json = jsonObject.getJSONObject("record");

                return json;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JSONObject userDetail(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.USERDETAIL, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");

                return json;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    //welcomeView
    public JSONObject welcomePage(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.WELCOMEPAGE, params);
        // Log.i("ct","RESponse="+rp.asString());
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                //  Log.i("ct","jsonObject success=============");
                JSONObject json = jsonObject.getJSONObject("record");

                return json;
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }


    public JSONObject myNetStroe(Context context) throws HttpException,
            IOException, TaskResultException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", CRApplication.getToken(context));

        HttpResponse rp = client.get(Constant.URL.MYNETSTROE, params);
        JSONObject jsonObject = rp.asJSONObject();
        try {
            if (jsonObject.getBoolean("success")) {
                return jsonObject.optJSONObject("record");
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }

    public JsonPage getNetStoreList(Context context, Map<String, Object> params ) throws HttpException,
            IOException, TaskResultException {
        params.put("token", CRApplication.getToken(context));
        params.put("pagesize", 20);
        if (Constant.TEST_FLAG) {
            Log.d("partnerlist", params.toString());
        }
        if(Constant.TEST_FLAG) getURL("myNetStoreOrderlist",Constant.URL.NETSTOREORDERLIST,params);
        HttpResponse rp = client.post(Constant.URL.NETSTOREORDERLIST, params);
        JSONObject jsonObject = rp.asJSONObject();

        try {
            if (jsonObject.getBoolean("success")) {
                JSONObject json = jsonObject.getJSONObject("record");
                JSONArray array = json.getJSONArray("orderList");
                List<JSONObject> orderList = new ArrayList<JSONObject>();
                for (int i = 0; i < array.length(); i++) {
                    orderList.add(array.getJSONObject(i));
                }
                return new JsonPage(orderList, json.getInt("pageindex"), json.getInt("prePage"), json.getInt("nextPage"));
            } else {
                throw new TaskResultException(ErrorCodeHelper.getErrorCode(jsonObject.getString("msg"), context));
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage(), e);
            throw new HttpResponseException(e.getMessage(), e);
        }
    }
}
