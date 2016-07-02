package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.PreAd;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedingsnail on 15/8/24.
 */
public class PreAdActivity extends Activity {

    private Context mContext;
    ViewPager ad_viewpager;
    List<ImageView> imageViewList ;
    List<ImageView> dots;
    private AsynImageLoaderWithSDCache imageLoader;
    MyPagerAdapter adapter ;
    Button bt_into;
    LinearLayout ll_dot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageViewList = new ArrayList<ImageView>();
        dots = new ArrayList<ImageView>();
        mContext = this;
        imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), mContext);
        adapter = new MyPagerAdapter();

        setContentView(R.layout.activity_pre_ad);
        bt_into = (Button) findViewById(R.id.bt_into);
        ll_dot = (LinearLayout) findViewById(R.id.ll_dot);
        ad_viewpager = (ViewPager) findViewById(R.id.ad_viewpager);


        bt_into.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,MainActivity.class));
                finish();
            }
        });
        ad_viewpager.setAdapter(adapter);
        ad_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == imageViewList.size() - 1) {
                    bt_into.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initData();


    }

    private void initData(){
        ImageView imageView1 = new ImageView(mContext);
        PreAd preAd1 = new PreAd("https://www.baidu.com/img/bdlogo.png","");
        imageView1.setTag(preAd1);
        imageViewList.add(imageView1);

        ImageView imageView2 = new ImageView(mContext);
        PreAd preAd2 = new PreAd("http://www.189.cn/image/189cnv2/indexv2/img_head/logo.png","");
        imageView2.setTag(preAd2);
        imageViewList.add(imageView2);

        ImageView imageView3 = new ImageView(mContext);
        PreAd preAd3 = new PreAd("http://image1.chinatelecom-ec.com/images/2015/6/8/1/00000000BA18766055144776980AE850692B2F51.png","");
        imageView3.setTag(preAd3);
        imageViewList.add(imageView3);
        adapter.notifyDataSetChanged();

        int dotSize = imageViewList.size();
        for(int i=0;i<dotSize;i++){
            ImageView dot = new ImageView(mContext);
            if (i == 0) {
                dot.setImageResource(R.drawable.dot1);
            } else {
                dot.setImageResource(R.drawable.dot2);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 5, 0);
            dot.setLayoutParams(layoutParams);
            dots.add(dot);
            ll_dot.addView(dot);

        }



    }



    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(imageViewList.get(position));
            ImageView imageView = imageViewList.get(position);
            imageLoader.showImageAsyn(new SoftReference<ImageView>(imageView),
                    ((PreAd) imageView.getTag()).getImage(),
                    R.drawable.adbanner);
            return imageView;
        }
    }





}
