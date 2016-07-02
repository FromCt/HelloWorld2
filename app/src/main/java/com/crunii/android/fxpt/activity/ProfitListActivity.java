package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfitListActivity extends Activity {

	JSONArray profitList;
	
	ListView listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profitlist);
		
		listView = (ListView) findViewById(R.id.listView);
		lvAdapter = new ListAdapter(getBaseContext());
		listView.setAdapter(lvAdapter);
		
		refresh();
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	private void refresh() {

		new BaseTask<String, String, JSONArray>(this, "请稍后...") {

			@Override
			protected JSONArray doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().profitlist(getApplicationContext());
			}

			@Override
			protected void onSuccess(JSONArray result) {
				try {

					((TextView)findViewById(R.id.tv_total)).setText("前12月累积收益");
					
					itemHolder.clear();
					int length = result.length();
					for(int i=0; i<length; i++) {
						itemHolder.add(result.getJSONObject(i));
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
				view = inflater.inflate(R.layout.profit_list_item, null);
				viewHolder = new ViewHolder();

				viewHolder.month = (TextView) view.findViewById(R.id.month);
				viewHolder.profit = (TextView) view.findViewById(R.id.profit);

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

		TextView month;
		TextView profit;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder) {
		viewHolder.month.setText(json.optString("month"));
		viewHolder.profit.setText(json.optString("profit"));
	}

	public void doItemClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();

		String month = itemHolder.get(viewHolder.position).opt("month").toString();
		Intent i = new Intent(this, ProfitDetailActivity.class);
        i.putExtra("type", MyProfitActivity.ProfitDetailType.myself.toString());
		i.putExtra("date", month);
		startActivity(i);
	}

//	public void doPrevYear(View v) {
//		year--;
//		nextyear.setEnabled(true);
//		refresh();
//	}
//
//	public void doNextYear(View v) {
//		year++;
//		if(year == currentYear) {
//			nextyear.setEnabled(false);
//		}
//		refresh();
//	}
}
