package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.view.AppleListView;
import com.crunii.android.fxpt.view.AppleListView.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonalInfoTabCFragment extends Fragment {

	AppleListView listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;
	int pageindex = 1;
	private String keyword = "";
	
	JSONArray commissionModeList, channelList, companyList, stationList;
	boolean readonly = false;

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

		return inflater.inflate(R.layout.personalinfo_tab_c, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = (AppleListView) getView().findViewById(R.id.listView);
		lvAdapter = new ListAdapter(getActivity());
		listView.setAdapter(lvAdapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				pageindex = 1;
				getPartnerList();
			}
		});
		listView.setPrevButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pageindex > 1) {
					pageindex--;
				}
				getPartnerList();
			}
		});
		listView.setNextButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pageindex++;
				getPartnerList();
			}
		});
		listView.hidePrevButton();
		listView.hideNextButton();

	}

	class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public ListAdapter(Context mContext) {
			super();
			this.inflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return itemHolder.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder;

			if (view == null) {
				view = inflater.inflate(R.layout.partner_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.name = (TextView) view.findViewById(R.id.name);
				viewHolder.phone = (TextView) view.findViewById(R.id.phone);
				view.setTag(viewHolder);
				
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.position = position;
			viewHolder.name.setText(itemHolder.get(position).optString("name").toString());
			viewHolder.phone.setText(itemHolder.get(position).optString("phone").toString());
			
			if(!readonly) {
				view.findViewById(R.id.manage).setVisibility(View.VISIBLE);
				view.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View arg0) {
						ViewHolder viewHolder = (ViewHolder) arg0.getTag();
						//String json = itemHolder.get(viewHolder.position).toString();
						checkPartnerSystem(itemHolder.get(viewHolder.position));
					}
					
				});
			} else { //趣购分销商不能进行管理操作
				view.findViewById(R.id.manage).setVisibility(View.GONE);
			}

			return view;
		}
	}

	class ViewHolder {
		int position;
		TextView name;
		TextView phone;
	}

	private void checkPartnerSystem(JSONObject jsonObject) {
		final String userId = jsonObject.optString("id");
		final String phone = jsonObject.optString("phone");
		final String userName = jsonObject.optString("name");//伙伴姓名
		final String channelId = jsonObject.optString("channelId");
		final String channelName = jsonObject.optString("channelName");
		new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
				String type = "update";
				return CRApplication.getApp().checkPartnerSystem(getActivity(), phone, "", type,userId);
			}

			@Override
			protected void onSuccess(JSONObject jsonObject) {
				//-----checkPartnerSystem获取的值
				boolean isMsg = jsonObject.optBoolean("isMsg"); //是否有有效信息
				boolean isDQ = jsonObject.optBoolean("isDQ"); //是否是电子渠道
				channelList = jsonObject.optJSONArray("channelList");
//				commissionControl //酬金分配控制 1 分销伙伴拿全额 2 分销伙伴拿百分比 3我拿全额
//				commissionPercent  //如果commissionControl为2，则commissionPercent为1~99的整数
				int commissionControl = jsonObject.optInt("commissionControl");
				int commissionPercent = jsonObject.optInt("commissionPercent");
				String userId = jsonObject.optString("userId");

				Intent intent = new Intent(getActivity(),CreatPartnerActivity.class);
				intent.putExtra("requestType","update");
				intent.putExtra("isMsg",isMsg);
				intent.putExtra("isDQ",isDQ);
				intent.putExtra("userName",userName);
				intent.putExtra("phone",phone);
				intent.putExtra("channelId",channelId);
				intent.putExtra("channelName",channelName);
				intent.putExtra("channelList",channelList.toString());
				intent.putExtra("commissionControl",commissionControl);
				intent.putExtra("commissionPercent",commissionPercent);
				intent.putExtra("userId",userId);
				getActivity().startActivityForResult(intent, PersonalInfoActivity.TAB_C_EDIT_PARTNER_REQUEST);

			}
		}.execute("");
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

	void refresh() {

		new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().partner(getActivity());
			}

			@Override
			protected void onSuccess(JSONObject result) {
				commissionModeList = result.optJSONArray("commissionModeList");
				channelList = result.optJSONArray("channelList");
				companyList = result.optJSONArray("companyList");
				stationList = result.optJSONArray("stationList");
				readonly = result.optBoolean("readonly");
				
				String userLevel = result.optString("userLevel");
				if(userLevel.equals("1")) { //一级分销商
					getView().findViewById(R.id.level1view).setVisibility(View.VISIBLE);
					getView().findViewById(R.id.level2view).setVisibility(View.GONE);
					getView().findViewById(R.id.search_partner).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent i = new Intent(getActivity(), KeywordActivity.class);
							i.putExtra("hint", "请输入伙伴姓名");
							getActivity().startActivityForResult(i, PersonalInfoActivity.TAB_C_KEYWORD_REQUEST);
						}
						
					});

                  getView().findViewById(R.id.add_partner).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							isBuildPartner();
						}
					});
					//趣购分销商不能执行添加操作
					if(readonly) {
						getView().findViewById(R.id.add_partner).setVisibility(View.GONE);
					}
					getPartnerList();

				} else { //二级分销商
					getView().findViewById(R.id.level1view).setVisibility(View.GONE);
					getView().findViewById(R.id.level2view).setVisibility(View.VISIBLE);
					
					String info1 = "\n您所属的分公司为:" + result.optString("company") + "\n\n"
							+ "您所属的支局是:" + result.optString("station") + "\n\n"
							+ "您所属的渠道类别为:" + result.optString("channelType") + "\n";
					((TextView)getView().findViewById(R.id.info1)).setText(info1);

					String info2 = "您的一级分销商的信息为:" + "\n\n"
							+ "姓名:" + result.optString("upperName") + "  " 
							+ "联系电话:" + result.optString("upperPhone");
					((TextView)getView().findViewById(R.id.info2)).setText(info2);
					
					final String upperName = result.optString("upperName");
					getView().findViewById(R.id.close_account).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							Dialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("您是否要注消账号，不再成为" + upperName + "的二级分销商？")
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											doCloseAccount();
										}
									}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									}).create();
							alertDialog.show();
						}

					});
				}
				
			}

		}.execute("");
	}

	/***
	 * 增加伙伴
	 */
	private void isBuildPartner(){

		new BaseTask<String,String,Boolean>(getActivity(),"请稍后..."){

			@Override
			protected void onSuccess(Boolean aBoolean) {
				Intent i = new Intent(getActivity(),AddPartnerActivity.class);
				getActivity().startActivityForResult(i, PersonalInfoActivity.TAB_C_EDIT_PARTNER_REQUEST);
			}

			@Override
			protected Boolean doInBack(String... strings) throws HttpException, IOException, TaskResultException {
				return CRApplication.getApp().isBuildPartner(getActivity());
			}
		}.execute("");

	}

	void setKeyword(String name) {
		keyword = name;
		((TextView)getView().findViewById(R.id.title)).setText(name);
		if(!name.equals("")) {
			getView().findViewById(R.id.title).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.add_partner).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.title).setVisibility(View.GONE);
			getView().findViewById(R.id.add_partner).setVisibility(View.VISIBLE);
		}
	}
	
	String getKeyword() {
		return keyword;
	}
	
	void getPartnerList() {

		new BaseTask<String, String, JsonPage>(getActivity(), "请稍后...") {

			@Override
			protected JsonPage doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().partnerlist(getActivity(), pageindex, keyword);
			}

			@Override
			protected void onSuccess(JsonPage result) {
				
				List<JSONObject> orderList = result.getJsonList();
				if(result.isHasPrevPage() || result.isHasNextPage()) {
					if (result.isHasPrevPage()) {
						listView.enablePrevButton();
					} else {
						listView.disablePrevButton();
					}
					if (result.isHasNextPage()) {
						listView.enableNextButton();
					} else {
						listView.disableNextButton();
					}
				} else {
					listView.hidePrevButton();
					listView.hideNextButton();
				}
				itemHolder.clear();
				for (int i = 0; i < orderList.size(); i++) {
					itemHolder.add(orderList.get(i));
				}

				//重新new adapter而不是notifyDataSetChanged，避免HeaderViewListAdapter IndexOutOfBoundsException
				//lvAdapter.notifyDataSetChanged();
				lvAdapter = new ListAdapter(getActivity());
				listView.setAdapter(lvAdapter);
			}

			@Override
			protected void onPostExecute(JsonPage result) {
				super.onPostExecute(result);
				listView.onRefreshComplete();
			}
		}.execute("");
	}
	
	private void doCloseAccount() {

		new BaseTask<String, String, Boolean>(getActivity(), "请稍后...") {

			@Override
			protected Boolean doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().closeaccount(getActivity());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				CRApplication.setToken(getActivity(), null);
				CRApplication.setId(getActivity(), null);
				CRApplication.setName(getActivity(), null);
				CRApplication.setUnread(0);

			}

			@Override
			protected void onSuccess(Boolean result) {
				Toast.makeText(getActivity(), "您的分销账户已经注销", Toast.LENGTH_LONG).show();
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				//CRApplication.getInstance().exit();
			}

		}.execute("");

	}
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser == true) {
			refresh();
		}
	}
}
