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

public class CancelOrderActivity extends Activity {
	String orderId;
    boolean isBL ;
	EditText reason;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancelorder);
		
		orderId = getIntent().getExtras().getString("orderId");
        isBL = getIntent().getExtras().getBoolean("isBL");
		reason = (EditText) findViewById(R.id.reason);
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doSubmit(View v) {
		if(reason.getText().toString().equals("")) {
			Toast.makeText(this, "请输入取消原因", Toast.LENGTH_SHORT).show();
		} else {
            if(isBL){
                blOrderCancel();
            }else{
                orderCancel();
            }

		}
	}


	private void orderCancel() {

		new BaseTask<String, String, Boolean>(this) {
			private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				this.loadMask = ProgressDialog.show(context, null, "请稍候...");
			}

			@Override
			protected void onSuccess(Boolean result) {

				Toast.makeText(getApplicationContext(), "订单已撤销。", Toast.LENGTH_SHORT).show();

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
			protected Boolean doInBack(String... params) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().cancelOrder(getApplicationContext(), params[0], params[1]);
			}
			
		}.execute(orderId, reason.getText().toString());
	}

    private void blOrderCancel() {

        new BaseTask<String, String, Boolean>(this) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                this.loadMask = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected void onSuccess(Boolean result) {

                Toast.makeText(getApplicationContext(), "订单已撤销。", Toast.LENGTH_SHORT).show();

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
            protected Boolean doInBack(String... params) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().blOrderCancel(getApplicationContext(), params[0], params[1]);
            }

        }.execute(orderId, reason.getText().toString());
    }
	
}
