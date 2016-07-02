package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.task.DownloadTask;
import com.crunii.android.base.util.DownloadCallback;
import com.crunii.android.base.util.DownloadCallback.Callback;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.PersonChoose;
import com.crunii.android.fxpt.business.Version;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyLog;
import com.crunii.android.fxpt.util.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class LoginActivity extends Activity implements Callback {

	Version serverVersion;

	Button btnSend;
	int sendLimitTime = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnSend = (Button) findViewById(R.id.btn_yzm);
		checkNewVersion();
	}
	
	public void doSend(View v)  {
		final EditText username;
		username = (EditText) findViewById(R.id.ed_username);
		if (username.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_LONG).show();
			return;
		}

		new BaseTask<String, String, Boolean>(this, "请稍后...") {

			@Override
			protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().getCode(getApplicationContext(), arg0[0]);
			}

			@Override
			protected void onSuccess(Boolean arg0) {
				Toast.makeText(getApplicationContext(), "验证码已发送到" + username.getText().toString(), Toast.LENGTH_LONG)
						.show();

				sendLimitTime = 30;
				btnSend.setEnabled(false);
				handler.postDelayed(runnable, 1000);
			}
		}.execute(username.getText().toString());

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
	
	public void doLogin(View v) {

		EditText username, password;
		username = (EditText) findViewById(R.id.ed_username);
		password = (EditText) findViewById(R.id.ed_password);

		new BaseTask<String, String, JSONObject>(this, "登录中...") {

			String userName="";

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
				userName=arg0[0];
				return CRApplication.getApp().login(getApplicationContext(), arg0[0], arg0[1]);
			}

			@Override
			protected void onSuccess(JSONObject jsonObject) {

				try {
					boolean b=jsonObject.getBoolean("success");
					JSONObject record = jsonObject.getJSONObject("record");
					MyLog.i("ct", "LoginActivity record=" + record.toString());
					if(b){

						boolean value=record.getBoolean("selectValue");
						Intent intent = new Intent();
						if(value) {//当有选择权限时候
							MyLog.i("ct", "LoginActivity 传递值到");
							intent.putExtra("selectValue", true);
							JSONArray array = record.getJSONArray("list");
							ArrayList<PersonChoose> list  = new ArrayList<PersonChoose>();
							PersonChoose ps;
							for (int i=0;i<array.length();i++) {
								JSONObject jsonObject1= (JSONObject) array.get(i);
								MyLog.i("ct", "LoginActivity jsonObject1=" + jsonObject1.toString());
								ps = new PersonChoose();
								ps.setName(jsonObject1.getString("name"));
								ps.setBranch(jsonObject1.getString("branch"));
								ps.setCategory(jsonObject1.getString("category"));
								ps.setCrm(jsonObject1.getString("crm"));
								ps.setSale_point(jsonObject1.getString("sale_point"));
								ps.setAttribute(jsonObject1.getString("attribute"));
								ps.setMaster(jsonObject1.getString("master"));
								ps.setId(jsonObject1.getString("id"));
								list.add(ps);
							}
							MyLog.i("ct", "LoginActivity list.size=" + list.size());
							//intent.putParcelableArrayListExtra("list", list);
							Intent intent1 = new Intent(LoginActivity.this, LoginChooseActivity.class);
							intent1.putParcelableArrayListExtra("list", list);
							startActivity(intent1);

						}else{
							CRApplication.setId(getApplicationContext(), record.getString("userId"));
							CRApplication.setName(getApplicationContext(), record.getString("userName"));
							CRApplication.setToken(getApplicationContext(), record.getString("token"));

						}
						Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();
						CRApplication.setPhone(getApplicationContext(), userName);
						setResult(RESULT_OK);
						Constant.loginTimes+=1;
						finish();

					}else {
						MyToast.show("登录失败，请重新登录...", LoginActivity.this);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}



			}
		}.execute(username.getText().toString(), password.getText().toString());

	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private void checkNewVersion() {
		new BaseTask<String, String, Version>(this) {
			//private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				//this.loadMask = ProgressDialog.show(context, null, "checking new version...");
			}

			@Override
			protected void onSuccess(Version result) {
				serverVersion = result;
				onNewVersionArrived();
			}

			@Override
			protected void onError() {
			}
			
			@Override
			protected void onPostExecute(Version result) {
				super.onPostExecute(result);
				//this.loadMask.dismiss();
			}

			@Override
			protected Version doInBack(String... params)
					throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().checkNewVersion(LoginActivity.this);
			}
		}.execute("");
	}
	

	private void onNewVersionArrived() {
		int currentVersionCode = 0;
		try {
			currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if(currentVersionCode < serverVersion.getNumber()) {
			new AlertDialog.Builder(this)
					.setTitle("版本更新:" + serverVersion.getName())
					.setMessage(serverVersion.getDesc())
					.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadNewVersion();
						}
					})
					.setCancelable(serverVersion.isForce()).show();
		}
	}
	
	private void downloadNewVersion() {
		String url = serverVersion.getUrl();
		String path = Environment.getExternalStorageDirectory().getPath() + "/fxpt.apk";
        new DownloadTask(this, CRApplication.getApp().getHttpClient()).execute(new DownloadCallback(url, path, this));
	}

	@Override
	public void doCallback(String savePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + savePath), "application/vnd.android.package-archive");
		startActivity(intent);
	}

}
