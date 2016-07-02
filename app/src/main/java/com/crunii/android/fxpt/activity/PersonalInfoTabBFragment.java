package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class PersonalInfoTabBFragment extends Fragment {

	Button btnSend;
	int sendLimitTime = 0;
	
	List<Item> bankList = new ArrayList<Item>();
	Item bank = new Item("0", "中国工商银行");
    private boolean isTelecom;

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
 	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view =  inflater.inflate(R.layout.personalinfo_tab_b, container, false);
		
		btnSend = (Button) view.findViewById(R.id.btn_yzm);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doSend();
			}
			
		});
		
		view.findViewById(R.id.bank).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showBankList();
			}
			
		});
		
		view.findViewById(R.id.update).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doUpdate();
			}
			
		});


		return view;
	}

	private void showBankList() {

		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(getActivity(), R.layout.spinner_item, bankList);

		new AlertDialog.Builder(getActivity()).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				bank = bankList.get(position);
				((Button)getView().findViewById(R.id.bank)).setText(bank.getName());
				dlg.dismiss();
			}
		}).show();
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

		new BaseTask<String, String, Boolean>(getActivity(), "请稍后...") {

			@Override
			protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().accountCode(getActivity());
			}

			@Override
			protected void onSuccess(Boolean arg0) {
				Toast.makeText(getActivity(), "验证码已发送到您的手机", Toast.LENGTH_LONG)
						.show();

				sendLimitTime = 30;
				btnSend.setEnabled(false);
				handler.postDelayed(runnable, 1000);
			}
		}.execute("");

	}

	private void doUpdate() {
		if(NullUtils.isEmpty(((EditText) getView().findViewById(R.id.code)).getText().toString())) {
			Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
			return;
		}

		new BaseTask<String, String, Boolean>(getActivity(), "请稍后...") {

			@Override
			protected Boolean doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().accountupdate(getActivity(), params[0], params[1], params[2], params[3], params[4]);
			}

			@Override
			protected void onSuccess(Boolean result) {
				sendLimitTime = 0;
				
				new AlertDialog.Builder(getActivity())
				.setTitle("提示")
				.setMessage("账户资料修改成功!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						getActivity().finish();
					}})
				.show();    	
			}

		}.execute(
			  ((Button) getView().findViewById(R.id.bank)).getText().toString(),
			  ((EditText) getView().findViewById(R.id.card)).getText().toString(),
			  ((EditText) getView().findViewById(R.id.name)).getText().toString(),
			  ((EditText) getView().findViewById(R.id.citizenId)).getText().toString(),
			  ((EditText) getView().findViewById(R.id.code)).getText().toString()
		);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onStart() {
		super.onStart();
 	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
 	}

	@Override
	public void onStop() {
		super.onStop();
 	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
 	}

	@Override
	public void onDestroy() {
		super.onDestroy();
 	}

	@Override
	public void onDetach() {
		super.onDetach();
 	}

	private void refresh() {

		new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().accountinfo(getActivity());
			}

			@Override
			protected void onSuccess(JSONObject result) {
				try {
					bankList.clear();
					JSONArray array = result.getJSONArray("bankList");
					for(int i=0; i<array.length(); i++) {
						bankList.add(new Item(array.getJSONObject(i).getString("id"), array.getJSONObject(i).getString("name")));
					}
					
					((Button)getView().findViewById(R.id.bank)).setText(result.getString("bank"));
					((EditText)getView().findViewById(R.id.card)).setText(result.getString("card"));
					((EditText)getView().findViewById(R.id.name)).setText(result.getString("name"));
					((EditText)getView().findViewById(R.id.citizenId)).setText(result.getString("citizenId"));



					  isTelecom = result.optBoolean("isTelecom");
					  ((TextView)getView().findViewById(R.id.ePayNumber)).setText(result.getString("ePayNumber"));
					  boolean isOpen =  result.getBoolean("isOpen");
					  Button bt_ePay_open = (Button)getView().findViewById(R.id.bt_ePay_open);
					  Button bt_epay_confirm = (Button)getView().findViewById(R.id.bt_epay_confirm);
					  View ll_ePayConfirm = getView().findViewById(R.id.ll_ePayConfirm);
					  if(isOpen){
						bt_ePay_open.setVisibility(View.GONE);
						ll_ePayConfirm.setVisibility(View.GONE);
					  }else{
						bt_ePay_open.setVisibility(View.VISIBLE);
						ll_ePayConfirm.setVisibility(View.VISIBLE);
					  }
					  bt_ePay_open.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						    Uri uri = Uri.parse("http://bestpay.ctdns.net/bestpay_common_signed.apk");
						    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
						    startActivity(downloadIntent);
						}
					  });
					  bt_epay_confirm.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						    ePayConfirm();
						}
					  });


					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.execute("");
	}

	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser == true) {
			refresh();
		}
	}

    private void ePayConfirm() {

	  if(!isTelecom){
		new AlertDialog.Builder(getActivity()).setTitle("温馨提示").setMessage("非电信用户，异网翼支付账号").setPositiveButton("确认开通", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			  confirm();
		    }
		}).setNegativeButton("暂不开通", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			  dialog.dismiss();
		    }
		}).show();
	  }
	  confirm();
    }

    private void confirm(){
	  new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

		@Override
		protected JSONObject doInBack(String... params) throws HttpException,
			  IOException, TaskResultException {
		    return CRApplication.getApp().ePayConfirm(getActivity(),isTelecom);
		}

		@Override
		protected void onSuccess(JSONObject result) {
		    boolean isSuccess = result.optBoolean("isSuccess");
		    Button bt_ePay_open = (Button)getView().findViewById(R.id.bt_ePay_open);
		    Button bt_epay_confirm = (Button)getView().findViewById(R.id.bt_epay_confirm);
		    if(isSuccess){
			  bt_ePay_open.setVisibility(View.GONE);
			  bt_epay_confirm.setVisibility(View.GONE);
		    }else{
			  Toast.makeText(getActivity(),result.optString("desc"),Toast.LENGTH_SHORT).show();
		    }
		}

	  }.execute("");
    }
}
