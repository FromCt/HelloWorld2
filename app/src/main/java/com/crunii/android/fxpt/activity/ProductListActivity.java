package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyAsynImageLoader;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter.OnRefreshListener;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends Activity {
	String category, mode, speed, packageId, packageName;
	    
	AppleListViewReverseFooter listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;

	int pageindex = 1;
	
	private MyAsynImageLoader imageLoader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productlist);

		category = getIntent().getExtras().getString("category");
		mode = getIntent().getExtras().getString("mode");
		speed = getIntent().getExtras().getString("speed");
		packageId = getIntent().getExtras().getString("packageId");
		packageName = getIntent().getExtras().getString("packageName");
		
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(packageName);
		
		imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), this);

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

		refresh(true);
	}

	public void doBack(View v) {
		onBackPressed();
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
				return CRApplication.getApp().product(getApplicationContext(), pageindex, mode, speed, packageId);
			}

			@Override
			protected void onSuccess(JsonPage result) {
				Log.i("77777777777",result.toString());
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
				view = inflater.inflate(R.layout.product_list_item, null);
				viewHolder = new ViewHolder();
				
				viewHolder.image = (ImageView) view.findViewById(R.id.image);
				viewHolder.text_spmc = (TextView) view.findViewById(R.id.text_spmc);
				viewHolder.text_jg = (TextView) view.findViewById(R.id.text_jg);
				viewHolder.text_sj = (TextView) view.findViewById(R.id.text_sj);

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
		
		TextView text_spmc, text_jg, text_sj;
		ImageView image;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder) {
		
		String name = json.optString("name");
		String price = json.optString("price");
		price = price.replace("/月", "");
		String retailer = json.optString("retailer");
		String image = json.optString("image");
		
		viewHolder.text_spmc.setText(name);
		viewHolder.text_jg.setText(price);
		viewHolder.text_sj.setText(retailer);

		imageLoader.showImageAsyn(new SoftReference<ImageView>(viewHolder.image), image, R.drawable.product);

	}

	public void doItemClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		int position = viewHolder.position;
		
		String templateId = itemHolder.get(position).optString("templateId");
		Intent i = new Intent();

		if(Constant.TEST_FLAG){
			Toast.makeText(this, templateId, Toast.LENGTH_SHORT).show();
		}

		//TODO
		if(templateId.equals(ProductDetailBroadband.templateId)) { //固话/宽带售卖模板
			i.setClass(this, ProductDetailBroadband.class);
		} else if(templateId.equals(ProductDetailTerminal.templateId)) { //终端销售模板
			i.setClass(this, ProductDetailTerminal.class);
		} else if(templateId.equals(ProductDetailContract.templateId)) { //合约机模板
			i.setClass(this, ProductDetailContract.class);
		} else if(templateId.equals(ProductDetailBatch.templateId)) { //批量售卖模板
			i.setClass(this, ProductDetailBatch.class);
		} else if(templateId.equals(ProductDetailRechargeableCard.templateId)) { //批量售卖模板
            i.setClass(this, ProductDetailRechargeableCard.class);
        }else {
			Toast.makeText(this, "Internal Error: unsupported template", Toast.LENGTH_SHORT).show();
			return;
		}
		
		i.putExtra("category", category);
		i.putExtra("mode", mode);
		i.putExtra("speed", speed);
		i.putExtra("packageId", packageId);
		i.putExtra("goodsCode", itemHolder.get(position).optString("goodsCode"));
		startActivity(i);

	}
	
	ImageGetter imgGetter = new ImageGetter() {
        public Drawable getDrawable(String source) {
              Drawable drawable = null;
              if(source.equals("text_spmc")) {
            	  drawable = ProductListActivity.this.getResources().getDrawable(R.drawable.text_spmc);
              } else if(source.equals("text_jg")) {
            	  drawable = ProductListActivity.this.getResources().getDrawable(R.drawable.text_jg);
              } else if(source.equals("text_sj")) {
            	  drawable = ProductListActivity.this.getResources().getDrawable(R.drawable.text_sj);
              }
              
              if(drawable != null) {
            	  drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
              }
              return drawable;
        }
};
}
