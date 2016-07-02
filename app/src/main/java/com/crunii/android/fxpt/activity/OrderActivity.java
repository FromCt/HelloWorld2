package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/14.
 */
public class OrderActivity extends FragmentActivity {

    final static int ORDER_A_KEYWORD_REQUEST = 1;
    final static int ORDER_B_KEYWORD_REQUEST = 2;
    public List<Fragment> fragmentList = new ArrayList<Fragment>();
    FragmentManager fragmentManager;
    FragmentPagerAdapter fragmentPagerAdapter;
    public ViewPager viewPager;
    private String keyword = "";
    private ImageView iv_keyword;
    private TextView title;
    RadioButton rb_generalOrder, rb_netstore;
    public int userLevel = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        fragmentManager = this.getSupportFragmentManager();
        fragmentPagerAdapter = new MyPagerAdapter(fragmentManager);
        fragmentList.add(new OrderAFragment());
        fragmentList.add(new OrderCFragment());
        iv_keyword = (ImageView) findViewById(R.id.iv_keyword);
        title = (TextView) findViewById(R.id.title);
        rb_generalOrder = (RadioButton) findViewById(R.id.rb_generalOrder);
        rb_netstore = (RadioButton) findViewById(R.id.rb_netstore);
        viewPager = (MainViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(fragmentPagerAdapter);

        //默认值
        viewPager.setCurrentItem(1); //默认为第一个tab
        rb_netstore.setChecked(true); //默认选中第一个tab

        ((RadioGroup) findViewById(R.id.rg_order_type)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_generalOrder:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_netstore:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });

    }

    public int getUserLevel() {
        return userLevel;
    }

    public void doGeneral(View view) {

    }

    private void doGeneralShow() {
        viewPager.setCurrentItem(0);
        iv_keyword.setVisibility(View.VISIBLE);
        if (keyword.equals("")) {
            title.setText("电商分销");
        } else {
            title.setText(keyword);
        }
    }

    public void doTranscribe(View view) {
        viewPager.setCurrentItem(1);
        iv_keyword.setVisibility(View.INVISIBLE);
        title.setText("我的订单");
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }
    }

    ;


    public void doBack(View v) {
        onBackPressed();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ORDER_A_KEYWORD_REQUEST:
                if (resultCode == RESULT_OK) {
                    keyword = data.getExtras().getString("keyword");
                    if (!NullUtils.isEmpty(keyword)) {
                        title.setText(keyword);
                    } else {
                        keyword = "";
                        title.setText("电商分销");
                    }
                    ((OrderAFragment) fragmentList.get(0)).doRefresh(keyword);
                }
                break;

            case ORDER_B_KEYWORD_REQUEST:
                if (resultCode == RESULT_OK) {
                    keyword = data.getExtras().getString("keyword");
                    if (!NullUtils.isEmpty(keyword)) {
                        title.setText(keyword);
                    } else {
                        keyword = "";
                        title.setText("电商分销");
                    }
                    ((OrderAFragment) fragmentList.get(1)).doRefresh(keyword);
                }
                break;


        }
    }

}
