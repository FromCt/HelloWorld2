package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.ViewEvent;
import com.crunii.android.fxpt.base.ViewEventType;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyToast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ct on 15/11/18.
 * 网店管理查看二维码
 */
public class shopManagementSetMoneyActivity extends BaseActivity {

    public EditText edit_money;
    public int maxMoney;
    public String packageID;
    public String id;
    private String chooseValue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_management_set_money);
       TextView tishiTextView = (TextView) findViewById(R.id.tishiTextview);
        TextView textView = (TextView) findViewById(R.id.head_tv_user);
        textView.setText(CRApplication.getName(shopManagementSetMoneyActivity.this));
        Intent intent=getIntent();
        packageID=intent.getStringExtra("packageId");
        String maxMoney1=intent.getStringExtra("maxMoney");
        maxMoney = Integer.parseInt(maxMoney1);
        id = intent.getStringExtra("id");
        chooseValue2=intent.getStringExtra("chooseValue2");

        TextView setMoneyTextView = (TextView) findViewById(R.id.smsm_money);
        setMoneyTextView.setText("0-"+maxMoney+"元");
        edit_money = (EditText) findViewById(R.id.smsm_setMoney);
        if (chooseValue2.equals("1002")){
          tishiTextView.setText("       优惠金额从您的坐扣返利中扣减, 即坐扣返利的实际金额=“坐扣返利”-“优惠金额”");
        }else {
            tishiTextView.setText("      优惠金额从您的终端利润中扣减，即终端返利的实际金额=“终端返利”-“优惠金额”。");
        }

    }



    @ViewEvent(id = R.id.smsm_store, eventType = {ViewEventType.CLICK})
    public void setMoney(View v) {//设置钱后保存

        String money1=edit_money.getText().toString().trim();
        int money=Integer.parseInt(money1);
        if(money>maxMoney){//当设置金额大于最大金额时。报错
            MyToast.show("设置金额大于最大优惠金额", shopManagementSetMoneyActivity.this);
        }else{
            HashMap<String, String> postparams = new HashMap<String, String>();
            postparams.put("packageId",packageID);
            postparams.put("id", id);
            postparams.put("money", money1);
            sendPost(Constant.URL.SHOPMANAGEMENTSETMONEY, postparams, new HttpPostProp() {
                @Override
                public void dealRecord(Map record) {
                    boolean success = (boolean) record.get("success");
                    if (success){
                        showMessage("设置成功");
                        finish();
                    }else {
                        showMessage("设置失败");
                    }

                }
            });
        }
    }


    @ViewEvent(id = R.id.smsm_cancel, eventType = {ViewEventType.CLICK})
    public void cancel(View v) {//取消保存
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }




}
