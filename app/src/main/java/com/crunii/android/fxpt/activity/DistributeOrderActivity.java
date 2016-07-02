package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.RadioButton;

import com.crunii.android.fxpt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedingsnail on 16/1/15.
 */
public class DistributeOrderActivity extends FragmentActivity {

    List<Fragment> list = new ArrayList<Fragment>();
    FragmentManager fragmentManager;
    MainViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribute_order);
        fragmentManager = getSupportFragmentManager();
        viewPager = (MainViewPager) findViewById(R.id.viewPager);
        list.add(new DistributeOrderFragmentA());
        list.add(new DistributeOrderFragmentB());
        viewPager.setAdapter(new MyViewPagerAdapter(fragmentManager));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        ((RadioButton) findViewById(R.id.radio1)).setChecked(true); //默认选中第一个tab
    }


    public void doRadio1(View view) {
        viewPager.setCurrentItem(0);
    }

    public void doRadio2(View view) {
        viewPager.setCurrentItem(1);
    }

    class MyViewPagerAdapter extends FragmentPagerAdapter {


        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
      @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode ==RESULT_OK) {
            ((DistributeOrderFragmentB) list.get(1)).onRefresh1();
        }

    }

    public void doBack(View v) {
        onBackPressed();
    }

}
