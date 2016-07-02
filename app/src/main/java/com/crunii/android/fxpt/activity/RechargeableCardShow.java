package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Administrator on 2015/3/25.
 */
public class RechargeableCardShow extends Activity {

    String orderId = "",goodsId="", payValue = "",requestRe="";
    TextView order, pay ,preExplainDesc;
    ListView cardList;
    LinearLayout top_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_show_old);

        orderId = getIntent().getExtras().getString("orderId");
        goodsId = getIntent().getExtras().getString("goodsId");
        payValue = getIntent().getExtras().getString("payValue");
        requestRe = getIntent().getExtras().getString("requestRe");//如果是订单列表过来的，隐藏top_head

        order = (TextView) findViewById(R.id.order);
        pay = (TextView) findViewById(R.id.pay);
        preExplainDesc = (TextView) findViewById(R.id.preExplainDesc);
        cardList = (ListView) findViewById(R.id.cardList);

        top_head = (LinearLayout)findViewById(R.id.top_head);
        if(requestRe != null && requestRe.equals(OrderAFragment.REQUEST_RESOURCE)){
            top_head.setVisibility(View.GONE);
        }
        requestCardList();
        initData();


    }

    private void requestCardList() {
        new BaseTask<String, String, JSONObject>(RechargeableCardShow.this, "数据请求中...") {

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().cardList(RechargeableCardShow.this, orderId,goodsId);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                show(jsonObject);
            }
        }.execute("");

    }

    private void initData() {
        order.setText(orderId);
        pay.setText("¥" + payValue);


    }

    private void show(JSONObject jsonObject) {
        String inchargeContent = jsonObject.optString("inchargeContent");
        preExplainDesc.setText(Html.fromHtml(inchargeContent));
        MyAdapter adapter = new MyAdapter(jsonObject.optJSONArray("dataList"));
        cardList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(cardList);
    }

    private class MyAdapter extends BaseAdapter {

        JSONArray list;

        public MyAdapter(JSONArray listData) {
            super();
            if (listData != null) {
                list = listData;
            } else {
                list = new JSONArray();
            }

        }

        @Override
        public int getCount() {
            return list.length();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(RechargeableCardShow.this, R.layout.activity_card_message, null);
                holder.orderNumber = (TextView) convertView.findViewById(R.id.orderNumber);
                holder.password = (TextView) convertView.findViewById(R.id.password);
                holder.config = (TextView) convertView.findViewById(R.id.config);
            } else {
                holder = (Holder) convertView.getTag();
            }

            showData(holder, list.optJSONObject(position));

            convertView.setTag(holder);
            return convertView;
        }
    }

    private void showData(Holder holder, JSONObject jsonObject) {
        String config = "";
        String cardPwd = jsonObject.optString("cardPwd");
        String need_deal = jsonObject.optString("need_deal");
        if (need_deal.equals("0")) {//-1直接展示 0：未处理 1：已处理
            config = "【数据配置中】";
            cardPwd = "**************";
        } else {
            config = "【该卡可使用】";
        }
        holder.orderNumber.setText(jsonObject.optString("cardNo"));
        holder.password.setText(cardPwd);
        holder.config.setText(config);
    }

    class Holder {
        TextView orderNumber;
        TextView password;
        TextView config;

    }

    public void doBack(View v) {
        onBackPressed();
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height += 5;//if without this statement,the listview will be a little short
        listView.setLayoutParams(params);
    }

}
