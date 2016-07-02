package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by ct on 2015/8/28.
 */
public class WelcomeViewActivity extends Activity {
    private ViewPager viewPager;
    private LinearLayout pointLayout;
    private ArrayList<ImageView> list;
    private int count = 0;//the count of imageView
    private MyViewPagerAdapter adapter;
    private ImageView lastPoint;//记录当前选中点
    private Button JumpButton;
    private int currentItem = 0;
    private int jumptime = 3;
    private int onPageScrollState = 0;//0代表没有手指在滑动viewPager
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Log.i("ct", "handleMessage=========currentItem=" + currentItem);
            switch (msg.what) {
                case 0:
                    if (onPageScrollState == 0) {
                        if (currentItem >= list.size() - 1 || currentItem == -1) {
                            //跳转操作
                            Log.i("ct", "currentItem==============end");
                            currentItem = -1;
                         //   jumpToMain();
                        } else {
                            viewPager.setCurrentItem(++currentItem);
                        }
                    }
                    break;
                case 1:
                    if (jumptime > 0) {
                        JumpButton.setText("跳过"+jumptime + "S");
                        jumptime--;
                    } else {
                        jumpToMain();
                        finish();
                    }
                    break;
            }

        }

    };

    private void jumpToMain() {//
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcomeview);
        viewPager = (ViewPager) findViewById(R.id.wc_viewPager);
        pointLayout = (LinearLayout) findViewById(R.id.my_point);
        list = new ArrayList<ImageView>();
        JumpButton = (Button) findViewById(R.id.bt_bottom);
        adapter = new MyViewPagerAdapter();
        initCount();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int px = 0;
            int sx = 0;
            int time = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                px = positionOffsetPixels - px;
                Log.i("ct", "onPageScrolled==========onPageScrolled=vtime=" + time);
                if (position == list.size() - 1 && ((sx + px) == 0 || (sx - px) == 0) && time >= 1) {
                    // Log.i("ct", "onPageScroll=========jumpToMain==");
            /*        jumpToMain();*/
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                //设置匹配点的状态显示
                pointLayout.getChildAt(position).setEnabled(false);
                lastPoint.setEnabled(true);
                lastPoint = (ImageView) pointLayout.getChildAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("ct", "onPageScrollStateChanged==========state==" + state);
                sx = px;
                time++;
                onPageScrollState = state;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    //init the count
    private void initCount() {
        list.clear();
        new BaseTask<String, String, JSONObject>(this) {
            private ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                this.dialog = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected void onSuccess(JSONObject result) {

                count = Integer.parseInt(result.optString("count"));
                Log.i("ct", "count==============" + count);

                if (count > 0) {
                    JSONArray jsonArray = result.optJSONArray("data");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        try {
                            JSONObject js = jsonArray.getJSONObject(i);
                            String url = js.optString("uri");
                            Log.i("ct", "url==============" + url);
                            ImageView imageView = new ImageView(WelcomeViewActivity.this);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_CENTER
                            // imageView.setImageResource(R.drawable.dianxin);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                            imageView.setTag(url);
                            list.add(imageView);

                            //初始化图片对应的小点    小点实际上是imageview。根据不同的图片位置改变 其背景颜色 自己在xml中画该图片
                            ImageView point = new ImageView(WelcomeViewActivity.this);

                            //设置小点的布局形式             为了设置小点件的间距
                            LinearLayout.LayoutParams pointlayout = new LinearLayout.LayoutParams(20, 20);//指定宽高5 还有其他值如：包裹内容为-2
                            pointlayout.rightMargin = 10;
                            point.setLayoutParams(pointlayout);
                            //设置点的背景为selector  selector中有两种图片
                            point.setBackgroundResource(R.drawable.point_selector);
                            if (i == 0) {
                                //设置enable 为true时对用的图片
                                point.setEnabled(false);
                                //记录当前  灰色的点
                                lastPoint = point;
                            } else {
                                point.setEnabled(true);
                            }
                            //将小点加入到pointGroup的layout中去
                            pointLayout.addView(point);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //获取数据成功。。。。。。。。。。。。。
                    adapter.notifyDataSetChanged();
                    //viewPager.setCurrentItem(0);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (currentItem != -1) {
                                try {
                                    Thread.currentThread().sleep(2000);
                                    handler.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    startMain();
                } else {
                   jumpToMain();
                }
            }

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().welcomePage(context);
            }


            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                dialog.dismiss();
            }
        }.execute();
    }

    public void startMain() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (jumptime >= 0) {
                        Thread.currentThread().sleep(1000);
                        handler.sendEmptyMessage(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        });
        thread.start();

    }

    class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            ImageView imageView = list.get(position);

            AsynImageLoaderWithSDCache imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), WelcomeViewActivity.this);
            imageLoader.showImageAsyn(new SoftReference<ImageView>(imageView),
                    (String) imageView.getTag(),
                    R.drawable.dianxin);
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
            object = null;
        }
    }
}
