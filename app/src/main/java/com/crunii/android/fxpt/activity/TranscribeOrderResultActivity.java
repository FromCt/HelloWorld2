package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by wyp on 2015/7/14.
 */
public class TranscribeOrderResultActivity extends Activity{

    private String orderId = "" ;
    private Boolean needPay = false;
    private String payValue = "0.00";
    Button btnPay,btnCheckResult,btnClose;
    TextView tv_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transcribe_order_result);

        btnPay = (Button) findViewById(R.id.btnPay);
        btnCheckResult = (Button) findViewById(R.id.btnCheckResult);
        btnClose = (Button) findViewById(R.id.btnClose);
        tv_message = (TextView) findViewById(R.id.tv_message);

        orderId = getIntent().getExtras().getString("orderId");
        needPay = getIntent().getExtras().getBoolean("needPay");
        payValue = getIntent().getExtras().getString("payValue");

        show();
    }

    private void show() {
        if(needPay){
            String msg = "订单号： " + orderId +
                    "\n\n需支付： ¥" + payValue;
            tv_message.setText(msg);
            btnPay.setVisibility(View.VISIBLE);
            btnCheckResult.setVisibility(View.VISIBLE);
        }else{
            String msg = "订单号： " + orderId;
            tv_message.setText(msg);
            btnPay.setVisibility(View.GONE);
            btnCheckResult.setVisibility(View.GONE);
        }
    }


    public void doPay(View v) {
        Intent i = new Intent(this, WebPayActivity.class);
        i.putExtra("orderId", orderId);
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                //支付完成，检查支付结果
                checkResult();
            }
        }
    }
    public void doCheckResult(View view) {
        checkResult();
    }

    public void doClearTop(View view) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void checkResult() {

        new BaseTask<String, String, JSONObject>(this) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                this.loadMask = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected void onSuccess(JSONObject result) {
                if(result.optBoolean("needPay") == false) {
                    needPay = false;
                    show();
                } else {
                    Toast.makeText(getApplicationContext(), "尚未支付。如果您已经付款，请稍后再查看支付结果。", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onError() {
                super.onError();
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                this.loadMask.dismiss();
            }

            @Override
            protected JSONObject doInBack(String... params) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().payStatus(getApplicationContext(), orderId);
            }

        }.execute("");
    }


}
