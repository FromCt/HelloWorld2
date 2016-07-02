package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.BaseListViewAdapter;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.SetValue;
import com.crunii.android.fxpt.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐简介
 * <p/>
 * Created by ct on 16/1/15.
 */
public class ServiceIntroduction extends BaseActivity {

    private ListView listView, listView2;
    private String[] strs = {"移动4G套餐", "宽带套餐", "天翼高清套餐", "移动成品卡", "融合套餐", "营销政策"};
    private ArrayList<String> result;
    private List<Map> list;
    private List<Map> list2;

    private BaseListViewAdapter<Map> myAdapterLeft = new BaseListViewAdapter<Map>(this, R.layout.service_introduction_item) {


        @Override
        public void getBaseView(Map data, HolderView holderView, ViewGroup parent, int position) {
            TextView textView = (TextView) holderView.findViewById(R.id.sii_textView);
            LinearLayout linearLayout = (LinearLayout) holderView.findViewById(R.id.linearLayout0);
            SetValue.setText(textView, (String) data.get("packageMenu"));
            if (selectedPosition==position){
                textView.setTextColor(ServiceIntroduction.this.getResources().getColor(R.color.wallet_holo_blue_light));
                linearLayout.setBackgroundColor(ServiceIntroduction.this.getResources().getColor(R.color.transparent));
                list2 = (List<Map>) list.get(position).get("list");
                myAdapterRight.refreshData(list2);
                myAdapterRight.notifyDataSetChanged();
                selectDefultRight();
                listView2.setAdapter(myAdapterRight);
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myAdapterRight.setSelectedPosition(position);
                        myAdapterRight.notifyDataSetChanged();
                        String id2 = (String) list2.get(position).get("packageId");
                        Intent intent = new Intent(ServiceIntroduction.this, ServiceIntroductionWebViewActivity.class);
                        intent.putExtra("id", id2);
                        startActivity(intent);
                    }
                });

            }else{
                textView.setTextColor(Color.BLACK);
                linearLayout.setBackgroundColor(ServiceIntroduction.this.getResources().getColor(R.color.white));
            }
        }
    };
    private BaseListViewAdapter<Map> myAdapterRight=new BaseListViewAdapter<Map>(this,R.layout.service_introduction_content_listview) {
        @Override
        public void getBaseView(Map data, HolderView holderView, ViewGroup parent, int position) {

            TextView textView= (TextView) holderView.findViewById(R.id.sicl_textView);
            LinearLayout linearLayout0= (LinearLayout) holderView.findViewById(R.id.linearLayout0);
            SetValue.setText(textView, (String) data.get("packageTitle"));
            if (selectedPosition==position){
                textView.setTextColor(ServiceIntroduction.this.getResources().getColor(R.color.wallet_holo_blue_light));
                linearLayout0.setBackgroundResource(R.drawable.borderblue);
            }else{
                textView.setTextColor(Color.BLACK);
                linearLayout0.setBackgroundResource(R.drawable.border);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_introduction);
        listView = (ListView) findViewById(R.id.si_listView);
        listView2 = (ListView) findViewById(R.id.si_listView2);
        HashMap<String, String> postparams = new HashMap<String, String>();

        sendPost(Constant.URL.PACKAGEINTRODUCE, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                list = (List<Map>) record.get("goalList");
                myAdapterLeft.refreshData(list);
            }
        });

        listView.setDividerHeight(0);//设置listView 分割线高度为0
        listView2.setDividerHeight(0);
        //add();
        selectDefult();

        listView.setAdapter(myAdapterLeft);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                myAdapterLeft.setSelectedPosition(position);
                myAdapterLeft.notifyDataSetChanged();
            }
        });
    }

    private void selectDefult(){
        myAdapterLeft.setSelectedPosition(0);
        myAdapterLeft.notifyDataSetInvalidated();
    }
    private void selectDefultRight(){
        myAdapterRight.setSelectedPosition(0);
        myAdapterRight.notifyDataSetInvalidated();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
