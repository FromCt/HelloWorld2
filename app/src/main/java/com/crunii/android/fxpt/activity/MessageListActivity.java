package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.util.MyAsynImageLoader;
import com.crunii.android.fxpt.view.AppleListView;
import com.crunii.android.fxpt.view.AppleListView.OnRefreshListener;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends Activity {
    String category = "system"; //消息分类，system 系统消息  personal个人消息
    
	RadioButton radio1, radio2;

	AppleListView listView;
	List<JSONObject> itemHolder = new ArrayList<JSONObject>();
	ListAdapter lvAdapter;

	int pageindex = 1;
	
	private MyAsynImageLoader imageLoader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagelist);
		
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radio1.setChecked(true);
		
		imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), this);

		listView = (AppleListView) findViewById(R.id.listView);
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

	public void doRadio1(View v) {
		category = "system";
		refresh(true);
	}

	public void doRadio2(View v) {
		category = "personal";
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
				return CRApplication.getApp().messagelist(getApplicationContext(), category, pageindex);
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
				view = inflater.inflate(R.layout.message_list_item, null);
				viewHolder = new ViewHolder();
				
				viewHolder.urlView = view.findViewById(R.id.url_view);
				viewHolder.textView = view.findViewById(R.id.text_view);
				viewHolder.hybridView = view.findViewById(R.id.hybrid_view);
				viewHolder.image = (ImageView) view.findViewById(R.id.image);
				viewHolder.richtext = (TextView) view.findViewById(R.id.richtext);
				viewHolder.time = (TextView) view.findViewById(R.id.time);
				viewHolder.content = (TextView) view.findViewById(R.id.content);

				view.setTag(viewHolder);
				viewHolder.urlView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.position = position;
			viewHolder.read = itemHolder.get(position).optBoolean("read");
			
			showListItem(itemHolder.get(position), viewHolder);

			return view;
		}
	}

	class ViewHolder {
		int position;
		boolean read;
		
		View urlView;
		View hybridView;
		View textView;

		TextView time;
		TextView content;
		
		TextView richtext;
		ImageView image;
	}

	private void showListItem(JSONObject json, ViewHolder viewHolder) {
		String url = json.optString("url");
		if(NullUtils.isEmpty(url)) {
			viewHolder.urlView.setVisibility(View.GONE);
		} else {
			viewHolder.urlView.setVisibility(View.VISIBLE);
		}
		
		if(json.optString("type").equals("1")) {
			viewHolder.textView.setVisibility(View.VISIBLE);
			viewHolder.hybridView.setVisibility(View.GONE);

			viewHolder.time.setText(json.optString("time"));
			String content = json.optString("content");
			//TODO
			content = "重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站重庆电信4G移动基站";
			viewHolder.content.setText(content);
		} else {
			viewHolder.textView.setVisibility(View.GONE);
			viewHolder.hybridView.setVisibility(View.VISIBLE);

			String richtext = json.optString("richtext");
			//TODO
			//richtext = "<img src='jdyh'><br><font color='#f28e36'><big>华为P7</big></font> <font color='#000'><small>全网通<br>领先4G, 瞬息通晓世事万象<br>6.5mm全球领先超薄LTE手机<br>5英寸全高清LCD JDI显示屏</small></font>";
			viewHolder.richtext.setText(Html.fromHtml(richtext, imgGetter, null));
			imageLoader.showImageAsyn(new SoftReference<ImageView>(viewHolder.image), json.optString("image"), R.drawable.phone);
		}

	}

	public void doItemClick(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();

		//

	}
	
	public void doOpenUrl(View v) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		
		String url = itemHolder.get(viewHolder.position).optString("url");
		//TODO
		url = "http://www.baidu.com";
		Intent i = new Intent(this, WebviewActivity.class);
		i.putExtra("url", url);
		startActivity(i);
	}
	
	ImageGetter imgGetter = new ImageGetter() {
        public Drawable getDrawable(String source) {
              Drawable drawable = null;
              if(source.equals("jdyh")) {
            	  drawable = MessageListActivity.this.getResources().getDrawable(R.drawable.jdyh);
              } else if(source.equals("xpsx")) {
            	  drawable = MessageListActivity.this.getResources().getDrawable(R.drawable.xpsx);
              } else if(source.equals("czcx")) {
            	  drawable = MessageListActivity.this.getResources().getDrawable(R.drawable.czcx);
              }
              
              if(drawable != null) {
            	  drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
              }
              return drawable;
        }
};
}
