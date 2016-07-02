package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.CitizenUtil;

import java.io.IOException;

public class SubmitCardInfoActivity extends Activity {
	EditText number, puk, name, citizenId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submitcardinfo);

		number = (EditText) findViewById(R.id.number);
		number.setHint("请输入11位手机号码");

		puk = (EditText) findViewById(R.id.puk);
		puk.setHint("请输入8位PUK码");

		name = (EditText) findViewById(R.id.name);
		citizenId = (EditText) findViewById(R.id.citizenId);

	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doSubmit(View v) {
		if (number.getText().toString().length() != 11) {
			Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
		} else if (puk.getText().toString().length() != 8) {
			Toast.makeText(this, "请输入8位PUK码", Toast.LENGTH_SHORT).show();
		} else if (!CitizenUtil.verifyName(name.getText().toString())) {
			Toast.makeText(this, "客户姓名必须是中文", Toast.LENGTH_SHORT).show();
		} else if (!CitizenUtil.verifyCitizenId(citizenId.getText().toString())) {
			Toast.makeText(this, "身份证号码无效", Toast.LENGTH_SHORT).show();
		} else {
			submit();
		}
	}

	private void submit() {

		new BaseTask<String, String, Boolean>(this) {
			private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				this.loadMask = ProgressDialog.show(context, null, "请稍候...");
			}

			@Override
			protected void onSuccess(Boolean result) {

				Toast.makeText(getApplicationContext(), "提交成功。",
						Toast.LENGTH_SHORT).show();

				setResult(Activity.RESULT_OK);
				finish();
			}

			@Override
			protected void onError() {
				super.onError();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				this.loadMask.dismiss();
			}

			@Override
			protected Boolean doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().submitcardinfo(
						getApplicationContext(), number.getText().toString(),
						puk.getText().toString(), name.getText().toString(),
						citizenId.getText().toString());
			}

		}.execute("");
	}


}
