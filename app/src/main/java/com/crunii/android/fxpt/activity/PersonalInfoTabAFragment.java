package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PersonalInfoTabAFragment extends Fragment {

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

		View view =  inflater.inflate(R.layout.personalinfo_tab_a, container, false);
		
		view.findViewById(R.id.addAddr).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), AddressActivity.class);
				getActivity().startActivityForResult(i, PersonalInfoActivity.TAB_A_EDIT_ADDRESS_REQUEST);
			}
			
		});
		
		return view;
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

	private void saveCrmNum(String crmNum) {
		new BaseTask<String, String, Void>(getActivity(), "请稍后...") {

			@Override
			protected Void doInBack(String... args) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().savecrmnum(getActivity(), args[0]);
			}

			@Override
			protected void onSuccess(Void result) {
				new AlertDialog.Builder(getActivity())
				.setTitle("提示")
				.setMessage("保存成功!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
						//getActivity().finish();
					}})
				.show();  
			}

		}.execute(crmNum);
	}

    private void saveActivityAddress() {
        String name = ((EditText)getView().findViewById(R.id.activity_address_name)).getText().toString();
        String addr = ((EditText)getView().findViewById(R.id.activity_address_addr)).getText().toString();
        String phone = ((EditText)getView().findViewById(R.id.activity_address_phone)).getText().toString();

        if(name.equals("")) {
            Toast.makeText(getActivity(), "请输入收货人", Toast.LENGTH_SHORT).show();
            return;
        }
        if(addr.equals("")) {
            Toast.makeText(getActivity(), "请输入收获地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.equals("")) {
            Toast.makeText(getActivity(), "请输入联系电话", Toast.LENGTH_SHORT).show();
            return;
        }

        new BaseTask<String, String, Void>(getActivity(), "请稍后...") {

            @Override
            protected Void doInBack(String... args) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().saveActivityAddress(getActivity(), args[0], args[1], args[2]);
            }

            @Override
            protected void onSuccess(Void result) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("保存成功!")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                //getActivity().finish();
                            }})
                        .show();
            }

        }.execute(name, addr, phone);
    }
	
	void refresh() {

		new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().personalinfo(getActivity());
			}
			@Override
			protected void onSuccess(JSONObject result) {
				try {
					((TextView)getView().findViewById(R.id.account)).setText(result.getString("account"));
					((TextView)getView().findViewById(R.id.partName)).setText(result.getString("partName"));
					((TextView)getView().findViewById(R.id.partName)).setText(result.getString("partName"));
					((TextView)getView().findViewById(R.id.dlsName)).setText(result.getString("dlsName"));
					((TextView)getView().findViewById(R.id.company)).setText(result.getString("company"));
					((TextView)getView().findViewById(R.id.channelattribute)).setText(result.getString("channelattribute"));//渠道类型
					((TextView)getView().findViewById(R.id.channelType)).setText(result.getString("channelType")); //渠道属性
					((EditText)getView().findViewById(R.id.crm_num)).setText(result.getString("crm_num"));
					getView().findViewById(R.id.btn_crm_save).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							String crmNum = ((EditText)getView().findViewById(R.id.crm_num)).getText().toString();
							saveCrmNum(crmNum);
						}
					});
					LinearLayout addressView = (LinearLayout) getView().findViewById(R.id.address_view);
					addressView.removeAllViews();

					LayoutInflater inflater = LayoutInflater.from(getActivity());
					JSONArray address = result.getJSONArray("address");
					for(int i=0; i<address.length(); i++) {
						JSONObject json = address.getJSONObject(i);
						View view = inflater.inflate(R.layout.address_list_item, null);
						TextView isDefault = (TextView) view.findViewById(R.id.isdefault);
						if(json.getString("isDefault").equals("1")) {
							isDefault.setText("[默认]");
							isDefault.setTextColor(Color.parseColor("#FF0000"));
						} else {
							isDefault.setText("[备用]");
							isDefault.setTextColor(Color.parseColor("#333333"));
						}
						TextView addr = (TextView) view.findViewById(R.id.addr);
						addr.setText(json.getString("addr"));
						
						view.setTag(json);
						
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								JSONObject json = (JSONObject) arg0.getTag();
								Intent i = new Intent(getActivity(), AddressActivity.class);
								i.putExtra("json", json.toString());
								getActivity().startActivityForResult(i, PersonalInfoActivity.TAB_A_EDIT_ADDRESS_REQUEST);
							}
							
						});
						addressView.addView(view);
					}


                    //address for activity
                    JSONObject activityAddress = result.getJSONObject("activityAddress");
                    ((EditText)getView().findViewById(R.id.activity_address_name)).setText(activityAddress.getString("name"));
                    ((EditText)getView().findViewById(R.id.activity_address_addr)).setText(activityAddress.getString("addr"));
                    ((EditText)getView().findViewById(R.id.activity_address_phone)).setText(activityAddress.getString("phone"));
                    getView().findViewById(R.id.btn_activity_address_save).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveActivityAddress();
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


    public void doLogout(View v) {
        if(CRApplication.getToken(getActivity()) != null) {
            final String[] items = { "退出当前账号", "关闭分销平台客户端" };
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setItems(items,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    logout();
                                    break;
                                case 1:
                                    exit();
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }

    }

    private void exit(){
        CRApplication.getInstance().exit();
    }
    private void logout() {

        new BaseTask<String, String, Boolean>(getActivity(), "正在注销...") {

            @Override
            protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().logout(getActivity());
            }

            @Override
            protected void onSuccess(Boolean arg0) {
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Log.i("ct++++", "jump to login activity");
				startActivity(intent);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

		    /*Intent intent = new Intent(getActivity(),LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);*/

                CRApplication.setToken(getActivity(), null);
                CRApplication.setId(getActivity(), null);
                CRApplication.setName(getActivity(), null);
                CRApplication.setUnread(0);


            }

        }.execute("");

    }
}
