package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartnerActivity extends Activity {
	String mode;
	JSONObject json = new JSONObject();
	
	Button btnSend;
	int sendLimitTime = 0;
	
	EditText name, phone, citizenId, bank, bankAccount, code;
	RadioGroup profitView, commissionControl;
	LinearLayout commissionMode;
	
	RadioButton btnPercent;
	int commissionPercent;
	
	JSONArray commissionModeList = new JSONArray();

	List<Item> channelList = new ArrayList<Item>();
	Item channel = new Item("0", "null");
	List<Item> companyList = new ArrayList<Item>();
	Item company = new Item("0", "null");
	List<Item> stationList = new ArrayList<Item>(); //所有支局的列表
	List<Item> stationList2 = new ArrayList<Item>(); //属于某个分公司的支局列表
	Item station = new Item("0", "null");
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_partner);
				
		name = (EditText) findViewById(R.id.name);
		phone = (EditText) findViewById(R.id.phone);
		citizenId = (EditText) findViewById(R.id.citizenId);
		bank = (EditText) findViewById(R.id.bank);
		bankAccount = (EditText) findViewById(R.id.bankAccount);
		code = (EditText) findViewById(R.id.code);
		
		profitView = (RadioGroup) findViewById(R.id.profitView);
		commissionControl = (RadioGroup) findViewById(R.id.commissionControl);
		commissionMode = (LinearLayout) findViewById(R.id.commissionMode);

		JSONArray channelArray = new JSONArray();
		JSONArray companyArray = new JSONArray();
		JSONArray stationArray = new JSONArray();
		try {
			commissionModeList = new JSONArray(getIntent().getExtras().getString("commissionModeList"));
			channelArray = new JSONArray(getIntent().getExtras().getString("channelList"));
			companyArray = new JSONArray(getIntent().getExtras().getString("companyList"));
			stationArray = new JSONArray(getIntent().getExtras().getString("stationList"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		//初始化酬金模式列表
		for(int i=0; i<commissionModeList.length(); i++) {
            JSONObject mode = commissionModeList.optJSONObject(i);
            String name = mode.optString("name");
            String id = mode.optString("id");
            if(id.equals("2")) {
                name = "商家一次性支付";
            }
			CheckBox cb = new CheckBox(this);
			cb.setText(name);
			cb.setTextColor(0xFF333333);
			cb.setTextSize(14);
			cb.setTag(id);
			commissionMode.addView(cb);
		}

		//初始化渠道列表
		for(int i=0; i<channelArray.length(); i++) {
			JSONObject json = channelArray.optJSONObject(i);
			channelList.add(new Item(json.optString("id"), json.optString("name")));
		}
		findViewById(R.id.channel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showChannelList();
			}
		});
		//初始化分公司列表
		for(int i=0; i<companyArray.length(); i++) {
			JSONObject json = companyArray.optJSONObject(i);
			companyList.add(new Item(json.optString("id"), json.optString("name")));
		}
		findViewById(R.id.company).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showCompanyList();
			}
		});
		//初始化支局列表
		for(int i=0; i<stationArray.length(); i++) {
			JSONObject json = stationArray.optJSONObject(i);
			stationList.add(new Item(json.optString("id"), json.optString("name"), json.optString("upperId")));
		}
		findViewById(R.id.station).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showStationList();
			}
		});
		
		//验证码按钮
		btnSend = (Button) findViewById(R.id.btn_yzm);
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(phone.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "请输入伙伴电话", Toast.LENGTH_LONG).show();
				} else {
					doSend();
				}
			}
		});
		
		
		String jsonString = getIntent().getExtras().getString("json");
		if(jsonString != null) {
			mode = "update";
			((TextView) findViewById(R.id.title)).setText("管理伙伴");
			try {
				json = new JSONObject(jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//编辑模式，隐藏验证码
			findViewById(R.id.codeview).setVisibility(View.GONE);
			
			name.setText(json.optString("name"));
			phone.setText(json.optString("phone"));
			citizenId.setText(json.optString("citizenId"));
			bank.setText(json.optString("bank"));
			bankAccount.setText(json.optString("bankAccount"));
			name.setEnabled(false);
			phone.setEnabled(false);
			citizenId.setEnabled(false);
			bank.setEnabled(false);
			bankAccount.setEnabled(false);
			
			//设置渠道
			String channelId = json.optString("channelId");
			for(int i=0; i<channelList.size(); i++) {
				if(channelList.get(i).getId().equals(channelId)) {
					channel = new Item(channelList.get(i).getId(), channelList.get(i).getName());
					break;
				}
			}
			((Button)findViewById(R.id.channel)).setText(channel.getName());
			
			//设置分公司
			String companyId = json.optString("companyId");
			for(int i=0; i<companyList.size(); i++) {
				if(companyList.get(i).getId().equals(companyId)) {
					company = new Item(companyList.get(i).getId(), companyList.get(i).getName());
					break;
				}
			}
			((Button)findViewById(R.id.company)).setText(company.getName());
			
			//设置支局
			String stationId = json.optString("stationId");
			for(int i=0; i<stationList.size(); i++) {
				if(stationList.get(i).getId().equals(stationId)) {
					station = new Item(stationList.get(i).getId(), stationList.get(i).getName());
					break;
				}
			}
			((Button)findViewById(R.id.station)).setText(station.getName());
			//刷新支局列表
			buildStationList2();
			
			//设置收益查看权限
			if(json.optBoolean("profitView")) {
				((RadioButton) profitView.getChildAt(0)).setChecked(true);
			} else {
				((RadioButton) profitView.getChildAt(1)).setChecked(true);
			}
			
			//设置酬金模式
			String commisionModeString = json.optString("commissionMode");
			for(int i=0; i<commissionMode.getChildCount(); i++) {
				CheckBox cb = (CheckBox) commissionMode.getChildAt(i);
				String id = (String) cb.getTag();
				//TODO
				//鉴于酬金模式的总数几乎不可能大于10，其id值也就在1-9之间没有重复，为了简单起见，采用如下方法判断
				if(commisionModeString.contains(id)) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
			}
			
			//设置酬金分配
			int commissionControlValue = json.optInt("commissionControl");
			if(commissionControlValue == 1) {
				((RadioButton)commissionControl.getChildAt(0)).setChecked(true);
			} else if(commissionControlValue == 2) {
				((RadioButton)commissionControl.getChildAt(1)).setChecked(true);
			} else if(commissionControlValue == 3) {
				((RadioButton)commissionControl.getChildAt(2)).setChecked(true);
			}
			
			commissionPercent = json.optInt("commissionPercent");
			
		} else {
			mode = "new";
			((TextView) findViewById(R.id.title)).setText("新增伙伴");
			//新增模式，隐藏删除按钮
			findViewById(R.id.del).setVisibility(View.GONE);
			//新增模式，隐藏开户银行和开户行账号
			findViewById(R.id.bankView).setVisibility(View.GONE);
			findViewById(R.id.bankView2).setVisibility(View.GONE);
			findViewById(R.id.bankAccountView).setVisibility(View.GONE);
			findViewById(R.id.bankAccountView2).setVisibility(View.GONE);

			//设置渠道为默认第一个
			//channel = new Item(channelList.get(0).getId(), channelList.get(0).getName());
			//((Button)findViewById(R.id.channel)).setText(channel.getName());
			//渠道默认不选择，必须强迫用户选择
			((Button)findViewById(R.id.channel)).setText("<请选择>");
			
			//设置分公司为默认第一个
			company = new Item(companyList.get(0).getId(), companyList.get(0).getName());
			((Button)findViewById(R.id.company)).setText(company.getName());
			
			//设置支局为默认第一个
			station = new Item(stationList.get(0).getId(), stationList.get(0).getName());
			((Button)findViewById(R.id.station)).setText(station.getName());
			//刷新支局列表
			buildStationList2();
			
			//默认收益查看权限为允许查看
			((RadioButton)profitView.getChildAt(0)).setChecked(true); 

			//默认酬金模式为全选
			for(int i=0; i<commissionMode.getChildCount(); i++) {
				CheckBox cb = (CheckBox) commissionMode.getChildAt(i);
				cb.setChecked(true);
			}
			
			//默认酬金分配为分销伙伴拿全额酬金
			((RadioButton)commissionControl.getChildAt(0)).setChecked(true); 
			
			//默认酬金百分比为 50%
			commissionPercent = 50; 
		}

		//输入酬金百分比的对话框
		btnPercent = (RadioButton) findViewById(R.id.btnPercent);
		btnPercent.setText("分销伙伴拿 " + commissionPercent + "% 酬金");
		btnPercent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.percent_dialog, (ViewGroup) findViewById(R.id.dialog));
				((EditText)layout.findViewById(R.id.etname)).setText(commissionPercent + "");
				new AlertDialog.Builder(PartnerActivity.this)
						.setTitle("酬金百分比")
						.setView(layout)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String percentstr = ((EditText)layout.findViewById(R.id.etname)).getText().toString();
								int percentvalue = 0;
								try {
									percentvalue = Integer.valueOf(percentstr);
								} catch (Exception e){
									e.printStackTrace();
								}
								if(percentvalue > 0 && percentvalue < 100) {
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

	private void showChannelList() {
		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, channelList);
		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				channel = channelList.get(position);
				((Button)findViewById(R.id.channel)).setText(channel.getName());
				dlg.dismiss();
			}
		}).show();
	}

	private void showCompanyList() {
		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, companyList);
		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				company = companyList.get(position);
				((Button)findViewById(R.id.company)).setText(company.getName());
				
				//刷新支局列表
				buildStationList2();
				
				station = stationList2.get(0);
				((Button)findViewById(R.id.station)).setText(station.getName());

				dlg.dismiss();
			}
		}).show();
	}
	
	private void buildStationList2() {
		stationList2.clear();
		for(int i=0; i<stationList.size(); i++) {
			if(stationList.get(i).getUpperId().equals(company.getId())) {
				stationList2.add(stationList.get(i));
			}
		}
	}

	private void showStationList() {
		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, stationList2);
		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				station = stationList2.get(position);
				((Button)findViewById(R.id.station)).setText(station.getName());
				dlg.dismiss();
			}
		}).show();
	}
	
	public void doBack(View v) {
		onBackPressed();
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
	
	private void doSend()  {

		new BaseTask<String, String, Boolean>(this, "请稍后...") {

			@Override
			protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().partnerVerify(PartnerActivity.this, phone.getText().toString());
			}

			@Override
			protected void onSuccess(Boolean arg0) {
				Toast.makeText(PartnerActivity.this, "验证码已发送到分销伙伴的手机", Toast.LENGTH_LONG).show();

				sendLimitTime = 30;
				btnSend.setEnabled(false);
				handler.postDelayed(runnable, 1000);
			}
		}.execute("");

	}
	public void doDel(View v) {

		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("您是否确定要注销伙伴" + json.optString("name") + "?")
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
				return CRApplication.getApp().removePartner(getApplicationContext(), json.optString("id"));
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
			Toast.makeText(this, "请输入伙伴姓名", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NullUtils.isEmpty(phone.getText().toString())) {
			Toast.makeText(this, "请输入伙伴电话", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NullUtils.isEmpty(citizenId.getText().toString())) {
			Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(channel.getName().equals("null")) {
			Toast.makeText(this, "请选择渠道类别", Toast.LENGTH_SHORT).show();
			return;
		}
		

		new BaseTask<String, String, Boolean>(this, "请稍后...") {

			@Override
			protected Boolean doInBack(String... params) throws HttpException,
					IOException, TaskResultException {

				String profitViewValue = "1";
				if(((RadioButton)profitView.getChildAt(1)).isChecked()) {
					profitViewValue = "0";
				}
				
				String commissionModeValue = "";
				for(int i=0; i<commissionMode.getChildCount(); i++) {
					CheckBox cb = (CheckBox) commissionMode.getChildAt(i);
					if(cb.isChecked()) {
						String id = (String) cb.getTag();
						commissionModeValue += "-" + id;
					}
				}
				
				String commissionControlValue = "1";
				if(((RadioButton)commissionControl.getChildAt(1)).isChecked()) {
					commissionControlValue = "2";
				} else if(((RadioButton)commissionControl.getChildAt(2)).isChecked()) {
					commissionControlValue = "3";
				}
				
				if(mode.equals("new")) {
					return CRApplication.getApp().addPartner(getApplicationContext(),
							code.getText().toString(), name.getText().toString(), phone.getText().toString(), 
							citizenId.getText().toString(), channel.getId(), company.getId(), station.getId(), 
							profitViewValue, commissionModeValue, commissionControlValue, commissionPercent+"");
				} else {
					return CRApplication.getApp().modifyPartner(getApplicationContext(), json.optString("id"),
							channel.getId(), company.getId(), station.getId(), 
							profitViewValue, commissionModeValue, commissionControlValue, commissionPercent+"");
				}
			}

			@Override
			protected void onSuccess(Boolean result) {
				Toast.makeText(PartnerActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				setResult(Activity.RESULT_OK);
				finish();
			}

		}.execute("");
	}
	
}
