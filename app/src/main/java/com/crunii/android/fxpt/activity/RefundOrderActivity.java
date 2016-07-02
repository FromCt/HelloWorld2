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

import java.io.IOException;

public class RefundOrderActivity extends Activity {
	String orderId;
	EditText reason;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refundorder);
		
		orderId = getIntent().getExtras().getString("orderId");
		reason = (EditText) findViewById(R.id.reason);
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doSubmit(View v) {
		if(reason.getText().toString().equals("")) {
			Toast.makeText(this, "请输入退款原因", Toast.LENGTH_SHORT).show();
		} else {
			orderRefund();
		}
	}


	private void orderRefund() {

		new BaseTask<String, String, Boolean>(this) {
			private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				this.loadMask = ProgressDialog.show(context, null, "请稍候...");
			}

			@Override
			protected void onSuccess(Boolean result) {

				Toast.makeText(getApplicationContext(), "退款申请已提交。", Toast.LENGTH_SHORT).show();

				setResult(Activity.RESULT_OK);
				finish();;
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
			protected Boolean doInBack(String... params) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().refundOrder(getApplicationContext(), params[0], params[1]);
			}
			
		}.execute(orderId, reason.getText().toString());
	}
	
}
