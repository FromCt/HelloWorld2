package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.view.AppleListView;
import com.crunii.android.fxpt.view.AppleListView.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfitDetailActivity extends Activity {
    String date,userId="",partnerName = "";
    
	RadioButton radio1, radio2;
	JSONArray incoming, uncollected;

	AppleListView listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;
    String type = "";//partner,myself

	int pageindex = 1;
	private RadioGroup radiogroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profitdetail);
		
		date = getIntent().getExtras().getString("date");

		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
		radio1.setChecked(true);
		listView = (AppleListView) findViewById(R.id.listView);
		lvAdapter = new ListAdapter(getBaseContext());
		listView.setAdapter(lvAdapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				pageindex = 1;
				refresh();
			}
		});
		listView.setPrevButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pageindex > 1) {
					pageindex--;
				}
				refresh();
			}
		});
		listView.setNextButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pageindex++;
				refresh();
			}
		});
		listView.hidePrevButton();
		listView.hideNextButton();

		listView.setEmptyView(findViewById(R.id.empty));
        type = getIntent().getExtras().getString("type");

        if(type.equals(MyProfitActivity.ProfitDetailType.partner.toString())){
            userId =  getIntent().getExtras().getString("userId");
			partnerName = getIntent().getExtras().getString("partnerName");
        }else if(type.equals("yesterday")){
			radiogroup.setVisibility(View.GONE);
		}
		refresh();
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doRadio1(View v) {
		if(incoming != null) {
			itemHolder.clear();
			for (int i = 0; i < incoming.length(); i++) {
				try {
					itemHolder.add(incoming.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			lvAdapter.notifyDataSetChanged();
		}
	}

	public void doRadio2(View v) {
		if(uncollected != null) {
			itemHolder.clear();
			for (int i = 0; i < uncollected.length(); i++) {
				try {
					itemHolder.add(uncollected.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			lvAdapter.notifyDataSetChanged();
		}
	}
	
	private void refresh() {

		new BaseTask<String, String, JSONObject>(this, "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().profitdetail(getApplicationContext(), date,type,userId);
			}
			@Override
			protected void onSuccess(JSONObject result) {
				try {
					String total = result.getString("total");
					incoming = result.getJSONArray("incoming");
					uncollected = result.getJSONArray("uncollected");
					if(type.equals(MyProfitActivity.ProfitDetailType.partner.toString())){
						((TextView)findViewById(R.id.tv_total)).setText(partnerName + " 收益:  " + total);
					}else{
						((TextView)findViewById(R.id.tv_total)).setText(date + " 收益:  " + total);
					}
					listView.hidePrevButton();
					listView.hideNextButton();
					
					itemHolder.clear();
					if(radio1.isChecked()) {
						for (int i = 0; i < incoming.length(); i++) {
							itemHolder.add(incoming.getJSONObject(i));
						}
					} else if(radio2.isChecked()) {
						for (int i = 0; i < uncollected.length(); i++) {
							itemHolder.add(uncollected.getJSONObject(i));
						}
					}
					lvAdapter.notifyDataSetChanged();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.execute("");
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
				view = inflater.inflate(R.layout.profit_detail_list_item, null);
				viewHolder = new ViewHolder();

				viewHolder.orderId = (TextView) view.findViewById(R.id.orderId);
				viewHolder.goods = (TextView) view.findViewById(R.id.goods);
				viewHolder.value = (TextView) view.findViewById(R.id.value);
				viewHolder.channel = (TextView) view.findViewById(R.id.channel);
				viewHolder.userName = (TextView) view.findViewById(R.id.userName);
				viewHolder.orderPerson = (TextView) view.findViewById(R.id.orderPerson);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.position = position;
			
			showListItem(itemHolder.get(position), viewHolder);

			return view;
		}
	}

	class ViewHolder {
		int position;

		TextView orderId;
		TextView goods;
		TextView value;
		TextView channel;
		TextView userName;
		TextView orderPerson;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder) {
		viewHolder.orderId.setText(json.optString("orderId"));
		viewHolder.goods.setText(json.optString("goods"));
		viewHolder.value.setText("¥"+json.optString("value"));
		viewHolder.channel.setText(json.optString("channel"));
		viewHolder.userName.setText(json.optString("userName"));
		viewHolder.orderPerson.setText(json.optString("orderPerson"));
	}


}
