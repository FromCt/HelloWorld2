package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONObject;

import java.io.IOException;

public class OrderResultActivity extends Activity {
	String orderId,goodsId, orderName, payValue, originalPrice, buyNumber, discountPrice,templateId,payType;
	boolean needPay, hasDiscount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderresult);

		orderId = getIntent().getExtras().getString("orderId");
        goodsId = getIntent().getExtras().getString("goodsId");
		orderName = getIntent().getExtras().getString("orderName");
		needPay = getIntent().getExtras().getBoolean("needPay");
		payValue = getIntent().getExtras().getString("payValue");
        originalPrice = getIntent().getExtras().getString("originalPrice");
        buyNumber = getIntent().getExtras().getString("buyNumber");
        hasDiscount = getIntent().getExtras().getBoolean("hasDiscount");
        discountPrice = getIntent().getExtras().getString("discountPrice");
        templateId = getIntent().getExtras().getString("templateId");
		payType = getIntent().getExtras().getString("payType");
		updatePayStatus();
	}
	
	private void updatePayStatus() {
		TextView tv_message = (TextView) findViewById(R.id.tv_message);
		String payText = "\n\n需支付： ¥";
		//payType  //支付方式  1代表在线支付 ，offLine代表线下支付
		//终端模版线下支付
		if(templateId.equals(ProductDetailTerminal.templateId) && payType.equals("offLine"))
		{
			payText = "\n\n线下支付金额： ¥";
		}

        if(!hasDiscount) {
            String msg = "订单号： " + orderId +
                    "\n\n商    品： " + orderName +
                    "\n\n价    格： ¥" + originalPrice +
                    "\n\n数    量： " + buyNumber +
					payText + payValue;
            tv_message.setText(msg);
        } else {

            String msg1 = "订单号： " + orderId +
                    "\n\n商    品： " + orderName;
            String msg2 = "\n\n原    价： ¥" + originalPrice;
            String msg3 = "\n\n优惠价： ¥" + discountPrice +
                    "\n\n数    量： " + buyNumber +
					payText + payValue;
            SpannableString ss = new SpannableString( msg1 + msg2 + msg3);
            ss.setSpan(new StrikethroughSpan(), msg1.length(), msg1.length()+msg2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_message.setText(ss);
        }
		
		if(needPay) {
			//终端模版线下支付
			if(templateId.equals(ProductDetailTerminal.templateId) && payType.equals("offLine"))
			{
				findViewById(R.id.btnPay).setVisibility(View.GONE);
				findViewById(R.id.btnCheckResult).setVisibility(View.GONE);
				findViewById(R.id.btnClose).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.btnPay).setVisibility(View.VISIBLE);
				findViewById(R.id.btnCheckResult).setVisibility(View.VISIBLE);
				findViewById(R.id.btnClose).setVisibility(View.GONE);
			}
		} else {
			findViewById(R.id.btnPay).setVisibility(View.GONE);
			findViewById(R.id.btnCheckResult).setVisibility(View.GONE);
			findViewById(R.id.btnClose).setVisibility(View.VISIBLE);
		}
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doClearTop(View v) {
		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}
	
	@Override
	public void onBackPressed() {

		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("关闭订单页面?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		alertDialog.show();
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
	
	public void doCheckResult(View v) {
		checkResult();
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
					//Toast.makeText(getApplicationContext(), "支付成功。", Toast.LENGTH_SHORT).show();
					if(templateId.equals(ProductDetailRechargeableCard.templateId)){//充值卡直接展示卡号
                        Intent intent = new Intent(OrderResultActivity.this,RechargeableCardShow.class);
                        intent.putExtra("goodsId",goodsId);
                        intent.putExtra("orderId",orderId);
                        intent.putExtra("payValue",payValue);
                        startActivity(intent);
                    }else{
                        needPay = false;
                        updatePayStatus();
                    }
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
