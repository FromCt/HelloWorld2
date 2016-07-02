package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.Item;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseNumberActivity extends Activity {
	JSONArray areaArray = new JSONArray();
	JSONArray sectionArray = new JSONArray();;
	String areaId = "";
	String section = "";
	String query = "";
	String databaseId = "";
	    
	AppleListViewReverseFooter listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;

	int pageindex = 1;

	List<Item> areaList = new ArrayList<Item>();
	Item areaItem = new Item("", "null");

	List<Item> sectionList = new ArrayList<Item>();
	Item sectionItem = new Item("", "null");
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_number);
		
		databaseId = getIntent().getExtras().getString("databaseId");

		try {
			areaArray = new JSONArray(getIntent().getExtras().getString("areaList"));
			//区域选择第一个默认为区域列表的第一个
			areaItem = new Item(areaArray.optJSONObject(0).optString("id"), areaArray.optJSONObject(0).optString("name"));
			for(int i=0; i<areaArray.length(); i++) {
				JSONObject json = areaArray.optJSONObject(i);
				areaList.add(new Item(json.optString("id"), json.optString("name")));
			}
			
			sectionArray = new JSONArray(getIntent().getExtras().getString("sectionList"));
			//号段选择第一个默认不选，也即"全部"
			sectionItem = new Item("", "全部");
			sectionList.add(sectionItem);
			for(int i=0; i<sectionArray.length(); i++) {
				String string = sectionArray.optString(i);
				sectionList.add(new Item(string, string));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		listView = (AppleListViewReverseFooter) findViewById(R.id.listView);
		lvAdapter = new ListAdapter(getBaseContext());
		listView.setAdapter(lvAdapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				pageindex = 1;
				refresh(true);
			}
		});
		listView.setPrevButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pageindex > 1) {
					pageindex--;
				}
				refresh(true);
			}
		});
		listView.setNextButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pageindex++;
				refresh(true);
			}
		});
		listView.hidePrevButton();
		listView.hideNextButton();

		doSearch(null);
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void showAreaList(View v) {
		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, areaList);

		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				areaItem = areaList.get(position);
				((Button)findViewById(R.id.btn_area)).setText(areaItem.getName());
				dlg.dismiss();
			}
		}).show();
	}
	
	public void showSectionList(View v) {
		ArrayAdapter<Item> itemAdapter = new ArrayAdapter<Item>(this, R.layout.spinner_item, sectionList);

		new AlertDialog.Builder(this).setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dlg, int position) {
				sectionItem = sectionList.get(position);
				((Button)findViewById(R.id.btn_section)).setText(sectionItem.getName());
				dlg.dismiss();
			}
		}).show();
	}
	
	public void doSearch(View v) {
		areaId = areaItem.getId();
		section = sectionItem.getId();
		query = ((EditText)findViewById(R.id.query)).getText().toString();
		
		pageindex = 1;
		refresh(true);
	}
	
	private void refresh(final boolean showProgressDialog) {

		new BaseTask<String, String, JsonPage>(this) {
			private ProgressDialog loadMask;

			@Override
			protected void onPreExecute() {
				if (showProgressDialog) {
					this.loadMask = ProgressDialog.show(context, null, "请稍候...");
				}
			}

			@Override
			protected JsonPage doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().choosenumber(getApplicationContext(), pageindex, areaId, section, query, databaseId);
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

			@Override
			protected void onPostExecute(JsonPage result) {
				super.onPostExecute(result);
				if (showProgressDialog) {
					this.loadMask.dismiss();
				}
				listView.onRefreshComplete();
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
				view = inflater.inflate(R.layout.choose_number_list_item, null);
				viewHolder = new ViewHolder();
				
				viewHolder.text_number = (TextView) view.findViewById(R.id.number);
				viewHolder.text_prepaid = (TextView) view.findViewById(R.id.prepaid);

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
		
		TextView text_number, text_prepaid;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder) {
		
		String number = json.optString("number");
		String prepaid = json.optString("prepaid");
		
		viewHolder.text_number.setText(number);
		viewHolder.text_prepaid.setText(prepaid);

	}

	public void doItemClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		int position = viewHolder.position;
		
		String number = itemHolder.get(position).optString("number");
		String tele_use = itemHolder.get(position).optString("tele_use");
		String prepaid = itemHolder.get(position).optString("prepaid");

		Intent i = new Intent();
		i.putExtra("number", number);
		i.putExtra("tele_use", tele_use);
		i.putExtra("prepaid", prepaid);
		setResult(RESULT_OK, i);
		finish();
	}
	
}
