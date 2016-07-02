package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.http.HttpClient;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.task.DownloadTask;
import com.crunii.android.base.util.DownloadCallback;
import com.crunii.android.base.util.DownloadCallback.Callback;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.Constant;

import org.json.JSONObject;

import java.io.IOException;

public class OtherActivity extends Activity implements Callback {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other);

	}


	//套餐简介
	public void doIntroduction(View v) {

		Log.i("OtherActivity", "套餐简介点击事件");
		//Toast.makeText(this, "套餐简介点击事件", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(this,ServiceIntroduction.class);
		startActivity(intent);

	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doLaunchMms(View v) {

		try {
			Intent i = new Intent();
			i.setClassName("com.crunii.android.mms", "com.crunii.android.mms.activity.LoginActivity");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		} catch (Exception e) {
			appDownloadConfirm();
		}
	}

	private void appDownloadConfirm() {

		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("爱运维尚未安装，是否下载?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadMms();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		alertDialog.show();
	}

	private void downloadMms() {
		new DownloadTask(this, new HttpClient())
				.execute(new DownloadCallback("http://222.177.4.208:7172/upload/mms-apk/mms-android-signed-mms-3.3.7.apk",
						Constant.DOWNLOAD_PATH + "mms-android.apk", OtherActivity.this));
	}

	@Override
	public void doCallback(String savePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + savePath), "application/vnd.android.package-archive");
		startActivity(intent);
	}

	public void doQrCode(View v) {
		Intent i = new Intent(this, ShareQrCodeActivity.class);
		startActivity(i);
	}

	public void doSms(View v) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		String message = CRApplication.getName(this) +
				"向您分享了“电商分销手机客户端（安卓版）”下载地址:  http://dwz.cn/yZnqf 。" +
				"电信手机发送4683至10001即可申请每月1G客户端本地定向流量免费优惠。";
		i.putExtra("sms_body", message);
		i.setType("vnd.android-dir/mms-sms");
		startActivity(i);
	}

	public void doDistribution(View view){
//		Intent i = new Intent(OtherActivity.this, DistributionChannelActivity.class);
//		startActivity(i);
		isToDistributor();
	}

	private void isToDistributor(){
		new BaseTask<String,String,JSONObject>(this,"正在校验权限，请稍后..."){

			@Override
			protected void onSuccess(JSONObject jsonObject) {
				if(jsonObject.optBoolean("isDo")){
					Intent i = new Intent(OtherActivity.this, DistributionChannelActivity.class);
					startActivity(i);
				}else{
					Toast.makeText(OtherActivity.this,jsonObject.optString("reasonDesc"),Toast.LENGTH_LONG).show();
				}
			}

			@Override
			protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().isToDistributor(OtherActivity.this);
			}
		}.execute("");
	}
}
