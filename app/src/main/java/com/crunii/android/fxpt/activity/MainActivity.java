package com.crunii.android.fxpt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.task.DownloadTask;
import com.crunii.android.base.util.DownloadCallback;
import com.crunii.android.base.util.DownloadCallback.Callback;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.business.Version;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity implements Callback {
    Version serverVersion;
    final static int LOGIN_REQUEST = 1;
    final static int PERSONALINFO_REQUEST = 2;
    final static int WELCOME_OK = 3;
    final static int LOGIN_Choose_REQUEST = 4;

    public  boolean canBeShopManagement=false;

    private long mExitTime;

    public MainViewPager viewPager;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    public String hello = "hello ";
    private ImageView mTab1, mTab2, mTab3, mTab4, mTab5;
    private TextView mText1, mText2, mText3, mText4, mText5;
    private LinearLayout LayoutTab1, LayoutTab2, LayoutTab3, LayoutTab4, LayoutTab5;
    private int currIndex = 0;// 当前页卡编号
    FragmentManager fragmentManager;
    FragmentPagerAdapter fragmentPagerAdapter;
    String imsi = "";
    boolean isFirstFlag = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {//爱营销 单点登录
            String token = bundle.getString("token");
            String userId = bundle.getString("userId");
            String userName = bundle.getString("userName");
            String phone = bundle.getString("phone");
            MyLog.i("ct", "MainActivity  爱营销 单点登录  token===" + token + ",phone" + phone + ",userId=" + userId + ",userName=" + userName);
            //checkPermission(phone);
            if(token!=null){
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
                CRApplication.setToken(getApplicationContext(), token);
                CRApplication.setId(getApplicationContext(), userId);
                CRApplication.setName(getApplicationContext(), userName);
                CRApplication.setPhone(getApplicationContext(), phone);
            }
        }

        if (CRApplication.getToken(getApplicationContext()) == null) {
            MyLog.i("ct", "MainActivity  token=null 登录调用。 ");
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(i, LOGIN_REQUEST);
        }

        if(Constant.loginTimes == 1){//当第一次登录时到欢迎页面
            MyLog.i("ct", "MainActivity  当第一次登录时到欢迎页面。 ");
            Intent intent = new Intent(MainActivity.this, WelcomeViewActivity.class);
            startActivityForResult(intent, WELCOME_OK);
        }


        checkNewVersion();

        CRApplication.getInstance().addActivity(this);//为了退出应用程序

        initViews();// 初始化控件

        initData();// 初始化数据

    }


    public void initData(){
        fragments.add(new TabBFragment());//将网店管理放在list的0号位置。
        fragments.add(new TabCFragment());
        fragments.add(new TabFFragment());
        //     fragments.add(new TabAFragment(this, "broadband", "代客下单"));
        fragments.add(new TabEFragment());


        fragmentManager = this.getSupportFragmentManager();
        fragmentPagerAdapter = new MyPagerAdapter(fragmentManager);

        viewPager = (MainViewPager) findViewById(R.id.viewPager);
        // TODO
        viewPager.setSlipping(false);// 设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(1); //默认为第三个tab
        ((ImageView) findViewById(R.id.img_tab_c)).setImageDrawable(getResources().getDrawable(R.drawable.menu_c_1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog.i("ct", " MainActivity onStart...");

    }

    /**
     * 判断爱营销是否选择权限
     */
    private void checkPermission(final String phoneNumber){

        new BaseTask<String, String, JSONObject>(this, "登录中...") {


            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().login(getApplicationContext(), arg0[0], arg0[1]);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {

                try {
                    boolean b=jsonObject.getBoolean("success");
                    JSONObject record = jsonObject.getJSONObject("record");
                    MyLog.i("ct", "LoginActivity record=" + record.toString());
                    boolean value=record.getBoolean("selectValue");
                    if(value) {//当有选择权限时候
                        MyLog.i("ct", "LoginActivity 跳转到权限选择页面");

							/*Intent intent = new Intent(LoginActivity.this, LoginChooseActivity.class);
							startActivity(intent);*/
                    }else{
                        MyLog.i("ct", "LoginActivity 没有跳转到权限选择页面");
                        CRApplication.setId(getApplicationContext(), record.getString("userId"));
                        CRApplication.setName(getApplicationContext(), record.getString("userName"));
                        CRApplication.setToken(getApplicationContext(), record.getString("token"));
                        CRApplication.setPhone(getApplicationContext(), phoneNumber);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();
                Constant.loginTimes+=1;
            }
        }.execute(phoneNumber, "0");

    }


    private void updateTab(int arg0) {
        switch (arg0) {

            case 0:
                mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu_b_1));
                mText2.setTextColor(Color.parseColor("#148fcc"));
                break;

            //mTab2.setImageResource(R.drawable.menu_b_0);
            case 1:
                mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu_c_1));
                mText3.setTextColor(Color.parseColor("#148fcc"));
                break;
            case 2:
                mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu_d_1));
                mText4.setTextColor(Color.parseColor("#148fcc"));
                break;
            case 3:
                mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu_e_1));
                mText5.setTextColor(Color.parseColor("#148fcc"));
                break;

        }

        updateTabImage();

        currIndex = arg0;
    }



    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            updateTab(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void updateTabImage() {
        if (currIndex == 0) {
            mTab2.setImageResource(R.drawable.tab_b_selector);
            mText2.setTextColor(Color.parseColor("#444444"));
        }
        if (currIndex == 1) {
            mTab3.setImageResource(R.drawable.tab_c_selector);
            mText3.setTextColor(Color.parseColor("#444444"));
        } else if (currIndex == 2) {
            mTab4.setImageResource(R.drawable.tab_d_selector);
            mText4.setTextColor(Color.parseColor("#444444"));
        } else if (currIndex == 3) {
            mTab5.setImageResource(R.drawable.tab_e_selector);
            mText5.setTextColor(Color.parseColor("#444444"));
        }
    }

    public void initViews() {
        MyLog.i("ct", "MainActivity  initViews");
        LayoutTab1 = (LinearLayout) findViewById(R.id.tab1Layout);
        LayoutTab2 = (LinearLayout) findViewById(R.id.tab2Layout);
        LayoutTab3 = (LinearLayout) findViewById(R.id.tab3Layout);
        LayoutTab4 = (LinearLayout) findViewById(R.id.tab4Layout);
        LayoutTab5 = (LinearLayout) findViewById(R.id.tab5Layout);
        mTab1 = (ImageView) findViewById(R.id.img_tab_a);
        mTab2 = (ImageView) findViewById(R.id.img_tab_b);
        mTab3 = (ImageView) findViewById(R.id.img_tab_c);
        mTab4 = (ImageView) findViewById(R.id.img_tab_d);
        mTab5 = (ImageView) findViewById(R.id.img_tab_e);
        mText1 = (TextView) findViewById(R.id.text_tab_a);
        mText2 = (TextView) findViewById(R.id.text_tab_b);
        mText3 = (TextView) findViewById(R.id.text_tab_c);
        mText4 = (TextView) findViewById(R.id.text_tab_d);
        mText5 = (TextView) findViewById(R.id.text_tab_e);
        LayoutTab2.setOnClickListener(new MyOnClickListener(0));
        LayoutTab3.setOnClickListener(new MyOnClickListener(1));
        LayoutTab4.setOnClickListener(new MyOnClickListener(2));
        LayoutTab5.setOnClickListener(new MyOnClickListener(3));
        LayoutTab1.setOnClickListener(new MyOnClickListener(4));
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {

            switch (index) {
                case 0:

                    if(!canBeShopManagement){//默认为没有开通网店管理
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", CRApplication.getId(MainActivity.this));
                        HttpTool.sendPost(MainActivity.this, Constant.URL.SHOPMANAGEMENT, map, new HttpPostProp() {
                            @Override
                            public void dealRecord(Map record) {
                                boolean b = (boolean) record.get("success");
                                if (b) {
                                    canBeShopManagement = true;
                                    viewPager.setCurrentItem(index);
                                    TabBFragment tabBFragment = (TabBFragment) fragments.get(0);
                                    tabBFragment.fragment1.loadData();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("提示")
                                            .setMessage("你尚未开通“我的网店”，请在电脑登录电商协同平台，完成“我的网店”开通后再使用。" +
                                                    "平台网址：cq.189.cn/fx")
                                            .setPositiveButton("确认", new OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                }

                            }
                        });
                    }else{
                        viewPager.setCurrentItem(index);
                        TabBFragment tabBFragment= (TabBFragment) fragments.get(0);
                        TabBFragment.listView=tabBFragment.fragment1.listView;
                        tabBFragment.fragment1.loadData();
                    }


                    break;

                case 1:
                    viewPager.setCurrentItem(index);//主页
                    TabCFragment.needRefresh=true;
                    break;
                case 2:
                    viewPager.setCurrentItem(index);
                    break;
                case 3:
                    viewPager.setCurrentItem(index);
                    ((TabEFragment) fragments.get(index)).refresh();
                    break;
                case 4:
                    MynetStroe();
                    mTab1.setImageResource(R.drawable.tab_a_selector);
                    break;


                default:
                    return;
            }
        }
    }

    private void MynetStroe(){
        final Map<String, String> map = new HashMap<>();
        HttpTool.sendPost(this, Constant.CTX_PATH + "myNetStroe", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {

                boolean isFlag = (boolean) record.get("isFlag");
                if (isFlag) {
                    String url = (String) record.get("url");
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage((String) record.get("desc")).setPositiveButton("确认", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });
    }

    public void doUserIcon(View v) {
//        Intent i = new Intent(this, PersonalInfoActivity.class);
//        startActivityForResult(i, PERSONALINFO_REQUEST);
    }

    private void checkNewVersion() {
        new BaseTask<String, String, Version>(this) {
            //private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                //this.loadMask = ProgressDialog.show(context, null, "checking new version...");
            }

            @Override
            protected void onSuccess(Version result) {
                serverVersion = result;
                MyLog.i("ct", "MainActivity  获取版本号= " + result.getNumber());
                onNewVersionArrived();
            }

            @Override
            protected void onError() {
            }

            @Override
            protected void onPostExecute(Version result) {
                super.onPostExecute(result);
                //this.loadMask.dismiss();
            }

            @Override
            protected Version doInBack(String... params)
                    throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().checkNewVersion(MainActivity.this);
            }
        }.execute("");
    }


    private void onNewVersionArrived() {
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            Log.d("deng",String.valueOf(currentVersionCode));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (currentVersionCode < serverVersion.getNumber()) {

            View view = LayoutInflater.from(this).inflate(R.layout.update_remand, null);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setContentView(view);

            new AlertDialog.Builder(this)
                    .setTitle("版本更新:" + serverVersion.getName())
                    .setMessage(serverVersion.getDesc())
                    .setPositiveButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadNewVersion();
                        }
                    })
                    .setCancelable(serverVersion.isForce()).show();
        }
    }

    private void downloadNewVersion() {
        String url = serverVersion.getUrl();
        String path = Environment.getExternalStorageDirectory().getPath() + "/fxpt.apk";
        new DownloadTask(this, CRApplication.getApp().getHttpClient()).execute(new DownloadCallback(url, path, this));
    }

    @Override
    public void doCallback(String savePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + savePath), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }
    }

    ;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出电商分销", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                // TODO do not clear token
                // CRApplication.setToken(getApplicationContext(), null);
                // CRApplication.setId(getApplicationContext(), null);
                CRApplication.getInstance().exit();
               // finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        MyLog.i("ct", "MainActivity onActivityResult requestCode= " + requestCode);

        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {

                    MyLog.i("ct", "MainActivity onActivityResult LOGIN_REQUEST 登录成功");



            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        } else if (requestCode == PERSONALINFO_REQUEST) {
            if (resultCode == RESULT_OK) {
                //用户在个人中心退出登录
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(i, LOGIN_REQUEST);
            }
        } else if (requestCode == WELCOME_OK) {
            if (resultCode == RESULT_OK) {
                onResume();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            //返回首页
            viewPager.setCurrentItem(2); //默认为第三个tab
        }
    }

    private void getImsi() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        Log.i("IMSI", imsi);
        Log.i("IMEI", imei);

    }
}
