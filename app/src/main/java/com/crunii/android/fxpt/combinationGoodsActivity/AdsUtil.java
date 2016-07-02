package com.crunii.android.fxpt.combinationGoodsActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.AdBanner;
import com.crunii.android.fxpt.util.ImageHelper;
import com.crunii.android.fxpt.util.MyAsynImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedingsnail on 15/12/7.
 */
public class AdsUtil {

    private int currentItem = 0; // 当前图片的索引号
    private MyAsynImageLoader imageLoader;
    private Context mContext;
    String lastOpenedNetworkImage = "";

    public AdsUtil(Context mContext){
        this.mContext = mContext;
        imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), mContext);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            ImageView imageView = imageViews.get(position);
            imageLoader.showImageAsyn(new SoftReference<ImageView>(imageView), ((AdBanner) imageView.getTag()).getImage(), R.drawable.adbanner2);

            return imageView;
        }

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int size = imageViews.size();
            if(position < size) {
                container.removeView(imageViews.get(position));
            }
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
//        List<ImageView> dots;
//
//        public MyPageChangeListener(List<ImageView> dots){
//            this.dots = dots;
//        }

        private int oldPosition = 0;

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
            currentItem = position;

            dots.get(oldPosition).setImageResource(R.drawable.dot4);
            dots.get(position).setImageResource(R.drawable.dot3);

            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
    private ArrayList<ImageView> dots = new ArrayList<ImageView>();


    /**
     *
     * @param detailimg
     * @param adBannerVp  ViewPager
     * @param adBannerDots 点布局LinearLayout
     */
    public void updateTopAds(ArrayList<ProductDetailResultDemo.Detailimg> detailimg,ViewPager adBannerVp,LinearLayout adBannerDots) {

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        adBannerVp.setAdapter(adapter);
        adBannerVp.setOnPageChangeListener(new MyPageChangeListener());
        adBannerDots.removeAllViews();
        OnAdClickListner onAdClickListner = new OnAdClickListner();

        for (int i = 0; i < detailimg.size(); i++) {
            //添加图片
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(R.drawable.adbanner2);
            imageView.setOnClickListener(onAdClickListner);
            imageView.setTag(new AdBanner(detailimg.get(i).image));
            imageViews.add(imageView);

            //添加小圆点
            ImageView dot = new ImageView(mContext);
            if (i == 0) {
                dot.setImageResource(R.drawable.dot3);
            } else {
                dot.setImageResource(R.drawable.dot4);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 5, 0);
            dot.setLayoutParams(layoutParams);
            dots.add(dot);
            adBannerDots.addView(dot);
        }

        adapter.notifyDataSetChanged();

        currentItem = 0;
        adBannerVp.setCurrentItem(currentItem);
    }

    private class OnAdClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AdBanner banner = (AdBanner) v.getTag();
            String url = banner.getImage();

            //图片不一定会进入缓存，因此在这里总是先下载再打开
            openNetworkImage(url);

        }
    }

    private void openNetworkImage(final String url) {
        final String path = ImageHelper.getPath("temp.jpg");

        //缓存最近一次打开的图片
        if(url.equals(lastOpenedNetworkImage)) {
            openLocalImage(path);
            return;
        }

        new BaseTask<String, String, String>(mContext, "打开图片中...") {

            @Override
            protected String doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().download(url, path);
            }

            @Override
            protected void onSuccess(String result) {
                openLocalImage(path);
                //缓存最近一次打开的图片
                lastOpenedNetworkImage = url;
            }
        }.execute(url, path);

    }


    public void openLocalImage(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "image/*");
        mContext.startActivity(intent);
    }

}
