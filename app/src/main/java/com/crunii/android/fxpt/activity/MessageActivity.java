package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
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

public class MessageActivity extends Activity {
    String category = Category.personal.toString(); //消息分类，system 系统消息  personal个人消息

    RadioButton radio1, radio2;

    private enum Category {
        system, personal, comments;
    }

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
        category = Category.personal.toString();
        refresh(true);
    }

    public void doRadio2(View v) {
        category = Category.system.toString();
        refresh(true);
    }

    public void doRadio3(View v) {
        category = Category.comments.toString();
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
                if (result.isHasPrevPage() || result.isHasNextPage()) {
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
                view = inflater.inflate(R.layout.message_item, null);
                viewHolder = new ViewHolder();
                viewHolder.personalMsg = (RelativeLayout) view.findViewById(R.id.personalMsg);
                viewHolder.systemMsg = (RelativeLayout) view.findViewById(R.id.systemMsg);
                viewHolder.commentsMsg = (RelativeLayout) view.findViewById(R.id.commentsMsg);
                viewHolder.personIconImage = (ImageView) view.findViewById(R.id.image);
                viewHolder.personTitle = (TextView) view.findViewById(R.id.title_person);
                viewHolder.personMsgDesc = (TextView) view.findViewById(R.id.msg_desc_person);
                viewHolder.personTime = (TextView) view.findViewById(R.id.time_person);
                viewHolder.systemTitle = (TextView) view.findViewById(R.id.title_system);
                viewHolder.systemMsgDesc = (TextView) view.findViewById(R.id.msg_desc_system);
                viewHolder.systemTime = (TextView) view.findViewById(R.id.time_system);
                viewHolder.comments_Name = (TextView) view.findViewById(R.id.comments_Name);
                viewHolder.comments_PhoneNumber = (TextView) view.findViewById(R.id.comments_PhoneNumber);
                viewHolder.comments_time = (TextView) view.findViewById(R.id.comments_time);
                viewHolder.comments_msgDesc = (TextView) view.findViewById(R.id.comments_msgDesc);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            showListItem(itemHolder.get(position), viewHolder);
            return view;
        }
    }

    //消息内容
    class ViewHolder {
        String msgId;
        int position;
        String category;
        ImageView personIconImage;
        TextView personTitle;
        TextView personMsgDesc;
        boolean read;
        TextView personTime;

        TextView comments_Name;
        TextView comments_PhoneNumber;
        TextView comments_time;
        TextView comments_msgDesc;

        TextView systemTitle;
        TextView systemMsgDesc;
        TextView systemTime;
        RelativeLayout personalMsg;
        RelativeLayout systemMsg;
        RelativeLayout commentsMsg;
    }


    private void showListItem(JSONObject json, ViewHolder viewHolder) {
        viewHolder.msgId = json.optString("msgId");
        viewHolder.read = json.optBoolean("read");
        String title = json.optString("title");
        String time = json.optString("time");
        String msgDesc = json.optString("msgDesc");

        if (category.equals(Category.personal.toString())) {
            viewHolder.category = Category.personal.toString();
            viewHolder.personalMsg.setVisibility(View.VISIBLE);
            viewHolder.commentsMsg.setVisibility(View.GONE);
            viewHolder.systemMsg.setVisibility(View.GONE);
            viewHolder.personTitle.setText(title);
            viewHolder.personTime.setText(time);
            viewHolder.personMsgDesc.setText(msgDesc);
            imageLoader.showImageAsyn(new SoftReference<ImageView>(viewHolder.personIconImage), json.optString("iconImage"), R.drawable.app_mms_portal);
        } else if (category.equals(Category.system.toString())) {
            viewHolder.category = Category.system.toString();
            viewHolder.personalMsg.setVisibility(View.GONE);
            viewHolder.systemMsg.setVisibility(View.VISIBLE);
            viewHolder.commentsMsg.setVisibility(View.GONE);
            viewHolder.systemTitle.setText(title);
            viewHolder.systemTime.setText(time);
            viewHolder.systemMsgDesc.setText(msgDesc);
        } else if (category.equals(Category.comments.toString())) {
            String userName = json.optString("userName");
            String phoneNumber = json.optString("phoneNumber");
            viewHolder.category = Category.comments.toString();
            viewHolder.personalMsg.setVisibility(View.GONE);
            viewHolder.systemMsg.setVisibility(View.GONE);
            viewHolder.commentsMsg.setVisibility(View.VISIBLE);
            viewHolder.comments_time.setText(time);
            viewHolder.comments_Name.setText(userName);
            viewHolder.comments_PhoneNumber.setText(phoneNumber);
            viewHolder.comments_msgDesc.setText(msgDesc);
        }
        if (viewHolder.read) setTextColor(viewHolder);
    }
    public void doItemClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (!viewHolder.read) {
            setTextColor(viewHolder);
            if (viewHolder.category.equals(Category.personal.toString()))
                setReadMessageState(viewHolder.category, viewHolder.msgId);
        }
        Intent intent = new Intent(this, MessageDetailActivity.class);
        intent.putExtra("catagory", category);
        intent.putExtra("msgId", viewHolder.msgId);
        if (category.equals(Category.comments.toString())) {
            intent.putExtra("title", itemHolder.get(viewHolder.position).optString("userName") + "     " + itemHolder.get(viewHolder.position).optString("phoneNumber"));
        } else {
            intent.putExtra("title", itemHolder.get(viewHolder.position).optString("title"));
        }
        startActivity(intent);
    }

    private void setTextColor(ViewHolder viewHolder) {
        if (viewHolder.category.equals(Category.personal.toString())) {
            viewHolder.personTitle.setTextColor(getResources().getColor(R.color.textColor));
            viewHolder.personTime.setTextColor(getResources().getColor(R.color.textColor));
        } else {
            viewHolder.systemTitle.setTextColor(getResources().getColor(R.color.textColor));
            viewHolder.systemTime.setTextColor(getResources().getColor(R.color.textColor));
        }
    }


    private void setReadMessageState(final String catagoryType, final String msgId) {

        new BaseTask<String, String, Boolean>(this, "正在加载...") {

            @Override
            protected Boolean doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().readmessage(getApplicationContext(), catagoryType, msgId);
            }

            @Override
            protected void onSuccess(Boolean result) {

            }

        }.execute("");

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
            if (source.equals("jdyh")) {
                drawable = MessageActivity.this.getResources().getDrawable(R.drawable.jdyh);
            } else if (source.equals("xpsx")) {
                drawable = MessageActivity.this.getResources().getDrawable(R.drawable.xpsx);
            } else if (source.equals("czcx")) {
                drawable = MessageActivity.this.getResources().getDrawable(R.drawable.czcx);
            }

            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            return drawable;
        }
    };
}
