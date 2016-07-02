package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.view.AppleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/6/15.
 */
public class PartnerProfit extends Activity {

    private static final String[] yearList = {"2015","2016"};//,"2016","2017","2018","2019","2020"
    private static final String[] mouthList = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    private static enum Type {
        all,time;
    }
    String type = "all",userId="";
    private int yearInt = 2015,mouthInt=1 ;
    private String months;
    private Button year,mouth,search_all;
    private AppleListView listView;
    List<JSONObject> itemHolder = new ArrayList<JSONObject>();
    List<JSONObject> partnerListHolder = new ArrayList<JSONObject>();
    ListAdapter lvAdapter;
    int pageindex = 1;
    private RelativeLayout noneView;
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_profit);
        year = (Button)findViewById(R.id.year);
        mouth = (Button)findViewById(R.id.mouth);
        search_all = (Button)findViewById(R.id.search_all);
        listView = (AppleListView) findViewById(R.id.listView);
        noneView = (RelativeLayout) findViewById(R.id.noneView);
        lvAdapter = new ListAdapter(getBaseContext());
        listView.setAdapter(lvAdapter);

        listView.setonRefreshListener(new AppleListView.OnRefreshListener() {
            public void onRefresh() {
                pageindex = 1;
                refresh();
            }
        });
        listView.setPrevButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageindex > 1) {
                    pageindex--;
                }
                refresh();
                listView.setSelection(0);
            }
        });
        listView.setNextButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex++;
                refresh();
                listView.setSelection(0);
            }
        });
        listView.hidePrevButton();
        listView.hideNextButton();
        yearInt = Calendar.getInstance().get(Calendar.YEAR);
        mouthInt = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (mouthInt<10){
            months="0"+String.valueOf(mouthInt);
        }else {
            months=String.valueOf(mouthInt);
        }
        year.setText(String.valueOf(yearInt));
        mouth.setText("0"+String.valueOf(mouthInt));
        updateDate();
        refresh();
    }

    private void updateDate(){
            date = String.valueOf(yearInt)+"-0"+String.valueOf(mouthInt);
    }

    public void doYear(View view){
        new AlertDialog.Builder(this).setItems(yearList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yearInt = Integer.valueOf(yearList[which]);
                year.setText(yearList[which]);
                updateDate();
                refresh();
            }
        }).show();
    }

    public void doMouth(View view){
        new AlertDialog.Builder(this).setItems(mouthList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mouthInt = which+1;
                if (mouthInt<10){
                    months="0"+String.valueOf(mouthInt);
                }else {
                    months=String.valueOf(mouthInt);
                }
                mouth.setText(mouthList[which]);
                updateDate();
                refresh();
            }
        }).show();
    }

    public void doAll(View view){
        int length = partnerListHolder.size();
        String[] partnerArray = new String[length];
        for(int i=0;i<length;i++){
            partnerArray[i] = partnerListHolder.get(i).optString("userName");
        }
        new AlertDialog.Builder(this).setItems(partnerArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userId = partnerListHolder.get(which).optString("userId");
                search_all.setText(partnerListHolder.get(which).optString("userName"));
                refresh();
                dialog.dismiss();
            }
        }).show();

    }

    public void doSearch(View view){

        refresh();
    }

    private void refresh() {

        new BaseTask<String, String, JSONObject>(this, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().myPartnerList(getApplicationContext(), type, yearInt, months,userId,pageindex );
            }

            @Override
            protected void onSuccess(JSONObject result) {
                JSONArray array = null;
                JSONArray partnerList = null;
                List<JSONObject> list = new ArrayList<JSONObject>();
                int prePage = 1,nextPage = 1;
                try {
                    partnerList = result.getJSONArray("partnerList");
                    array = result.getJSONArray("list");
                    prePage = result.getInt("prePage");
                    nextPage = result.getInt("nextPage");
                    int length1 = partnerList.length();
                    partnerListHolder.clear();
                    for(int i=0; i<length1; i++) {
                        partnerListHolder.add(partnerList.getJSONObject(i));
                    }
                    for (int i = 0; i < array.length(); i++) {
                        list.add(array.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonPage jsonPage = new JsonPage(list, pageindex,prePage ,nextPage);
                List<JSONObject> orderList = jsonPage.getJsonList();
                if(orderList.size() == 0) {
                    noneView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    return;
                } else {
                    noneView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                if(jsonPage.isHasPrevPage() || jsonPage.isHasNextPage()) {
                    if (jsonPage.isHasPrevPage()) {
                        listView.enablePrevButton();
                    } else {
                        listView.disablePrevButton();
                    }
                    if (jsonPage.isHasNextPage()) {
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
                view = inflater.inflate(R.layout.partner_detail_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.partner = (TextView) view.findViewById(R.id.partner);
                viewHolder.profit = (TextView) view.findViewById(R.id.profit);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.position = position;
            viewHolder.userId = itemHolder.get(position).optString("userId");
            viewHolder.partnerName = itemHolder.get(position).optString("partner");
            viewHolder.partner.setText(itemHolder.get(position).optString("partner"));
            viewHolder.profit.setText(itemHolder.get(position).optString("profit"));
            return view;
        }
    }

    class ViewHolder {
        int position;
        String userId;
        String partnerName;
        TextView partner;
        TextView profit;
    }

    public void doPartnerItemClick(View view){
        ViewHolder holder = (ViewHolder) view.getTag();
        Intent i = new Intent(this, ProfitDetailActivity.class);
        i.putExtra("type", MyProfitActivity.ProfitDetailType.partner.toString());
        i.putExtra("userId",holder.userId);
        i.putExtra("partnerName",holder.partnerName);
        i.putExtra("date",date);
        startActivity(i);

    }
    public void doBack(View v) {
        onBackPressed();
    }
}
