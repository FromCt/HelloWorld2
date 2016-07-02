package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/30.
 */
public class CreatPartnerActivity extends Activity {

    private Context mContext;
    private String phoneNumber = "",codeNumber = "",channelId = "",requestType = "",userId="",userName="";
    private EditText phone, code, name;
    private Button btnSend ,bt_select;
    int sendLimitTime = 0;
    private LinearLayout ll_top,ll_bottom,ll_power_update;
    private List<JSONObject> channelList = new ArrayList<JSONObject>();
    RadioButton btnPercent;
    int commissionPercent;
    RadioGroup  commissionControl;
    ImageView del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_partner);
        mContext = this;
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        name = (EditText) findViewById(R.id.name);
        btnSend = (Button) findViewById(R.id.btnSend);
        bt_select = (Button) findViewById(R.id.bt_select);
        ll_top = (LinearLayout)findViewById(R.id.ll_top);
        ll_bottom = (LinearLayout)findViewById(R.id.ll_bottom);
        ll_power_update = (LinearLayout)findViewById(R.id.ll_power_update);
        commissionControl = (RadioGroup)findViewById(R.id.commissionControl);
        del = (ImageView) findViewById(R.id.del);
        Intent intent = getIntent();
        requestType = intent.getExtras().getString("requestType");
        if(requestType.equals("add")){//新增的时候
            ll_bottom.setVisibility(View.GONE);
            ll_power_update.setVisibility(View.GONE);
            name.setEnabled(true);
            del.setVisibility(View.GONE);
        }else if(requestType.equals("update")){
            ll_top.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.VISIBLE);
            ll_power_update.setVisibility(View.VISIBLE);
            String userName = intent.getExtras().getString("userName");//伙伴姓名
            phoneNumber = intent.getExtras().getString("phone");
            channelId = intent.getExtras().getString("channelId");
            String channelName = intent.getExtras().getString("channelName");
            userId = intent.getExtras().getString("userId");
            del.setVisibility(View.VISIBLE);

            boolean isMsg = intent.getExtras().getBoolean("isMsg"); //是否有有效信息
            boolean isDQ = intent.getExtras().getBoolean("isDQ"); //是否是电子渠道
            String channelListStr = intent.getExtras().getString("channelList");
            try {
                JSONArray list =new JSONArray(channelListStr);
                for(int i=0;i < list.length();i++){
                    channelList.add(list.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            name.setText(userName);//姓名不让修改
            name.setEnabled(false);
            if(isMsg){//有有效信息
                if(isDQ){//电子渠道,可以选择渠道类别
                    bt_select.setClickable(true);
                    bt_select.setText(channelName);
                }else{
                    bt_select.setClickable(false);
                    bt_select.setText(channelName);
                }
            }else{
                bt_select.setClickable(true);
                bt_select.setText(channelName);
            }
            //设置酬金分配
            int commissionControlValue = intent.getExtras().getInt("commissionControl");;
            if(commissionControlValue == 1) {
                ((RadioButton)commissionControl.getChildAt(0)).setChecked(true);
            } else if(commissionControlValue == 2) {
                ((RadioButton)commissionControl.getChildAt(1)).setChecked(true);
            } else if(commissionControlValue == 3) {
                ((RadioButton)commissionControl.getChildAt(2)).setChecked(true);
            }

            commissionPercent = intent.getExtras().getInt("commissionPercent");
            //输入酬金百分比的对话框
            btnPercent = (RadioButton) findViewById(R.id.btnPercent);
            btnPercent.setText("分销伙伴拿 " + commissionPercent + "% 酬金");
            btnPercent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.percent_dialog, (ViewGroup) findViewById(R.id.dialog));
                    ((EditText) layout.findViewById(R.id.etname)).setText(commissionPercent + "");
                    new AlertDialog.Builder(CreatPartnerActivity.this)
                            .setTitle("酬金百分比")
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String percentstr = ((EditText) layout.findViewById(R.id.etname)).getText().toString();
                                    int percentvalue = 0;
                                    try {
                                        percentvalue = Integer.valueOf(percentstr);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (percentvalue > 0 && percentvalue < 100) {
                                        commissionPercent = percentvalue;
                                        btnPercent.setText("分销伙伴拿 " + commissionPercent + "% 酬金");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "请输入请输入 1 ~ 99 之间的整数", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();

                }

            });

        }


    }



    public void doGetCode(View view) {
        if(!checkCode())return ;

        new BaseTask<String, String, Boolean>(mContext, "请稍后...") {

            @Override
            protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().partnerVerify(mContext, phoneNumber);
            }

            @Override
            protected void onSuccess(Boolean arg0) {
                Toast.makeText(mContext, "验证码已发送到您的手机", Toast.LENGTH_LONG)
                        .show();
                sendLimitTime = 30;
                btnSend.setEnabled(false);
                handler.postDelayed(runnable, 1000);
            }
        }.execute("");
    }

    public void nextStep(View view){
        if(!checkCode() )return;
        if(!checkNextStep())return;
        new BaseTask<String, String, JSONObject>(mContext, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                String type = "check";
                return CRApplication.getApp().checkPartnerSystem(mContext, phoneNumber, codeNumber,type,"");
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                ll_top.setVisibility(View.GONE);
                ll_bottom.setVisibility(View.VISIBLE);
                ll_power_update.setVisibility(View.GONE);
                boolean isMsg = jsonObject.optBoolean("isMsg"); //是否有有效信息
                boolean isDQ = jsonObject.optBoolean("isDQ"); //是否是电子渠道
                userName = jsonObject.optString("userName");//伙伴姓名
                channelId = jsonObject.optString("channelId");
                String channelName = jsonObject.optString("channelName");
                JSONArray list = null;
                list = jsonObject.optJSONArray("channelList");
                String channelName1 = "";
                try {
                    for(int i=0;i < list.length();i++){
                        channelList.add(list.getJSONObject(i));
                    }
                    if(list.length()>0){
                        channelName1 = list.getJSONObject(0).optString("name");
                        channelId = list.getJSONObject(0).optString("id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(isMsg){//有有效信息
                    name.setEnabled(false);
                    name.setText(userName);
                    if(isDQ){//电子渠道,可以选择渠道类别
                        bt_select.setClickable(true);
                        bt_select.setText(channelName1);
                    }else{
                        bt_select.setClickable(false);
                        bt_select.setText(channelName);
                    }
                }else{
                    name.setEnabled(true);
                    bt_select.setClickable(true);
                    bt_select.setText(channelName1);
                }




            }
        }.execute("");


    }

    private boolean checkCode(){
        phoneNumber = phone.getText().toString();
        if (phoneNumber.trim().length() == 0) {
            Toast.makeText(mContext, "请输入伙伴手机号", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (phoneNumber.trim().length() != 11) {
            Toast.makeText(mContext, "请正确输入伙伴手机号", Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }

    private boolean checkNextStep(){
        codeNumber = code.getText().toString();
        if (codeNumber.trim().length() == 0) {
            Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendLimitTime--;
            if (sendLimitTime > 0) {
                btnSend.setText("" + sendLimitTime + "秒后重试");
                handler.postDelayed(this, 1000);
            } else {
                btnSend.setText("获取验证码");
                btnSend.setEnabled(true);
            }
        }
    };

    public void doChoose(View view){
        List<String> list = new ArrayList<String>();
        for(JSONObject jsonObject : channelList){
            list.add(jsonObject.optString("name"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        new AlertDialog.Builder(mContext).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bt_select.setText(channelList.get(which).optString("name"));
                channelId = channelList.get(which).optString("id");

                dialog.dismiss();
            }
        }).show();
    }

    public void saveMsg(View view){
        final String partnerName = name.getText().toString();
        if(partnerName.trim().length()==0){
            Toast.makeText(mContext, "请输入伙伴姓名", Toast.LENGTH_SHORT).show();
            return ;
        }
        new BaseTask<String,String,Boolean>(mContext,"正在保存，请稍后..."){

            @Override
            protected Boolean doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                String commissionControlValue = "1";
                if(((RadioButton)commissionControl.getChildAt(1)).isChecked()) {
                    commissionControlValue = "2";
                } else if(((RadioButton)commissionControl.getChildAt(2)).isChecked()) {
                    commissionControlValue = "3";
                }
                return  CRApplication.getApp().savePartnerMsg(mContext,phoneNumber,partnerName,channelId,requestType,commissionControlValue, commissionPercent);
            }

            @Override
            protected void onSuccess(Boolean aBoolean) {
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }.execute("");
    }

    public void doDel(View v) {

        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("您是否确定要注销伙伴" + userName + "?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removePartner();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    private void removePartner() {

        new BaseTask<String, String, Boolean>(this, "请稍后...") {

            @Override
            protected Boolean doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().removePartner(getApplicationContext(), userId);
            }

            @Override
            protected void onSuccess(Boolean result) {
                setResult(Activity.RESULT_OK);
                finish();
            }

        }.execute("");
    }
    public void doBack(View v) {
        onBackPressed();
    }
}
