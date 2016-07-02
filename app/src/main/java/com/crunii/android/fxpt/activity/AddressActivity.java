package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddressActivity extends Activity {
	String mode;
	EditText name, addr, phone;
	CheckBox isDefault;


	String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		name = (EditText) findViewById(R.id.name);
		addr = (EditText) findViewById(R.id.addr);
		phone = (EditText) findViewById(R.id.phone);
		isDefault = (CheckBox) findViewById(R.id.isdefault);
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			mode = "update";
			try {
				JSONObject json = new JSONObject(b.getString("json"));
				name.setText(json.getString("name"));
				addr.setText(json.getString("addr"));
				phone.setText(json.getString("phone"));
				if(json.getString("isDefault").equals("1")) {
					isDefault.setChecked(true);
				} else {
					isDefault.setChecked(false);
				}
				id = json.getString("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			mode = "new";
			//新增模式，隐藏删除按钮
			findViewById(R.id.del).setVisibility(View.GONE);
		}
		
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doDel(View v) {

		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("是否删除收货地址?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delAddress();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();
		alertDialog.show();
	}
	
	private void delAddress() {

		new BaseTask<String, String, Boolean>(this, "请稍后...") {

			@Override
			protected Boolean doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().addressDel(getApplicationContext(), id);
			}

			@Override
			protected void onSuccess(Boolean result) {
				setResult(Activity.RESULT_OK);
				finish();
			}

		}.execute("");
	}

	public void doSave(View v) {

		if(NullUtils.isEmpty(name.getText().toString())) {
			Toast.makeText(this, "请输入收货人", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NullUtils.isEmpty(addr.getText().toString())) {
			Toast.makeText(this, "请输入收货地址", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NullUtils.isEmpty(phone.getText().toString())) {
			Toast.makeText(this, "请输入联系电话", Toast.LENGTH_SHORT).show();
			return;
		}
		

		new BaseTask<String, String, Boolean>(this, "请稍后...") {

			@Override
			protected Boolean doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
				
				if(mode.equals("new")) {
					return CRApplication.getApp().addressAdd(getApplicationContext(),
							name.getText().toString(), 
							addr.getText().toString(), 
							phone.getText().toString(),
							isDefault.isChecked());
				} else {
					return CRApplication.getApp().addressUpdate(getApplicationContext(),
							name.getText().toString(), 
							addr.getText().toString(), 
							phone.getText().toString(),
							isDefault.isChecked(),
							id);
				}
			}

			@Override
			protected void onSuccess(Boolean result) {
				Toast.makeText(AddressActivity.this, "收货地址已保存", Toast.LENGTH_SHORT).show();

				setResult(Activity.RESULT_OK);
				finish();
			}

		}.execute("");
	}
	
}
