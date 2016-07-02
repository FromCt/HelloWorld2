package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.business.Item;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.view.AppleListView;
import com.crunii.android.fxpt.view.AppleListView.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoQueryActivity extends Activity {
    String category = "info"; //查询内容，info客户信息 address光纤地址
	String addressCategory = "";
    
	RadioButton radio1, radio2;

	List<Item> typeList = new ArrayList<Item>();
	Item type = new Item("mobile", "手机");
	AppleListView listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;
	EditText gq_query;
	ListView gxq_list;
	List<JSONObject> holder = new ArrayList<JSONObject>();
	String info_query = "",gqxq_query = "";
	int pageindex = 1;
	MyListAdapter myListAdapter ;
	Button gq_search;
	boolean checkFlag = false;
	TextView search_result;
	private enum AddressCategory{
		address,//时按地址查询
		number; //时按号码查询
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infoquery);

		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		gq_search = (Button) findViewById(R.id.gq_search);


		radio1.setChecked(true);
		findViewById(R.id.ll_gq).setVisibility(View.GONE);
		gq_query = (EditText)findViewById(R.id.gq_query);
		search_result = (TextView)findViewById(R.id.search_result);

		gxq_list = (ListView)findViewById(R.id.gxq_list);
		myListAdapter = new MyListAdapter();
		gxq_list.setAdapter(myListAdapter);
		gxq_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				checkFlag = true;
				gqxq_query = holder.get(position).optString("ID");
				gq_query.setText(holder.get(position).optString("ASSEMBLENAME"));
				addressCategory = AddressCategory.address.toString();
				gxq_list.setVisibility(View.INVISIBLE);
				gqxq_refresh();
			}
		});


		typeList.add(new Item("mobile", "手机"));
		typeList.add(new Item("broadband", "宽带"));
		typeList.add(new Item("line", "固话"));
		
		listView = (AppleListView) findViewById(R.id.listView);
		lvAdapter = new ListAdapter(getBaseContext());
		listView.setAdapter(lvAdapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				pageindex = 1;
				infoRefresh(true);
			}
		});
		listView.setPrevButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pageindex > 1) {
					pageindex--;
				}
				infoRefresh(true);
			}
		});
		listView.setNextButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pageindex++;
				infoRefresh(true);
			}
		});
		listView.hidePrevButton();
		listView.hideNextButton();

		setPermission();

	}


	/**
	 * 5月底设置判断权限在查询
	 */
	public void setPermission(){
		//权限判断展现效果
		HashMap<String, String> postparams = new HashMap<String, String>();
		HttpTool.sendPost(InfoQueryActivity.this, Constant.URL.INFO_PERMISSION, postparams, new HttpPostProp() {
			@Override
			public void dealRecord(Map record) {

				boolean value = (boolean) record.get("value");
				if (value) {

				} else {
					doRadio2(radio2);
				}
			}
		});

		/*if (false) {

		} else {
			radio1.setVisibility(View.GONE);
			doRadio2(radio2);
		}*/
	}


	private class MyTextWatcher implements TextWatcher {
		private EditText mEditText;
		private CharSequence charSequence;

		public MyTextWatcher(EditText editText) {
			mEditText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			charSequence = s;

		}

		@Override
		public void afterTextChanged(Editable s) {
				String str = charSequence.toString();
			if(addressCategory.equals(AddressCategory.address.toString()) && !str.equals("") ){
				search_result.setText("");
				gxq_list.setVisibility(View.VISIBLE);
				gq_query( str);
			}
		}
	}


	public void doRadio1(View v) {
		category = "info";
		findViewById(R.id.ll_gq).setVisibility(View.GONE);
		findViewById(R.id.info).setVisibility(View.VISIBLE);
		((EditText)findViewById(R.id.gq_query)).setText("");
		((EditText)findViewById(R.id.gq_query)).setHint("请输入查询号码");
		((EditText)findViewById(R.id.gq_query)).setInputType(InputType.TYPE_CLASS_PHONE);

		pageindex = 1;
		itemHolder.clear();
		lvAdapter.notifyDataSetChanged();
	}

	public void doRadio2(View v) {
		category = "address";
		findViewById(R.id.ll_gq).setVisibility(View.VISIBLE);
		findViewById(R.id.info).setVisibility(View.GONE);
		doAddress(v);
		((RadioButton)findViewById(R.id.rb_address)).setChecked(true);


	}

	
	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doInfoSearch(View v) {
		infoRefresh(true);
	}



	private void infoRefresh(final boolean showProgressDialog) {
		info_query = ((EditText)findViewById(R.id.info_query)).getText().toString();

		new BaseTask<String, String, JsonPage>(this) {

			@Override
			protected JsonPage doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
					return CRApplication.getApp().infoquery(getApplicationContext(), info_query, type.getId(), pageindex);
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
				lvAdapter.notifyDataSetChanged();
				
			}

		}.execute("");
	}

	public void doGQSearch(View view){
		if (addressCategory.equals(AddressCategory.number.toString())){
			search_result.setText("");
			gqxq_query = gq_query.getText().toString();
		}

		gqxq_refresh();

	}

	private void gqxq_refresh(){

		new BaseTask<String, String, JSONObject>(this,"正在查询，请稍后...") {

			@Override
			protected JSONObject doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().addressquery(getApplicationContext(), gqxq_query,addressCategory);
			}

			@Override
			protected void onSuccess(JSONObject result) {

				String type = result.optString("type");
				search_result.setText(type);



			}

		}.execute("");
	}

	private void gq_query(final String query) {

		new BaseTask<String, String, JSONObject>(this) {



			@Override
			protected JSONObject doInBack(String... params) throws HttpException,
					IOException, TaskResultException {
					return CRApplication.getApp().gqQuery(getApplicationContext(), query);
			}

			@Override
			protected void onSuccess(JSONObject result) {
				JSONArray jsonArray = result.optJSONArray("dataList");
				if(jsonArray != null && jsonArray.length() != 0){
					holder.clear();
					for(int i=0;i < jsonArray.length();i++){
						holder.add(jsonArray.optJSONObject(i));
					}
				}
				myListAdapter.notifyDataSetChanged();

				//myListAdapter.notifyDataSetChanged();
//				"ASSEMBLENAME":"重庆市石柱县南宾镇利民路门面1层AA建筑",
//						"ID":"380083224",
//						"ASSEMBLENAME_HIGHTLIGHT":"重庆市石柱县南宾镇利民路门面1层<font color='red'>AA</font>建筑",

			}

		}.execute("");
	}

	public class MyListAdapter extends BaseAdapter{


		@Override
		public int getCount() {
			return holder.size();
		}


		@Override
		public Object getItem(int position) {
			return holder.get(position);
		}


		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = (TextView) View.inflate(InfoQueryActivity.this,R.layout.list_item_text,null);
//			TextView textView = new TextView(InfoQueryActivity.this);
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//			layoutParams.setMargins(0,5,0,5);
//			textView.setHeight(80);
//			textView.setLayoutParams(layoutParams);
//			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setText(Html.fromHtml(holder.get(position).optString("ASSEMBLENAME_HIGHTLIGHT")));
			textView.setTag(holder.get(position));
			return textView;
		}
	}

	public void doAddress(View view) {
		gq_search.setVisibility(View.INVISIBLE);
		gq_query.addTextChangedListener(new MyTextWatcher(gq_query));
		gq_query.setText("");
		gq_query.setHint("请输入查询地址");
		gq_query.setInputType(InputType.TYPE_CLASS_TEXT);
		addressCategory = AddressCategory.address.toString();
		search_result.setText("");
	}

	public void doNumber(View view) {
		gqxq_query = "";
		gq_search.setVisibility(View.VISIBLE);
		gxq_list.setVisibility(View.INVISIBLE);
		gq_query.setText("");
		gq_query.setHint("请输入查询号码");
		gq_query.setInputType(InputType.TYPE_CLASS_PHONE);
		addressCategory = AddressCategory.number.toString();
		search_result.setText("");
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
				view = inflater.inflate(R.layout.query_list_item, null);
				viewHolder = new ViewHolder();


				viewHolder.infoView = view.findViewById(R.id.infoView);
                viewHolder.baseInfo = view.findViewById(R.id.baseInfo);
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.time = (TextView) view.findViewById(R.id.time);
                viewHolder.balance = (TextView) view.findViewById(R.id.balance);
                viewHolder.recommend = (TextView) view.findViewById(R.id.recommend);

                viewHolder.comboView = view.findViewById(R.id.comboView);
                viewHolder.packageName = (TextView) view.findViewById(R.id.packageName);
                viewHolder.packageTime = (TextView) view.findViewById(R.id.packageTime);



				viewHolder.addressView = view.findViewById(R.id.addressView);
				viewHolder.name2 = (TextView) view.findViewById(R.id.name2);
				viewHolder.address = (TextView) view.findViewById(R.id.address);
				viewHolder.accessType = (TextView) view.findViewById(R.id.accessType);
				viewHolder.seq_no = (TextView) view.findViewById(R.id.seq_no);
				
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.position = position;
			
			showListItem(itemHolder.get(position), viewHolder,position);

			return view;
		}
	}

	class ViewHolder {
		int position;

		View infoView;
        View baseInfo;
		TextView name;
		TextView time;
		TextView balance;
		TextView recommend;

        View comboView;
        TextView packageName;
        TextView packageTime;

		View addressView;
		TextView name2;
		TextView address;
		TextView accessType;
		TextView seq_no;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder,int position) {
		if(category.equals("info")) {
			viewHolder.infoView.setVisibility(View.VISIBLE);
			viewHolder.addressView.setVisibility(View.GONE);

            viewHolder.comboView.setVisibility(View.VISIBLE);
			if(position == 0){
                viewHolder.baseInfo.setVisibility(View.VISIBLE);
                viewHolder.name.setText(json.optString("name"));
                viewHolder.time.setText(json.optString("time"));
                viewHolder.packageName.setText(json.optString("packageName"));
                viewHolder.packageTime.setText(json.optString("packageTime"));
                viewHolder.balance.setText(json.optString("balance") + "元");
                viewHolder.recommend.setText(json.optString("recommend"));
            }else{
                viewHolder.baseInfo.setVisibility(View.GONE);
                viewHolder.packageName.setText(json.optString("packageName"));
                viewHolder.packageTime.setText(json.optString("packageTime"));
            }

		}
		
	}

	public void doItemClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();

		//

	}


	public void showTypeList(View v) {

		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, typeList);

		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				type = typeList.get(position);
				((Button)findViewById(R.id.btn_select)).setText(type.getName());
				dlg.dismiss();
			}
		}).show();
	}
}
