package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.Item;
import com.crunii.android.fxpt.business.TibmGoodsPlan;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VerifyNumberActivity extends Activity {

	final static String TAG = "VerifyNumberActivity";
	String type;
	Button btnType;
	EditText number, name, balance, packageName, packageTime, accountTime;
	TextView tv_yucun;
	String address, bestContact;
	String displayName = "";
	String displayCitizenId = "";
    String displayCitizenAddress = "";
	String verifiedName = "";
	String verifiedCitizenId = "";
    String verifiedCitizenAddress = "";
	String typeId = "";
	String numberT = "";
	TibmGoodsPlan tibmGoodsPlan ;

	List<Item> typeList = new ArrayList<Item>();
	Item typeItem = new Item("fixedline", "固话");

	LinearLayout ll_name,ll_balance,ll_packageName,ll_packageTime,ll_accountTime;
	RelativeLayout ll_yucun;
	Map<String,Object> contractAccount ;
	boolean isContractAccount = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verifynumber);

		ll_name = (LinearLayout)findViewById(R.id.ll_name);
		ll_balance = (LinearLayout)findViewById(R.id.ll_balance);
		ll_packageName = (LinearLayout)findViewById(R.id.ll_packageName);
		ll_packageTime = (LinearLayout)findViewById(R.id.ll_packageTime);
		ll_accountTime = (LinearLayout)findViewById(R.id.ll_accountTime);
		ll_yucun = (RelativeLayout)findViewById(R.id.ll_yucun);


		isContractAccount = getIntent().getExtras().getBoolean("isContractAccount");
		tibmGoodsPlan =(TibmGoodsPlan) getIntent().getExtras().getSerializable("contractAccount");
		type = getIntent().getExtras().getString("type");
		address = getIntent().getExtras().getString("address");
		typeId = getIntent().getExtras().getString("typeId");
		numberT = getIntent().getExtras().getString("number");

		btnType = (Button)findViewById(R.id.type);
		number = (EditText) findViewById(R.id.number);
		name = (EditText) findViewById(R.id.name);
		balance = (EditText) findViewById(R.id.balance);
		packageName = (EditText) findViewById(R.id.packageName);
		packageTime = (EditText) findViewById(R.id.packageTime);
		accountTime = (EditText) findViewById(R.id.accountTime);
		tv_yucun = (TextView)findViewById(R.id.tv_yucun);

		number.setText(numberT);

		if(type.equals("fixedline")) {
			btnType.setText("固话");
			typeItem = new Item("fixedline", "固话");
			typeList.add(typeItem);
		} else if(type.equals("broadband")) {
			btnType.setText("宽带");
			typeItem = new Item("broadband", "宽带");
			typeList.add(typeItem);
		} else if(type.equals("phone")) {
			btnType.setText("手机");
			typeItem = new Item("phone", "手机");
			typeList.add(typeItem);
		} else if(type.equals("contractmode")) {
			btnType.setText("固话");
			typeItem = new Item("fixedline", "固话");
			typeList.add(new Item("fixedline", "固话"));
			typeList.add(new Item("broadband", "宽带"));
			//20150123 合约机加装验证号码取消验证手机
			//typeList.add(new Item("phone", "手机"));
		}else if(type.equals("contractAccount")){
			btnType.setText("固话");
			typeItem = new Item("fixedline", "固话");
			typeList.add(new Item("fixedline", "固话"));
			typeList.add(new Item("broadband", "宽带"));
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		if(isContractAccount) {//两条线走的，有营销方案的商品调营销方案，无营销方案的商品保持原来的不变。
			ll_name.setVisibility(View.GONE);        //用户姓名
			ll_balance.setVisibility(View.GONE);     // 账户余额
			ll_packageName.setVisibility(View.GONE); // 套餐名称
			ll_packageTime.setVisibility(View.GONE); //套餐生效时间
			ll_accountTime.setVisibility(View.GONE); // 入网时间
			ll_yucun.setVisibility(View.GONE);
			if (typeId.equals(tibmGoodsPlan.YZHM_JZ)) {
				setVisibleView(ll_name, tibmGoodsPlan.getJz().getJz_cust_name());
				setVisibleView(ll_balance, tibmGoodsPlan.getJz().getJz_blance());
				setVisibleView(ll_packageName, tibmGoodsPlan.getJz().getJz_combo_name());
				setVisibleView(ll_packageTime, tibmGoodsPlan.getJz().getJz_eff_time());
				setVisibleView(ll_accountTime, tibmGoodsPlan.getJz().getJz_net_time());
				setVisibleView(ll_yucun, tibmGoodsPlan.getJz().getJz_cal());
			} else if (typeId.equals(tibmGoodsPlan.YZHM_DB)) {
				setVisibleView(ll_name, tibmGoodsPlan.getDb().getDb_cust_name());
				setVisibleView(ll_balance, tibmGoodsPlan.getDb().getDb_blance());
				setVisibleView(ll_packageName, tibmGoodsPlan.getDb().getDb_combo_name());
				setVisibleView(ll_packageTime, tibmGoodsPlan.getDb().getDb_eff_time());
				setVisibleView(ll_accountTime, tibmGoodsPlan.getDb().getDb_net_time());
				setVisibleView(ll_yucun, tibmGoodsPlan.getDb().getDb_cal());
			} else if (typeId.equals(tibmGoodsPlan.YZHM_HZ)) {
				setVisibleView(ll_name, tibmGoodsPlan.getHz().getHz_cust_name());
				setVisibleView(ll_balance, tibmGoodsPlan.getHz().getHz_blance());
				setVisibleView(ll_packageName, tibmGoodsPlan.getHz().getHz_combo_name());
				setVisibleView(ll_packageTime, tibmGoodsPlan.getHz().getHz_eff_time());
				setVisibleView(ll_accountTime, tibmGoodsPlan.getHz().getHz_net_time());
				setVisibleView(ll_yucun, tibmGoodsPlan.getHz().getHz_cal());
			}
		}

	}

	private void setVisibleView(View view,int i){
		if(i == 1){
			view.setVisibility(View.VISIBLE);
		}else if(i == 0){
			view.setVisibility(View.GONE);
		}
	}

	private String yuncunShow(String time,int days){
		if(time != null && !time.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date nowDate = new Date();
			Double numDays = 0.0;
			Date date;
			try {
				date = sdf.parse(time);
				numDays = Math.floor((nowDate.getTime() - date.getTime()) / 1000 / 60 / 60/24);
			} catch (ParseException e) {
				System.out.println("计算出错");
				e.printStackTrace();
			}
			if(numDays > days){
				tv_yucun.setTextColor(getResources().getColor(R.color.yucun_color));
				return "不需要预存";
			}else{
				return "需要预存";
			}
		}
		return "不需要预存";
	}

	private void  switchColor(String str){
		if(str.equals("不需要预存")){
			tv_yucun.setTextColor(getResources().getColor(R.color.yucun_color));
		}
	}


	public void showTypeList(View v) {

		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, typeList);

		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				typeItem = typeList.get(position);
				((Button)findViewById(R.id.type)).setText(typeItem.getName());
				dlg.dismiss();
			}
		}).show();
	}
	
	public void doBack(View v) {
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.putExtra("number", number.getText().toString());
		i.putExtra("address", address);
		i.putExtra("bestContact", bestContact);
		i.putExtra("type", typeItem.getId());
		i.putExtra("displayName", displayName);
		i.putExtra("displayCitizenId", displayCitizenId);
        i.putExtra("displayCitizenAddress", displayCitizenAddress);
		i.putExtra("verifiedName", verifiedName);
		i.putExtra("verifiedCitizenId", verifiedCitizenId);
        i.putExtra("verifiedCitizenAddress", verifiedCitizenAddress);
		setResult(RESULT_OK, i);
		finish();
	}
	
	public void doSubmit(View v) {
		if(number.getText().toString().equals("")) {
			Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
		} else{
			submit();
		}
	}

	private void submit() {

		new BaseTask<String, String, JSONObject>(this) {
			private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				this.loadMask = ProgressDialog.show(context, null, "请稍候...");
			}

			@Override
			protected void onSuccess(JSONObject result) {
				displayName = result.optString("name");
				displayCitizenId = result.optString("citizenId");
                displayCitizenAddress = result.optString("citizenAddress");
				verifiedName = result.optString("custName");
				verifiedCitizenId = result.optString("custId");
                verifiedCitizenAddress = result.optString("custAddress");
				address = result.optString("address");
				bestContact = result.optString("bestContact");
				
				name.setText(displayName); 
				balance.setText(result.optString("balance")); 
				packageName.setText(result.optString("packageName")); 
				packageTime.setText(result.optString("packageTime")); 
				accountTime.setText(result.optString("accountTime"));

				if(isContractAccount) {
					if (typeId.equals(tibmGoodsPlan.YZHM_JZ)) {
						String jzShow = yuncunShow(result.optString("accountTime"), tibmGoodsPlan.getJz().getJz_days());
						tv_yucun.setText(jzShow);
						switchColor(jzShow);
					} else if (typeId.equals(tibmGoodsPlan.YZHM_DB)) {
						String dbShow = yuncunShow(result.optString("accountTime"), tibmGoodsPlan.getDb().getDb_days());
						tv_yucun.setText(dbShow);
						switchColor(dbShow);
					} else if (typeId.equals(tibmGoodsPlan.YZHM_HZ)) {
						String hzShow = yuncunShow(result.optString("accountTime"), tibmGoodsPlan.getHz().getHz_days());
						tv_yucun.setText(hzShow);
						switchColor(hzShow);
					}
				}
				findViewById(R.id.btnOk).setVisibility(View.VISIBLE);
			}

			@Override
			protected void onError() {
				super.onError();
				
				findViewById(R.id.btnOk).setVisibility(View.GONE);
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);
				this.loadMask.dismiss();
			}

			@Override
			protected JSONObject doInBack(String... params) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().verifyNumber(getApplicationContext(), typeItem.getId(), number.getText().toString());
			}
			
		}.execute("");
	}

}
