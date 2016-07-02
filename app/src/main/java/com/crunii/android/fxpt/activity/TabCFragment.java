package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.business.AdBanner;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyLog;
import com.crunii.android.fxpt.util.MyToast;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TabCFragment extends Fragment {
    public static boolean needRefresh = true;

    private Context mContext;
    private ViewPager adBannerVp;
    private int currentItem = 0; // 当前图片的索引号
    private List<ImageView> dots; // 图片切换布局
    private AsynImageLoaderWithSDCache imageLoader;
    private List<ImageView> imageViews;
    private List<Map> tabList;
    private ImagePagerAdapter adapter;
    private LinearLayout linearLayout;

    private BadgeView unreadBadge, distributeOrderBadge;
    private View menu1, menu2, menu3, menu4, menu5, menu6, menu7, menu8;
    private ImageView menu1_img, menu2_img, menu3_img, menu4_img, menu5_img, menu6_img, menu7_img, menu8_img;
    private TextView menu1_text, menu2_text, menu3_text, menu4_text, menu5_text, menu6_text, menu7_text, menu8_text;
    private ImageView ad1, ad2;
    String imsi = "";
    private OnAdClickListner onAdClickListner = new OnAdClickListner();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取imsi码 20150821
        TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        imsi = mTelephonyMgr.getSubscriberId();
        mContext = getActivity();
        adapter = new ImagePagerAdapter();
        dots = new ArrayList<ImageView>();
        imageViews = new ArrayList<ImageView>();
        imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), mContext);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_c, container, false);
        init(view);
        refreshtab();
        return view;
    }

    private void init(View view) {

        //顶部广告
        adBannerVp = (ViewPager) view.findViewById(R.id.home_ad_vp);
        adBannerVp.setAdapter(adapter);
        adBannerVp.setOnPageChangeListener(new MyPageChangeListener());
        linearLayout = (LinearLayout) view.findViewById(R.id.home_ad_index_ll);

        //中间的8个菜单
        menu1 = view.findViewById(R.id.menu1);
        menu2 = view.findViewById(R.id.menu2);
        menu3 = view.findViewById(R.id.menu3);
        menu4 = view.findViewById(R.id.menu4);
        menu5 = view.findViewById(R.id.menu5);
        menu6 = view.findViewById(R.id.menu6);
        menu7 = view.findViewById(R.id.menu7);
        menu8 = view.findViewById(R.id.menu8);
        menu1_img = (ImageView) view.findViewById(R.id.menu1_img);
        menu2_img = (ImageView) view.findViewById(R.id.menu2_img);
        menu3_img = (ImageView) view.findViewById(R.id.menu3_img);
        menu4_img = (ImageView) view.findViewById(R.id.menu4_img);
        menu5_img = (ImageView) view.findViewById(R.id.menu5_img);
        menu6_img = (ImageView) view.findViewById(R.id.menu6_img);
        menu7_img = (ImageView) view.findViewById(R.id.menu7_img);
        menu8_img = (ImageView) view.findViewById(R.id.menu8_img);
        menu1_text = (TextView) view.findViewById(R.id.menu1_text);
        menu2_text = (TextView) view.findViewById(R.id.menu2_text);
        menu3_text = (TextView) view.findViewById(R.id.menu3_text);
        menu4_text = (TextView) view.findViewById(R.id.menu4_text);
        menu5_text = (TextView) view.findViewById(R.id.menu5_text);
        menu6_text = (TextView) view.findViewById(R.id.menu6_text);
        menu7_text = (TextView) view.findViewById(R.id.menu7_text);
        menu8_text = (TextView) view.findViewById(R.id.menu8_text);


        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Toast.makeText(mContext, "升级中...", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), MyProfitActivity.class);
                 startActivity(i);
               // MyToast.show("建设中...", mContext);
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), OrderListActivity.class);
                Intent i = new Intent(getActivity(), OrderActivity.class);
                startActivity(i);
            }
        });
        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Toast.makeText(mContext, "建设中...", Toast.LENGTH_LONG).show();
//				Intent intent = new Intent(getActivity(), MobileReaderActivity.class);
//				startActivity(intent);

                if (unreadBadge != null) {
                    unreadBadge.hide();
                }

                Intent i = new Intent(getActivity(), MessageActivity.class);
                startActivity(i);

            }
        });
        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                Intent i = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(i);
//                startActivityForResult(i, PERSONALINFO_REQUEST);

            }
        });
        menu5.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (distributeOrderBadge != null) {
                    distributeOrderBadge.hide();
                }

                Intent i = new Intent(getActivity(), DistributeOrderActivity.class);
                startActivity(i);
//                new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {
//
//                    @Override
//                    protected JSONObject doInBack(String... arg0) throws HttpException,
//                            IOException, TaskResultException {
//                        return CRApplication.getApp().isSupportTranscribe(getActivity());
//                    }
//
//                    @Override
//                    protected void onSuccess(JSONObject result) {
//                        boolean isSupport = result.optBoolean("isSupport");
//                        if (isSupport) {
//                            Intent i = new Intent(getActivity(), SpeedRegisterActivity.class);
//                            startActivity(i);
//                        } else {
//                            String reason = result.optString("reason");
//                            Toast.makeText(getActivity(), reason, Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }.execute("");
            }
        });

        menu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), InfoQueryActivity.class);
                startActivity(i);


//			    new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {
//
//				  @Override
//				  protected JSONObject doInBack(String... arg0) throws HttpException,
//					    IOException, TaskResultException {
//					return CRApplication.getApp().myNetStroe(getActivity());
//				  }
//
//				  @Override
//				  protected void onSuccess(JSONObject result) {
//					boolean isFlag = result.optBoolean("isFlag");
//					if(isFlag){
//					    　Intent intent = new Intent();
//					    　　intent.setAction("android.intent.action.VIEW");
//					    　　Uri content_url = Uri.parse("http://www.cnblogs.com");
//					    　　intent.setData(content_url);
//					    　　startActivity(intent);
//					    Intent i = new Intent(getActivity(), SpeedRegisterActivity.class);
//					    startActivity(i);
//					}else{
//					    String reason = result.optString("desc");
//					    Toast.makeText(getActivity(),reason,Toast.LENGTH_SHORT).show();
//					}
//
//				  }
//			    }.execute("");


            }
        });
        menu7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(getActivity(), StockMarketing.class);
                startActivity(intent);*/
                MyToast.show("建设中...", mContext);


//                if(checkAPP(getActivity(),"com.ztesoft.mobile.trunk")){
//                    //ComponentName componentName = new ComponentName("com.cdt.nadyboy.tfcbg","com.cdt.nadyboy.tfcbg.WelcomeActivity");
//                    ComponentName componentName = new ComponentName("com.ztesoft.mobile.trunk","com.ztesoft.mobile.trunk.SplashActivity");
//                    try {
//                        Intent intent = new Intent();
//                        intent.putExtra("phone", CRApplication.getPhone(getActivity()));
//                        intent.putExtra("token","B9ED8D9D151F9169688E4EB29F5AE4A8");
//                        intent.setComponent(componentName);
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "单点登录出错了。。。",Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    Uri uri = Uri.parse("http://cdt.cq.189.cn:8080/iDown/iSale_AndroidMobile.apk");
//                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(downloadIntent);
//                }

            }
        });
        menu8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//2016 05 月底版本，屏蔽其他应用功能。
                /*Intent i = new Intent(getActivity(), OtherActivity.class);
                startActivity(i);*/
                MyToast.show("建设中...", mContext);
            }
        });


        //底部广告区域
        ad1 = (ImageView) view.findViewById(R.id.ad1);
        ad2 = (ImageView) view.findViewById(R.id.ad2);
        //根据屏幕分辨率不同缩放图片，仅支持普通分辨率和高清分辨率，暂不支持低分辨率以及平板
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels == 480) { //普通分辨率
            ad1.setLayoutParams(new RelativeLayout.LayoutParams(getPixels(156), getPixels(106)));
            ad2.setLayoutParams(new RelativeLayout.LayoutParams(getPixels(156), getPixels(106)));
        } else { //默认为高清分辨率
            ad1.setLayoutParams(new RelativeLayout.LayoutParams(getPixels(180), getPixels(120)));
            ad2.setLayoutParams(new RelativeLayout.LayoutParams(getPixels(180), getPixels(120)));
        }
    }

    //检测是否安装了成品卡返档的APP
    public static boolean checkAPP(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private int getPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                r.getDisplayMetrics());
        return px;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
    }


    private static final int CHANGE_VP = 0;
    private Timer mTimer;

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = CHANGE_VP;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_VP:
                    if (imageViews != null) {
                        if (imageViews.size() != 0) {
                            currentItem = (currentItem + 1) % imageViews.size();
                            adBannerVp.setCurrentItem(currentItem);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTimer == null) {   //保证只有一个 定时任务
            mTimer = new Timer(true);
            mTimer.schedule(new MyTask(), 10000, 10000);
        }
        super.onResume();
        //切换用户刷新登陆名
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refreshtab() {
        Map<String, String> map = new HashMap<String, String>();
        HttpTool.sendPost(getActivity(), Constant.CTX_PATH + "mTableImage", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                Map result = (Map) record;
                tabList = (List) result.get("TableList");
                if (tabList.size() != 0) {
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu1_img), (String) tabList.get(0).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu2_img), (String) tabList.get(1).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu3_img), (String) tabList.get(2).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu4_img), (String) tabList.get(3).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu5_img), (String) tabList.get(4).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu6_img), (String) tabList.get(5).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu7_img), (String) tabList.get(6).get("tableImageUrl"), R.drawable.mode_loading);
                    imageLoader.showImageAsyn(new SoftReference<Object>(menu8_img), (String) tabList.get(7).get("tableImageUrl"), R.drawable.mode_loading);
                    menu1_text.setText(tabList.get(0).get("tableText").toString());
                    menu2_text.setText(tabList.get(1).get("tableText").toString());
                    menu3_text.setText(tabList.get(2).get("tableText").toString());
                    menu4_text.setText(tabList.get(3).get("tableText").toString());
                    menu5_text.setText(tabList.get(4).get("tableText").toString());
                    menu6_text.setText(tabList.get(5).get("tableText").toString());
                    menu7_text.setText(tabList.get(6).get("tableText").toString());
                    menu8_text.setText(tabList.get(7).get("tableText").toString());
                }
            }
        });

    }

    public void refresh() {

        new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {
            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException, com.crunii.android.base.exception.HttpException {
                MyLog.i("ct","refresh doInback...");
                return CRApplication.getApp().home(getActivity(), imsi);
            }

            @Override
            protected void onSuccess(JSONObject result) {
                needRefresh = false;
                try {
                    //顶部广告
                    JSONArray jsonArray = result.getJSONArray("topAd");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AdBanner adBanner = new AdBanner(jsonObject);
                        ImageView imageView = new ImageView(mContext);
                        imageView.setScaleType(ScaleType.FIT_CENTER);
                        imageView.setImageResource(R.drawable.adbanner);
                        imageView.setOnClickListener(onAdClickListner);
                        imageView.setTag(adBanner);
                        imageViews.add(imageView);
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
                        linearLayout.addView(dot);
                    }
                    adapter.notifyDataSetChanged();
                    //底部广告
                    JSONArray buttomAd = result.getJSONArray("buttomAd");
                    JSONObject jsonObject = buttomAd.getJSONObject(0);
                    JSONObject jsonObject1 = buttomAd.getJSONObject(1);
                    AdBanner buttomleft = new AdBanner(jsonObject);
                    AdBanner buttomright = new AdBanner(jsonObject1);

                    ad1.setTag(buttomleft);
                    imageLoader.showImageAsyn(new SoftReference<ImageView>(ad1), ((AdBanner) ad1.getTag()).getImage(), R.drawable.ad1);
                    ad1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String bgImg = ((AdBanner) ad1.getTag()).getBgPicPath();
                            String desc = ((AdBanner) ad1.getTag()).getAdvDesc();
                            if (!bgImg.equals("")) {
                                Intent intent = new Intent(getActivity(), AdBannerShow.class);
                                intent.putExtra("bgImg", bgImg);
                                intent.putExtra("desc", desc);
                                startActivity(intent);
                            }
                        }
                    });
                    ad2.setTag(buttomright);
                    imageLoader.showImageAsyn(new SoftReference<ImageView>(ad2), ((AdBanner) ad2.getTag()).getImage(), R.drawable.ad2);
                    ad2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String bgImg = ((AdBanner) ad2.getTag()).getBgPicPath();
                            String desc = ((AdBanner) ad2.getTag()).getAdvDesc();
                            if (!bgImg.equals("")) {
                                Intent intent = new Intent(getActivity(), AdBannerShow.class);
                                intent.putExtra("bgImg", bgImg);
                                intent.putExtra("desc", desc);
                                startActivity(intent);
                            }
                        }
                    });

                    //未读消息
                    int unreadMessage = result.optInt("unreadMsg");
                    View menu3 = getView().findViewById(R.id.menu3_img);
                    unreadBadge = new BadgeView(mContext, menu3);
                    unreadBadge.setBadgeMargin(0);
                    unreadBadge.setText(unreadMessage + "");
                    if (unreadMessage > 0) {
                        unreadBadge.show();
                    } else {
                        unreadBadge.hide();
                    }

                    int countNewDistributeOrder = result.optInt("countNewDistributeOrder");
                    View menu5 = getView().findViewById(R.id.menu5_img);
                    distributeOrderBadge = new BadgeView(mContext, menu5);
                    distributeOrderBadge.setBadgeMargin(0);
                    distributeOrderBadge.setText(countNewDistributeOrder + "");
                    if (countNewDistributeOrder > 0) {
                        distributeOrderBadge.show();
                    } else {
                        distributeOrderBadge.hide();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute("");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        MyLog.i("ct", "TabcFragment  setUserVisibleHint");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) { //添加到onStart中执行
            if (needRefresh) {
                MyLog.i("ct", "TabcFragment  setUserVisibleHint refresh");
                //refresh();
            }
        }
    }

    private class MyPageChangeListener implements OnPageChangeListener {
        private int oldPosition = 0;

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
            currentItem = position;
            dots.get(oldPosition).setImageResource(R.drawable.dot2);
            dots.get(position).setImageResource(R.drawable.dot1);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(imageViews.get(position));
            ImageView imageView = imageViews.get(position);
            imageLoader.showImageAsyn(new SoftReference<ImageView>(imageView),
                    ((AdBanner) imageView.getTag()).getImage(),
                    R.drawable.adbanner);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdBanner adBanner = (AdBanner) v.getTag();
                    String bgImg = adBanner.getBgPicPath();
                    String desc = adBanner.getAdvDesc();
                    if (!bgImg.equals("")) {
                        Intent intent = new Intent(getActivity(), AdBannerShow.class);
                        intent.putExtra("bgImg", bgImg);
                        intent.putExtra("desc", desc);
                        startActivity(intent);
                    }

                    //  startActivity(intent);
                }
            });
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
            ((ViewPager) container).removeView(imageViews.get(position));
        }

    }

    private class OnAdClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AdBanner banner = (AdBanner) v.getTag();
            /*String target = banner.getTarget();

            if (target.startsWith("about:blank")) { //不可点击
                Log.d("33333333333333","为空");
                return;
            }

            if (target.startsWith("http://")) { //webview
                Log.d("5555555555555555","");
                Intent i = new Intent(getActivity(), WebviewActivity.class);
                i.putExtra("url", target);
                startActivity(i);
            }

            if (target.startsWith("activity://")) { //activity
                Log.d("666666666666","");
                Intent intent = new Intent();

                String[] array1 = target.split("\\?");
                String[] array2 = array1[0].split("//");
                String activity = array2[1];
                intent.setClassName(getActivity(), activity);

                String[] array3 = array1[1].split("&");
                for (int i = 0; i < array3.length; i++) {
                    String[] array4 = array3[i].split("=");
                    String paramName = array4[0];
                    String paramValue = array4[1];
                    intent.putExtra(paramName, paramValue);
                }
                startActivity(intent);*/

        }
    }


}
