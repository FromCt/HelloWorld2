package com.crunii.android.fxpt.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.AdBanner;
import com.crunii.android.fxpt.business.TibmGoodsPlan;
import com.crunii.android.fxpt.util.ImageHelper;
import com.crunii.android.fxpt.util.MyAsynImageLoader;
import com.crunii.android.fxpt.view.MyScrollView;
import com.crunii.android.fxpt.view.MyScrollView.OnScrollToBottomListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

abstract class ProductDetailActivity extends Activity {
    final static int VERIFY_NUMBER_REQUEST_FIXEDLINE = 1;
    final static int VERIFY_NUMBER_REQUEST_BROADBAND = 2;
    final static int VERIFY_NUMBER_REQUEST_PHONE = 3;
    final static int VERIFY_NUMBER_REQUEST_CONTRACTMODE = 4;
    final static int VERIFY_NUMBER_REQUEST_GUARANTEE_FIXEDLINE = 5;
    final static int VERIFY_NUMBER_REQUEST_GUARANTEE_BROADBAND = 6;
    final static int CHOOSE_NUMBER_REQUEST = 7;
    final static int CHOOSE_CONTRACT_PACKAGE_REQUEST = 8;
    final static int VERIFY_NUMBER_REQUEST_GUARANTEE_CONTRACTACCOUNT = 9;
    final static int CHOOSE_ITV_REQUEST = 10;


    private static DecimalFormat df = new DecimalFormat("###,##0.00");

    String category, mode, speed, packageId, goodsCode;

    String payPrice = "";//点击支付金额按钮后展示都内容
    String[] businePhone;
    String fixedlineNumber = "";
    String fixedlineAddress = "";
    String fixedlineContact = "";
    String broadbandNumber = "";
    String broadbandAddress = "";
    String broadbandContact = "";
    String phoneNumber = "";
    String phoneAddress = "";
    String phoneContact = "";
    String contractmodeAddType = "";
    String contractmodeAddNumber = "";
    String contractmodeAddress = "";
    String contractmodeContact = "";
    String guaranteeNumber = "";
    String guaranteeAddress = "";
    String guaranteeContact = "";

    String choosedNumber = "";
    String choosedNumberTeleUse = "";
    String contractPackageId = "";
    String ITVId = "";
    String ITVType = "";
    String databaseId = "";


    boolean isContractAccount = false;     //是否展示合帐
    Map<String, Object> contractAccount;   //配置项
    TibmGoodsPlan tibmGoodsPlan;
    String contractAccountNumber = "";            //合帐号码
    String contractAccountAddress = "";          //安装地址
    String contractAccountContact = "";               //联系电话

    String displayName = "";
    String displayCitizenId = "";
    String displayCitizenAddress = "";
    String verifiedName = "";
    String verifiedCitizenId = "";
    String verifiedCitizenAddress = "";


    String goodsId = "";
    Boolean requireLogistics = false;
    int buyLimit = 0;
    JSONArray contractPackage;
    JSONArray ITV;
    JSONArray areaList;
    JSONArray sectionList;
    boolean verifyOldInfo;
    boolean modifyOcrName;
    boolean modifyOcrCarNum;
    boolean modifyOcrAddress;
    boolean uploadCitizenPhoto;
    boolean isOfflinePay;

    private ViewPager adBannerVp;
    private int currentItem = 0; // 当前图片的索引号
    private List<ImageView> dots; // 图片切换布局
    private MyAsynImageLoader imageLoader;
    private List<ImageView> imageViews;
    private ImagePagerAdapter adapter;
    private LinearLayout adBannerDots;

    //合账号码要求也需要带入客户信息及老用户证件校验的逻辑，如同时存在的情况下的逻辑判断优先级为：担保>加装>合账。
    //加装 中 手机>宽带>固话
    //手机
    String displayName_SJ = "", displayCitizenId_SJ = "", displayCitizenAddress_SJ = "", verifiedName_SJ = "", verifiedCitizenId_SJ = "", verifiedCitizenAddress_SJ = "";
    //固话
    String displayName_GH = "", displayCitizenId_GH = "", displayCitizenAddress_GH = "", verifiedName_GH = "", verifiedCitizenId_GH = "", verifiedCitizenAddress_GH = "";
    //宽带
    String displayName_KD = "", displayCitizenId_KD = "", displayCitizenAddress_KD = "", verifiedName_KD = "", verifiedCitizenId_KD = "", verifiedCitizenAddress_KD = "";
    //担保
    String displayName_DB = "", displayCitizenId_DB = "", displayCitizenAddress_DB = "", verifiedName_DB = "", verifiedCitizenId_DB = "", verifiedCitizenAddress_DB = "";
    //合帐
    String displayName_HZ = "", displayCitizenId_HZ = "", displayCitizenAddress_HZ = "", verifiedName_HZ = "", verifiedCitizenId_HZ = "", verifiedCitizenAddress_HZ = "";
    //合约机
    String displayName_HY = "", displayCitizenId_HY = "", displayCitizenAddress_HY = "", verifiedName_HY = "", verifiedCitizenId_HY = "", verifiedCitizenAddress_HY = "";


    //radiobutton数量固定的radiogroup
    RadioGroup fixedline_rg, broadband_rg, phone_rg, contractmode_rg, guarantee_rg, choose_number_rg, contract_package_rg,
            rg_contractAccount, ITV_rg;
    RadioButton fixedline_rb0, fixedline_rb1, fixedline_rb2, broadband_rb1, broadband_rb2, phone_rb1, phone_rb2, contractmode_rb1, contractmode_rb2,
            guarantee_rb1, guarantee_rb2, guarantee_rb3, choose_number_rb, contract_package_rb, rg_contractAccount_1, rg_contractAccount_2, ITV_none_rb, ITV_new_rb, ITV_modify_rb;

    //radiobutton数量不固定的radiogroup
    RadioGroup products_rg, contract_rg, salemode_rg;

    View productsview, serialnumberview, fixedlineview, broadbandview, phoneview, contractmodeview, guaranteeview, tr_contractAccount,
            choose_number_view, contract_view, contract_package_view, amountview, detailview, discountview, numLimitView, preExplainView, ITV_view, ITV_view_desc;
    // salemodeview 20150610屏蔽酬金模式，后台传固定值（固宽、合约模板传）
    private OnAdClickListner onAdClickListner = new OnAdClickListner();

    String lastOpenedNetworkImage = "";

    Button bt_predict;//佣金预计按钮

    boolean needChooseNumber;

    boolean hasDiscount = false;
    String discountDesc = "";
    String numLimitDesc = "";//批量模板 分销商销售点（展示）
    String preExplainDesc = "";//充值卡模板前台文字说明
    String dlsName = "";     //经营主体校验字段
    String branchCompany = "";//分公司校验字段
    String originalProductPrice = "";
    JSONArray discountArray = new JSONArray();

    OnCheckedChangeListener productChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton radio, boolean checked) {
            if (checked) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);
                    ((TextView) findViewById(R.id.stock)).setText(json.optString("stock"));

                    updateProductPrice(json.optString("productCode"), getSaleModeId(), getContractId());

                    updateSnView(json);

                    updateTopAds(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    OnCheckedChangeListener salemodeChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton radio, boolean checked) {
            if (checked) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);
                    //终端铺货一次结 2
                    updateProductPrice(getProductCode(), "2", getContractId());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    OnCheckedChangeListener contractChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton radio, boolean checked) {
            if (checked) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);

                    //选择模式（合约）后，相应的套餐也要发生变化
                    updateContractPackage(json.optString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    abstract void showOrHideViews();

    abstract String getTemplateId();


    String getSaleModeId() {
        //20150610  终端铺货一次结 2
        String salemodeId = "2";
//        for(int j=0; j<salemode_rg.getChildCount(); j++) {
//            RadioButton radio = (RadioButton) salemode_rg.getChildAt(j);
//            if(radio.isChecked()) {
//                try {
//                    String jsonString = (String) radio.getTag();
//                    JSONObject json = new JSONObject(jsonString);
//                    salemodeId = json.optString("id");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
        return salemodeId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        category = getIntent().getExtras().getString("category", "");
        mode = getIntent().getExtras().getString("mode", "");
        speed = getIntent().getExtras().getString("speed", "");
        packageId = getIntent().getExtras().getString("packageId", "");
        goodsCode = getIntent().getExtras().getString("goodsCode");

        initViews();
        refresh();
    }

    int count = 0;

    private void initViews() {
        //监听滑动到底部事件
        initGestureDetector();

        adBannerVp = (ViewPager) findViewById(R.id.home_ad_vp);
        adBannerDots = (LinearLayout) findViewById(R.id.home_ad_index_ll);

        productsview = findViewById(R.id.productsview);
        products_rg = (RadioGroup) findViewById(R.id.products_rg);

        serialnumberview = findViewById(R.id.serialnumberview);

        fixedlineview = findViewById(R.id.fixedlineview); //固话新装或加装
        fixedline_rg = (RadioGroup) findViewById(R.id.fixedline_rg);
        fixedline_rb0 = (RadioButton) findViewById(R.id.fixedline_rb0);
        fixedline_rb1 = (RadioButton) findViewById(R.id.fixedline_rb1);
        fixedline_rb2 = (RadioButton) findViewById(R.id.fixedline_rb2);

        broadbandview = findViewById(R.id.broadbandview); //宽带新装或加装
        broadband_rg = (RadioGroup) findViewById(R.id.broadband_rg);
        broadband_rb1 = (RadioButton) findViewById(R.id.broadband_rb1);
        broadband_rb2 = (RadioButton) findViewById(R.id.broadband_rb2);

        phoneview = findViewById(R.id.phoneview); //手机入网方式，新装或加装
        phone_rg = (RadioGroup) findViewById(R.id.phone_rg);
        phone_rb1 = (RadioButton) findViewById(R.id.phone_rb1);
        phone_rb2 = (RadioButton) findViewById(R.id.phone_rb2);

        contractmodeview = findViewById(R.id.contractmodeview); //合约机入网方式，新装或加装
        contractmode_rg = (RadioGroup) findViewById(R.id.contractmode_rg);
        contractmode_rb1 = (RadioButton) findViewById(R.id.contractmode_rb1);
        contractmode_rb2 = (RadioButton) findViewById(R.id.contractmode_rb2);

        guaranteeview = findViewById(R.id.guaranteeview); //担保方式，无担保、固话担保、宽带担保
        guarantee_rg = (RadioGroup) findViewById(R.id.guarantee_rg);
        guarantee_rb1 = (RadioButton) findViewById(R.id.guarantee_rb1);
        guarantee_rb2 = (RadioButton) findViewById(R.id.guarantee_rb2);
        guarantee_rb3 = (RadioButton) findViewById(R.id.guarantee_rb3);

        tr_contractAccount = findViewById(R.id.tr_contractAccount);//合帐方式   不合帐  合帐
        rg_contractAccount = (RadioGroup) findViewById(R.id.rg_contractAccount);
        rg_contractAccount_1 = (RadioButton) findViewById(R.id.rg_contractAccount_1);
        rg_contractAccount_2 = (RadioButton) findViewById(R.id.rg_contractAccount_2);

        choose_number_view = findViewById(R.id.choose_number_view);
        choose_number_rg = (RadioGroup) findViewById(R.id.choose_number_rg);
        choose_number_rb = (RadioButton) findViewById(R.id.choose_number_rb);

        contract_view = findViewById(R.id.contract_view);
        contract_rg = (RadioGroup) findViewById(R.id.contract_rg);

        contract_package_view = findViewById(R.id.contract_package_view);
        contract_package_rg = (RadioGroup) findViewById(R.id.contract_package_rg);
        contract_package_rb = (RadioButton) findViewById(R.id.contract_package_rb);

        ITV_view_desc = findViewById(R.id.ITV_view_desc);

        //salemodeview = findViewById(R.id.salemodeview);
        //salemode_rg = (RadioGroup) findViewById(R.id.salemode_rg);

        amountview = findViewById(R.id.amountview);

        detailview = findViewById(R.id.detailview);

        discountview = findViewById(R.id.discountview);

        preExplainView = findViewById(R.id.preExplainView);

        ITV_view = findViewById(R.id.ITV_view);
        ITV_none_rb = (RadioButton) findViewById(R.id.ITV_none_rb);
        ITV_new_rb = (RadioButton) findViewById(R.id.ITV_new_rb);
        ITV_modify_rb = (RadioButton) findViewById(R.id.ITV_modify_rb);

        numLimitView = findViewById(R.id.numLimitView);

        bt_predict = (Button) findViewById(R.id.bt_predict);
        bt_predict.setVisibility(View.GONE);
        ((EditText) findViewById(R.id.count)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int number = Integer.valueOf(editable.toString());
                    if (!hasDiscount) {
                        if (getTemplateId().equals("1009") || getTemplateId().equals("1010")) {
                            String countStr = ((EditText) findViewById(R.id.count)).getText().toString().trim();
                            if (payPrice.length() == 0) {
                                return ;
                            } else {
                                Double showPrice = Double.valueOf(payPrice) * Integer.valueOf(countStr);
                                ((TextView) findViewById(R.id.price)).setText("商品零售价:" + showPrice.toString() + "元");
                            }
                        }
                        return;
                    } else {
                        updateDiscount(getProductCode(), number);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //默认隐藏所有按钮
        hideAllViews();
    }

    private void hideAllViews() {
        productsview.setVisibility(View.GONE);
        serialnumberview.setVisibility(View.GONE);
        fixedlineview.setVisibility(View.GONE);
        broadbandview.setVisibility(View.GONE);
        phoneview.setVisibility(View.GONE);
        contractmodeview.setVisibility(View.GONE);
        choose_number_view.setVisibility(View.GONE);
        contract_view.setVisibility(View.GONE);
        contract_package_view.setVisibility(View.GONE);
        //salemodeview.setVisibility(View.GONE);
        amountview.setVisibility(View.GONE);
        guaranteeview.setVisibility(View.GONE);
        detailview.setVisibility(View.GONE);
        discountview.setVisibility(View.GONE);
        numLimitView.setVisibility(View.GONE);
        preExplainView.setVisibility(View.GONE);
        ITV_view.setVisibility(View.GONE);
        ITV_view_desc.setVisibility(View.GONE);

        /**
         * 佣金预计按钮显示模板
         *   1001 1004 1008
         *   合约  套餐  固宽
         */
        if (getTemplateId().equals("1001") || getTemplateId().equals("1004") || getTemplateId().equals("1008")) {
            bt_predict.setVisibility(View.GONE);  //20150813 屏蔽佣金预计
        }
    }

    /**
     * 点击后展示定价计划中的支付金额
     *
     * @param view
     */
    public void doPayPrice(View view) {
//        if (getTemplateId().equals("1009") || getTemplateId().equals("1010") ) {
//            String countStr = ((EditText)findViewById(R.id.count)).getText().toString().trim();
//            Double showPrice = Double.valueOf(originalProductPrice) * Integer.valueOf(countStr);
//            Toast.makeText(this, "分销商支付金额" + showPrice, Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "分销商支付金额" + originalProductPrice, Toast.LENGTH_SHORT).show();
//        }
        if (getTemplateId().equals("1009") || getTemplateId().equals("1010")) {
            String countStr = ((EditText) findViewById(R.id.count)).getText().toString().trim();
            if (payPrice.length() == 0) {
                Toast.makeText(this, "定价计划有误", Toast.LENGTH_SHORT).show();
                return ;
            } else {
                Double showPrice = Double.valueOf(payPrice) * Integer.valueOf(countStr);
                Toast.makeText(this, "分销商支付金额" + showPrice, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "分销商支付金额" + payPrice, Toast.LENGTH_SHORT).show();
        }

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
            if (position < size) {
                container.removeView(imageViews.get(position));
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

            dots.get(oldPosition).setImageResource(R.drawable.dot4);
            dots.get(position).setImageResource(R.drawable.dot3);

            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
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
                        int size = imageViews.size();
                        if (size > 0) {
                            currentItem = (currentItem + 1) % size;
                            adBannerVp.setCurrentItem(currentItem);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (mTimer == null) {   //保证只有一个 定时任务
            mTimer = new Timer(true);
            mTimer.schedule(new MyTask(), 10000, 10000);
        }
        super.onResume();
        //cheakbuyLimit();//进商品详情的时候，校验商品购买限制

    }

    private void updateTopAds(JSONObject json) {
        JSONArray detailimg = json.optJSONArray("detailimg");

        imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), this);
        imageViews = new ArrayList<ImageView>();
        dots = new ArrayList<ImageView>();
        adapter = new ImagePagerAdapter();
        adBannerVp.setAdapter(adapter);
        adBannerVp.setOnPageChangeListener(new MyPageChangeListener());
        adBannerDots.removeAllViews();

        for (int i = 0; i < detailimg.length(); i++) {
            //添加图片
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ScaleType.FIT_CENTER);
            imageView.setImageResource(R.drawable.adbanner2);
            imageView.setOnClickListener(onAdClickListner);
            imageView.setTag(new AdBanner(detailimg.optJSONObject(i)));
            imageViews.add(imageView);

            //添加小圆点
            ImageView dot = new ImageView(this);
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

    public void doBack(View v) {
        onBackPressed();
    }

    public void doClearTop(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void refresh() {

        new BaseTask<String, String, JSONObject>(this, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().productdetail(ProductDetailActivity.this, category, mode, speed, packageId, goodsCode);
            }

            @Override
            protected void onSuccess(JSONObject result) {
                showData(result);
            }
        }.execute("");

    }

    private void showData(JSONObject result) {
        JSONArray numberList = result.optJSONArray("businePhone");
        if (numberList != null && numberList.length() != 0) {
            businePhone = new String[numberList.length()];
            for (int i = 0; i < numberList.length(); i++) {
                businePhone[i] = numberList.optString(i);
            }
        }
        needChooseNumber = result.optBoolean("needChooseNumber"); //仅固宽模板需要此标志判断是否需要选号

        hasDiscount = result.optBoolean("hasDiscount"); //是否存在折扣（目前仅限于终端模板和批量购买模板）
        discountDesc = result.optString("discountDesc");
        discountArray = result.optJSONArray("discount");

        //批量购买限制描述：发展量归属销售点：XXXXX
        numLimitDesc = result.optString("sellPoint");
        preExplainDesc = result.optString("preExplain");
        dlsName = result.optString("dlsName");
        branchCompany = result.optString("branchCompany");

        isContractAccount = result.optBoolean("isContractAccount");
        tibmGoodsPlan = new TibmGoodsPlan(result.optJSONObject("contractAccount"));
        //contractAccount = CitizenUtil.getMap();

        showOrHideViews();

        ((TextView) findViewById(R.id.name)).setText(result.optString("name"));
        //未选择产品时的价格和库存
        ((TextView) findViewById(R.id.price)).setText("商品零售价:" + result.optString("price") + "元");
        ((TextView) findViewById(R.id.stock)).setText(result.optString("stock"));
        payPrice = result.optString("payPrice");
        //产品列表
        LayoutInflater inflater = LayoutInflater.from(this);
        JSONArray products = result.optJSONArray("products");
        for (int i = 0; i < products.length(); i++) {
            JSONObject json = products.optJSONObject(i);
            String name = json.optString("name");
            //中文括号比较宽，用英文括号替换，尽量不换行
            name = name.replace("（", "(");
            name = name.replace("）", ")");

            RadioButton radio = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
            radio.setText(name);
            radio.setTag(json.toString());
            radio.setOnCheckedChangeListener(productChangeListener);
            products_rg.addView(radio);
        }
        //默认选择第一个产品
        ((RadioButton) products_rg.getChildAt(0)).setChecked(true);

        //固话新装或加装
        String fixedline = result.optString("fixedline");
        if (fixedline.equals("")) {
            fixedlineview.setVisibility(View.GONE);
        } else {
            fixedlineview.setVisibility(View.VISIBLE);
            if (fixedline.contains("none")) {
                fixedline_rb0.setVisibility(View.VISIBLE);
                fixedline_rb0.setChecked(true);
            } else {
                fixedline_rb0.setVisibility(View.GONE);
            }
            if (fixedline.contains("new")) {
                fixedline_rb1.setVisibility(View.VISIBLE);
                if (!fixedline_rb0.isChecked()) {
                    fixedline_rb1.setChecked(true);
                }
            } else {
                fixedline_rb1.setVisibility(View.GONE);
            }
            if (fixedline.contains("add")) {
                fixedline_rb2.setVisibility(View.VISIBLE);
                if (!fixedline_rb0.isChecked() && !fixedline_rb1.isChecked()) {
                    fixedline_rb2.setChecked(true);
                }
            } else {
                fixedline_rb2.setVisibility(View.GONE);
            }
        }

        //宽带新装或加装
        String broadband = result.optString("broadband");
        if (broadband.equals("")) {
            broadbandview.setVisibility(View.GONE);
        } else {
            broadbandview.setVisibility(View.VISIBLE);

            if (broadband.contains("new")) {
                broadband_rb1.setVisibility(View.VISIBLE);
                broadband_rb1.setChecked(true);
            } else {
                broadband_rb1.setVisibility(View.GONE);
            }
            if (broadband.contains("add")) {
                broadband_rb2.setVisibility(View.VISIBLE);
                if (!broadband_rb1.isChecked()) {
                    broadband_rb2.setChecked(true);
                }
            } else {
                broadband_rb2.setVisibility(View.GONE);
            }
        }

        //手机新装或加装
        String phone = result.optString("phone");
        if (phone.equals("")) {
            phoneview.setVisibility(View.GONE);
        } else {
            phoneview.setVisibility(View.VISIBLE);

            if (phone.contains("new")) {
                phone_rb1.setVisibility(View.VISIBLE);
                phone_rb1.setChecked(true);
            } else {
                phone_rb1.setVisibility(View.GONE);
            }
            if (phone.contains("add")) {
                phone_rb2.setVisibility(View.VISIBLE);
                if (!phone_rb1.isChecked()) {
                    phone_rb2.setChecked(true);
                }
            } else {
                phone_rb2.setVisibility(View.GONE);
            }
        }

        //合约机入网方式，新装或加装
        String contractmode = result.optString("contractmode");
        if (contractmode.equals("")) {
            contractmodeview.setVisibility(View.GONE);
        } else {
            contractmodeview.setVisibility(View.VISIBLE);

            if (contractmode.contains("new")) {
                contractmode_rb1.setVisibility(View.VISIBLE);
                contractmode_rb1.setChecked(true);
            } else {
                contractmode_rb1.setVisibility(View.GONE);
            }
            if (contractmode.contains("add")) {
                contractmode_rb2.setVisibility(View.VISIBLE);
                if (!contractmode_rb1.isChecked()) {
                    contractmode_rb2.setChecked(true);
                }
            } else {
                contractmode_rb2.setVisibility(View.GONE);
            }
        }

        //担保方式，无担保、固话担保、宽带担保
        String guarantee = result.optString("guarantee");
        if (guarantee.equals("")) {
            guaranteeview.setVisibility(View.GONE);
        } else {
            guaranteeview.setVisibility(View.VISIBLE);

            if (guarantee.contains("none")) {
                guarantee_rb1.setVisibility(View.VISIBLE);
                guarantee_rb1.setChecked(true);
            } else {
                guarantee_rb1.setVisibility(View.GONE);
            }
            if (guarantee.contains("fixedline")) {
                guarantee_rb2.setVisibility(View.VISIBLE);
                if (!guarantee_rb1.isChecked()) {
                    guarantee_rb2.setChecked(true);
                }
            } else {
                guarantee_rb2.setVisibility(View.GONE);
            }
            if (guarantee.contains("broadband")) {
                guarantee_rb3.setVisibility(View.VISIBLE);
                if (!guarantee_rb1.isChecked() && !guarantee_rb2.isChecked()) {
                    guarantee_rb3.setChecked(true);
                }
            } else {
                guarantee_rb3.setVisibility(View.GONE);
            }
        }
        //合帐
        String contractAccountType = result.optString("contractAccountType");
        if (contractAccountType.equals("")) {
            tr_contractAccount.setVisibility(View.GONE);
        } else {
            tr_contractAccount.setVisibility(View.VISIBLE);
            if (contractAccountType.contains("no")) {
                rg_contractAccount_1.setVisibility(View.VISIBLE);
                rg_contractAccount_1.setChecked(true);
            } else if (contractAccountType.contains("yes")) {
                rg_contractAccount_2.setVisibility(View.VISIBLE);
                if (!rg_contractAccount_1.isChecked()) {
                    rg_contractAccount_2.setChecked(true);
                }
            }
        }

        //模式(合约)
        JSONArray contract = result.optJSONArray("contract");
        if (contract.length() == 0) {
            contract_view.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < contract.length(); i++) {
                JSONObject json = contract.optJSONObject(i);
                String name = json.optString("name");
                RadioButton radio = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
                radio.setText(name);
                radio.setTag(json.toString());
                radio.setOnCheckedChangeListener(contractChangeListener);
                contract_rg.addView(radio);
            }
            //默认选择第一种模式(合约)
            ((RadioButton) contract_rg.getChildAt(0)).setChecked(true);
        }


        //"ITVType":"none,new,modify",                  //ITV  无需ITV    新装ITV    改资费
        String ITVType = result.optString("ITVType");//
//固宽模版＋合约机模版
        if ((getTemplateId().equals(ProductDetailBroadband.templateId)) || getTemplateId().equals(ProductDetailContract.templateId)) {
            if (ITVType.equals("")) {
                ITV_view.setVisibility(View.GONE);
            } else {
                ITV_view.setVisibility(View.VISIBLE);

                if (ITVType.contains("none")) {
                    ITV_none_rb.setVisibility(View.VISIBLE);
                    ITV_none_rb.setChecked(true);
                } else {
                    ITV_none_rb.setVisibility(View.GONE);
                }
                if (ITVType.contains("new")) {
                    ITV_new_rb.setVisibility(View.VISIBLE);
                    if (!ITV_none_rb.isChecked()) {
                        ITV_new_rb.setChecked(true);
                    }
                } else {
                    ITV_new_rb.setVisibility(View.GONE);
                }
                if (ITVType.contains("modify")) {
                    ITV_modify_rb.setVisibility(View.VISIBLE);
                    if (!ITV_none_rb.isChecked() && !ITV_new_rb.isChecked()) {
                        ITV_modify_rb.setChecked(true);
                    }
                } else {
                    ITV_modify_rb.setVisibility(View.GONE);
                }
            }
        }


        //酬金模式
//		JSONArray salemode = result.optJSONArray("salemode");
//		if(salemode.length() == 0) {
//			salemodeview.setVisibility(View.GONE);
//		} else {
//			for(int i=0; i<salemode.length(); i++) {
//				JSONObject json = salemode.optJSONObject(i);
//				String name = json.optString("name");
//				RadioButton radio = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
//				radio.setText(name);
//				radio.setTag(json.toString());
//				radio.setOnCheckedChangeListener(salemodeChangeListener);
//				salemode_rg.addView(radio);
//			}
//			//默认选择第一种酬金模式
//			((RadioButton)salemode_rg.getChildAt(0)).setChecked(true);
//		}

        requireLogistics = result.optBoolean("requireLogistics");
        buyLimit = result.optInt("buyLimit");
        contractPackage = result.optJSONArray("contractPackage");
        areaList = result.optJSONArray("areaList");
        sectionList = result.optJSONArray("sectionList");
        databaseId = result.optString("databaseId");

        //默认终端串号调拨合作伙伴门户工号
        ((EditText) findViewById(R.id.serialnumber)).setText(result.optString("defaultSn"));

        //是否展示图文详情
        if (result.optBoolean("hasDetailContent")) {
            detailview.setVisibility(View.VISIBLE);
        } else {
            detailview.setVisibility(View.GONE);
        }

        //是否展示优惠信息
        if (hasDiscount) {
            discountview.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.discountDesc)).setText(discountDesc);
        } else {
            discountview.setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.numLimitDesc)).setText("发展量归属销售点：" + numLimitDesc);
        if (preExplainDesc.length() != 0) {
            findViewById(R.id.preExplainView).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.preExplainDesc)).setText(Html.fromHtml(preExplainDesc));
        } else {
            findViewById(R.id.preExplainView).setVisibility(View.GONE);
        }
        verifyOldInfo = result.optBoolean("verifyOldInfo");
        modifyOcrName = result.optBoolean("modifyOcrName");
        modifyOcrCarNum = result.optBoolean("modifyOcrCarNum");
        modifyOcrAddress = result.optBoolean("modifyOcrAddress");
        uploadCitizenPhoto = result.optBoolean("uploadCitizenPhoto");

        ITV = result.optJSONArray("ITV");
        isOfflinePay = result.optBoolean("isOfflinePay");
    }

    //根据产品属性显示或隐藏串号输入界面
    private void updateSnView(JSONObject json) {
        if (json.optBoolean("requireSn")) {
            serialnumberview.setVisibility(View.VISIBLE);
        } else {
            serialnumberview.setVisibility(View.GONE);
        }
    }

    public void doFixedlineAdd(View v) {
        //固话加装验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_JZ);
        bundle.putString("type", "fixedline");
        bundle.putString("number", fixedlineNumber);
        bundle.putString("address", fixedlineAddress);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_FIXEDLINE);
    }

    public void doBroadbandAdd(View v) {
        //宽带加装验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_JZ);
        bundle.putString("type", "broadband");
        bundle.putString("number", broadbandNumber);
        bundle.putString("address", broadbandAddress);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_BROADBAND);
    }

    public void doPhoneAdd(View v) {
        //手机加装验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_JZ);
        bundle.putString("type", "phone");
        bundle.putString("number", phoneNumber);
        bundle.putString("address", phoneAddress);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_PHONE);
    }

    public void doContractModeAdd(View v) {
//		//合约机加装验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_JZ);
        bundle.putString("type", "contractmode");
        bundle.putString("number", contractmodeAddNumber);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_CONTRACTMODE);

    }

    public void doGuaranteeFixedline(View v) {
        //固话担保验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_DB);
        bundle.putString("type", "fixedline");
        bundle.putString("number", guaranteeNumber);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_GUARANTEE_FIXEDLINE);
    }

    public void doGuaranteeBroadband(View v) {
        //宽带担保验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_DB);
        bundle.putString("type", "broadband");
        bundle.putString("number", guaranteeNumber);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_GUARANTEE_BROADBAND);
    }

    public void doContractAccount(View view) {
        //合帐验证号码
        Intent i = new Intent(this, VerifyNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount", tibmGoodsPlan);
        bundle.putBoolean("isContractAccount", isContractAccount);
        bundle.putString("typeId", TibmGoodsPlan.YZHM_HZ);
        bundle.putString("type", "contractAccount");
        bundle.putString("number", contractAccountNumber);
        i.putExtras(bundle);
        startActivityForResult(i, VERIFY_NUMBER_REQUEST_GUARANTEE_CONTRACTACCOUNT);
    }

    public void doChooseNumber(View v) {
        Intent i = new Intent(this, ChooseNumberActivity.class);
        i.putExtra("areaList", areaList.toString());
        i.putExtra("sectionList", sectionList.toString());
        i.putExtra("databaseId", databaseId);
        startActivityForResult(i, CHOOSE_NUMBER_REQUEST);
    }

    public void doChooseContractPackage(View v) {
        Intent i = new Intent(this, ChooseContractPackageActivity.class);
        i.putExtra("contractPackage", contractPackage.toString());
        startActivityForResult(i, CHOOSE_CONTRACT_PACKAGE_REQUEST);
    }

    //"ITVType":"none,new,modify",                  //ITV  无需ITV    新装ITV    改资费
    public void doITV(View view) {
        switch (view.getId()) {
            case R.id.ITV_none_rb:
                ITVId = "";
                ITVType = "none";
                ITV_view_desc.setVisibility(View.GONE);
                break;

            case R.id.ITV_new_rb:
                ITVType = "new";
                chooseITV();
                break;

            case R.id.ITV_modify_rb:
                ITVType = "modify";
                chooseITV();
                break;
        }
    }

    private void chooseITV() {
        Intent i = new Intent(this, ChooseContractPackageActivity.class);
        i.putExtra("contractPackage", ITV.toString());//
        startActivityForResult(i, CHOOSE_ITV_REQUEST);
    }

    public void doMinus(View v) {
        int countNumber = getCountNumber();
        if (countNumber > 1) {
            countNumber--;
        }
        ((EditText) findViewById(R.id.count)).setText(countNumber + "");
        updateUserPrice(countNumber);
    }

    public void doPlus(View v) {
        int countNumber = getCountNumber();

        countNumber++;
        if (buyLimit != 0 && countNumber > buyLimit) {
            Toast.makeText(this, "单次购买上限最大为" + buyLimit, Toast.LENGTH_LONG).show();
            return;
        }

        ((EditText) findViewById(R.id.count)).setText(countNumber + "");
        updateUserPrice(countNumber);
    }

    private void updateUserPrice(Integer number) {
        if (originalProductPrice.length() == 0) {
            Toast.makeText(this, "商品价格有问题", Toast.LENGTH_SHORT).show();
            return;
        }
        Double pricrStr =  Double.valueOf(originalProductPrice) * number;

        ((TextView) findViewById(R.id.price)).setText("商品零售价:" +pricrStr.toString() + "元");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case VERIFY_NUMBER_REQUEST_FIXEDLINE: //验证号码 固话
                    fixedlineNumber = data.getExtras().getString("number");
                    fixedlineAddress = data.getExtras().getString("address");
                    fixedlineContact = data.getExtras().getString("bestContact");
                    displayName_GH = data.getExtras().getString("displayName");
                    displayCitizenId_GH = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_GH = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_GH = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_GH = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_GH = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case VERIFY_NUMBER_REQUEST_BROADBAND: //验证号码 宽带
                    broadbandNumber = data.getExtras().getString("number");
                    broadbandAddress = data.getExtras().getString("address");
                    broadbandContact = data.getExtras().getString("bestContact");
                    displayName_KD = data.getExtras().getString("displayName");
                    displayCitizenId_KD = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_KD = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_KD = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_KD = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_KD = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case VERIFY_NUMBER_REQUEST_PHONE: //验证号码 手机
                    phoneNumber = data.getExtras().getString("number");
                    phoneAddress = data.getExtras().getString("address");
                    phoneContact = data.getExtras().getString("bestContact");
                    displayName_SJ = data.getExtras().getString("displayName");
                    displayCitizenId_SJ = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_SJ = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_SJ = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_SJ = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_SJ = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case VERIFY_NUMBER_REQUEST_CONTRACTMODE: //验证号码 合约机入网模式
                    contractmodeAddNumber = data.getExtras().getString("number");
                    contractmodeAddType = data.getExtras().getString("type");
                    contractmodeAddress = data.getExtras().getString("address");
                    contractmodeContact = data.getExtras().getString("bestContact");
                    displayName_HY = data.getExtras().getString("displayName");
                    displayCitizenId_HY = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_HY = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_HY = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_HY = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_HY = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case VERIFY_NUMBER_REQUEST_GUARANTEE_FIXEDLINE: //验证宽带担保号码
                case VERIFY_NUMBER_REQUEST_GUARANTEE_BROADBAND://宽带担保
                    guaranteeNumber = data.getExtras().getString("number");
                    guaranteeAddress = data.getExtras().getString("address");
                    guaranteeContact = data.getExtras().getString("bestContact");
                    displayName_DB = data.getExtras().getString("displayName");
                    displayCitizenId_DB = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_DB = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_DB = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_DB = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_DB = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case VERIFY_NUMBER_REQUEST_GUARANTEE_CONTRACTACCOUNT://合帐
                    contractAccountNumber = data.getExtras().getString("number");
                    contractAccountAddress = data.getExtras().getString("address");
                    contractAccountContact = data.getExtras().getString("bestContact");
                    displayName_HZ = data.getExtras().getString("displayName");
                    displayCitizenId_HZ = data.getExtras().getString("displayCitizenId");
                    displayCitizenAddress_HZ = data.getExtras().getString("displayCitizenAddress");
                    verifiedName_HZ = data.getExtras().getString("verifiedName");
                    verifiedCitizenId_HZ = data.getExtras().getString("verifiedCitizenId");
                    verifiedCitizenAddress_HZ = data.getExtras().getString("verifiedCitizenAddress");
                    break;
                case CHOOSE_NUMBER_REQUEST: //选择手机号
                    choosedNumber = data.getExtras().getString("number");
                    choosedNumberTeleUse = data.getExtras().getString("tele_use");
                    choose_number_rb.setText(choosedNumber);
                    break;
                case CHOOSE_CONTRACT_PACKAGE_REQUEST: //选择套餐
                    contractPackageId = data.getExtras().getString("contractPackageId");
                    String contractPackageName = "";
                    for (int i = 0; i < contractPackage.length(); i++) {
                        JSONObject json = contractPackage.optJSONObject(i);
                        if (json.optString("id").equals(contractPackageId)) {
                            contractPackageName = json.optString("name");
                            break;
                        }
                    }
                    contract_package_rb.setText(contractPackageName);
                    break;
                case CHOOSE_ITV_REQUEST: //选择ITV
                    ITVId = data.getExtras().getString("contractPackageId");
                    String ITVComboName = "";
                    for (int i = 0; i < ITV.length(); i++) {
                        JSONObject json = ITV.optJSONObject(i);
                        if (json.optString("id").equals(ITVId)) {
                            ITVComboName = json.optString("name");
                            break;
                        }
                    }
                    ITV_view_desc.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_itv_desc)).setText(ITVComboName);
                    break;

                default:
                    break;
            }

    }


    public void doPredict(View view) {

       /* goodsCode
                productCode
        salesId
                comboId
        installId     //手机安装  新装是1，加装是2
                netInstallId  //宽带安装  新装是1，加装是2
        num           //手机新装的号码
                oldAccNbrType //手机类型码：9
        netNum        //宽带新装的号码
                oldNetNumType //宽带类型码  5
        accNbr*/
        String broadband = "";
        if (broadbandview.getVisibility() == View.VISIBLE) {
            broadband = "new";
            if (broadband_rb2.isChecked()) {
                broadband = "add";
            }
        }
        //如果宽带选择加装，则宽带号码不能为空
        if (broadband.equals("add")) {
            if (broadbandNumber.equals("")) {
                Toast.makeText(this, "请验证宽带号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String phone = "";
        if (phoneview.getVisibility() == View.VISIBLE) {
            phone = "new";
            if (phone_rb2.isChecked()) {
                phone = "add";
            }
        }
        //如果手机选择加装，则手机号码不能为空
        if (phone.equals("add")) {
            if (phoneNumber.equals("")) {
                Toast.makeText(this, "请验证手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //如果需要选择号码，则选择号码不能为空
        if (choose_number_view.getVisibility() == View.VISIBLE) {
            if (choosedNumber.equals("")) {
                Toast.makeText(this, "请选择手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String contractmode = "";
        if (contractmodeview.getVisibility() == View.VISIBLE) {
            contractmode = "new";
            if (contractmode_rb2.isChecked()) {
                contractmode = "add";
            }
        }
        //如果合约机入网方式选择加装，则号码不能为空
        if (contractmode.equals("add")) {
            if (contractmodeAddNumber.equals("")) {
                Toast.makeText(this, "请验证号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String selectItvId = "";
        String selectItvComboId = "";
        if (ITV_view.getVisibility() == View.VISIBLE) {
            if (ITVType.equals("none") && ITVId.equals("")) {
                Toast.makeText(this, "请选择ITV套餐", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ITVType.equals("none")) {
                selectItvId = "0";
            } else if (ITVType.equals("new")) {
                selectItvId = "1";
            } else if (ITVType.equals("modify")) {
                selectItvId = "2";
            }
            selectItvComboId = ITVId;
        }

        new BaseTask<String, String, JSONObject>(this, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().calServiceFee(ProductDetailActivity.this, initFeeData());
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            protected void onSuccess(JSONObject result) {
                String content = "";
                if (result.optString("state").equals("1")) {
                    Double allFee = result.optDouble("prodFee") + result.optDouble("comboFee") + result.optDouble("otherFee");
                    Double prodFee = result.optDouble("prodFee") + result.optDouble("otherFee");
                    String sellerLevel = result.optString("sellerLevel");
                    if (getSaleModeId().equals("2")) {//一次结
                        if (sellerLevel.equals("1")) {

                        } else {
                            allFee = allFee * result.optDouble("BL");
                        }
                        content = "亲，本次订单预计可一次性获得佣金:" + "<font color=#fc5828>" + df.format(allFee) + "</font>" + "元";
                    } else {//分月结
                        if (sellerLevel.equals("1")) {
                            content = "亲，本次订单预计可获得佣金:" + "<font color=#fc5828>" + df.format(allFee) + "</font>" + "元"
                                    + "<br>其中一次性支付终端利润预计:" + "<font color=#fc5828>" + df.format(prodFee) + "</font>" + "元"
                                    + "<br>业务揽收酬金请将按分公司佣金结算规则至分公司结算。";
                        } else {
                            prodFee = prodFee * result.optDouble("BL");
                            content = "        亲，本次订单预计可一次性获得终端利润：" + "<font color=#fc5828>" + df.format(prodFee) + "</font>" + "元";
                        }
                    }

                } else {
                    content = result.optString("desc");
                }


                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.money_dialog, null);
                TextView contentView = (TextView) view.findViewById(R.id.tv_content);
                contentView.setText(Html.fromHtml(content));
                final AlertDialog dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).setView(view).show();
                view.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }.execute("");


    }


    private Map<String, Object> initFeeData() {
        Map<String, Object> map = new HashMap<String, Object>();
        //合约机模板下的情况
        //此处要考虑合约机模板下，加装的类型。如果加装选择的是固话，则加装类型是固话的时候，则算新装，如果是宽带类型，则算加装宽带
        if (getTemplateId().equals(ProductDetailContract.templateId)) {
            String NSFselectNetInstallId = "";
            String NSFselectInstallId = "";
            String oldAccNbr = "";
            String oldAccNbrType = "";
            oldAccNbr = contractmodeAddNumber;
            if (contractmodeAddType.equals("broadband")) {
                NSFselectNetInstallId = "2";
                NSFselectInstallId = "1";
            } else {
                NSFselectInstallId = "1";
                NSFselectNetInstallId = "1";
            }
            if (contractmodeAddType.equals("broadband")) {
                oldAccNbrType = "5"; //类型码
            } else if (contractmodeAddType.equals("phone")) {
                oldAccNbrType = "9";
            } else if (contractmodeAddType.equals("fixedline")) {
                oldAccNbrType = "1";
            }


            //必须值
            map.put("goodsCode", goodsCode);//
            map.put("productCode", getProductCode());//
            map.put("salesId", getSaleModeId());//
            map.put("installId", NSFselectInstallId);
            map.put("netInstallId", NSFselectNetInstallId);
            map.put("accNbr", choosedNumber);
            //可选的值
            map.put("activityId", getContractId());//
            map.put("comboId", contractPackageId);
            //已有号码的值
            map.put("num", oldAccNbr);
            map.put("oldAccNbrType", oldAccNbrType);
        }

        //固话宽带模板
        if (getTemplateId().equals(ProductDetailBroadband.templateId)) {
            String oldAccNbr = phoneNumber;
            String oldAccNbrType = "9";
            String netNum = broadbandNumber; //宽带
            String oldNetNumType = "5";
            String selectInstallId = "";
            if (phoneview.getVisibility() == View.VISIBLE) {
                if (phone_rb2.isChecked()) {
                    selectInstallId = "2";
                } else {
                    selectInstallId = "1";
                }
            }
            String selectNetId = "";
            if (broadbandview.getVisibility() == View.VISIBLE) {
                if (broadband_rb2.isChecked()) {
                    selectNetId = "2";
                } else {
                    selectNetId = "1";
                }
            }
            //必须值
            map.put("goodsCode", goodsCode);//
            map.put("productCode", getProductCode());//
            map.put("salesId", getSaleModeId());//
            map.put("installId", selectInstallId);
            map.put("netInstallId", selectNetId);

            //已有号码的值
            map.put("num", oldAccNbr);
            map.put("oldAccNbrType", oldAccNbrType);
            map.put("netNum", netNum);
            map.put("oldNetNumType", oldNetNumType);
        }

        String selectItvId = "";
        String selectItvComboId = "";
        if (ITV_view.getVisibility() == View.VISIBLE) {
            if (ITVType.equals("none")) {
                selectItvId = "0";
            } else if (ITVType.equals("new")) {
                selectItvId = "1";
            } else if (ITVType.equals("modify")) {
                selectItvId = "2";
            }
            selectItvComboId = ITVId;
        }
        map.put("selectItvId", selectItvId);//
        map.put("selectItvComboId", selectItvComboId);//

        return map;
    }

    private void cheakbuyLimit() {
        new BaseTask<String, String, JSONObject>(ProductDetailActivity.this, "正在校验商品购买限制，请稍后...") {

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                boolean isBuyLimit = jsonObject.optBoolean("isBuy");//购买限制
                if (!isBuyLimit) {
                    Toast.makeText(ProductDetailActivity.this, jsonObject.optString("desc"), Toast.LENGTH_LONG).show();
                    return;
                }
                submit();
            }

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().cheakbuyLimit(ProductDetailActivity.this, goodsCode);
            }
        }.execute("");
    }


    public void doSubmit(View v) {
        cheakbuyLimit();
    }

    private void submit() {
        String fixedline = "";
        if (fixedlineview.getVisibility() == View.VISIBLE) {
            fixedline = "none";
            if (fixedline_rb1.isChecked()) {
                fixedline = "new";
            }
            if (fixedline_rb2.isChecked()) {
                fixedline = "add";
            }
        }
        //如果固话选择加装，则固话号码不能为空
        if (fixedline.equals("add")) {
            if (fixedlineNumber.equals("")) {
                Toast.makeText(this, "请验证固话号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String broadband = "";
        if (broadbandview.getVisibility() == View.VISIBLE) {
            broadband = "new";
            if (broadband_rb2.isChecked()) {
                broadband = "add";
            }
        }
        //如果宽带选择加装，则宽带号码不能为空
        if (broadband.equals("add")) {
            if (broadbandNumber.equals("")) {
                Toast.makeText(this, "请验证宽带号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String phone = "";
        if (phoneview.getVisibility() == View.VISIBLE) {
            phone = "new";
            if (phone_rb2.isChecked()) {
                phone = "add";
            }
        }
        //如果手机选择加装，则手机号码不能为空
        if (phone.equals("add")) {
            if (phoneNumber.equals("")) {
                Toast.makeText(this, "请验证手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String contractmode = "";
        if (contractmodeview.getVisibility() == View.VISIBLE) {
            contractmode = "new";
            if (contractmode_rb2.isChecked()) {
                contractmode = "add";
            }
        }
        //如果合约机入网方式选择加装，则号码不能为空
        if (contractmode.equals("add")) {
            if (contractmodeAddNumber.equals("")) {
                Toast.makeText(this, "请验证号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String guarantee = "";
        if (guaranteeview.getVisibility() == View.VISIBLE) {
            guarantee = "none";
            if (guarantee_rb2.isChecked()) {
                guarantee = "fixedline";
            }
            if (guarantee_rb3.isChecked()) {
                guarantee = "broadband";
            }
        }
        //如果担保选择固话担保或宽带担保，则担保号码不能为空
        if (guarantee.equals("fixedline") || guarantee.equals("broadband")) {
            if (guaranteeNumber.equals("")) {
                Toast.makeText(this, "请验证担保号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //合帐
        String hezhang = "";
        if (tr_contractAccount.getVisibility() == View.VISIBLE) {
            hezhang = "no";
            if (rg_contractAccount_2.isChecked()) {
                hezhang = "yes";
            }
        }
        if (hezhang.equals("yes") && contractAccountNumber.equals("")) {
            Toast.makeText(this, "请验证合帐号码", Toast.LENGTH_SHORT).show();
            return;
        }

        //如果需要选择号码，则选择号码不能为空
        if (choose_number_view.getVisibility() == View.VISIBLE) {
            if (choosedNumber.equals("")) {
                Toast.makeText(this, "请选择手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //
        //"ITVType":"none,new,modify",
        //selectItvId  0 = none,1 = new ,2 = modify
        String selectItvId = "";
        String selectItvComboId = "";
        if (ITV_view.getVisibility() == View.VISIBLE) {
            if (!ITVType.equals("none") && ITVId.equals("")) {
                Toast.makeText(this, "请选择ITV套餐", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ITVType.equals("none")) {
                selectItvId = "0";
            } else if (ITVType.equals("new")) {
                selectItvId = "1";
            } else if (ITVType.equals("modify")) {
                selectItvId = "2";
            }
            selectItvComboId = ITVId;
        }


        //检查库存
        int stockNumber = getStockNumber();
        if (stockNumber == 0) {
            Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("所选产品没有库存，无法购买。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            alertDialog.show();
            return;
        }

        Intent i;
        if (getTemplateId().equals(ProductDetailTerminal.templateId) || getTemplateId().equals(ProductDetailBatch.templateId)) {  //终端模板和批量售卖模板不需要上传照片
            i = new Intent(this, ProductInfoSubmitActivity.class);
        } else { //其他模板均需要上传照片
            i = new Intent(this, ProductImageSubmitActivity.class);
        }

        //20151027 终端模版（1010）的商品是否支持线下支付方式
        i.putExtra("isOfflinePay", isOfflinePay);


        i.putExtra("mode", mode);
        i.putExtra("speed", speed);
        i.putExtra("packageId", packageId);
        i.putExtra("goodsCode", goodsCode);

        //产品id
        i.putExtra("productCode", getProductCode());

        i.putExtra("fixedline", fixedline);
        i.putExtra("fixedlineNumber", fixedlineNumber);
        i.putExtra("fixedlineAddress", fixedlineAddress);
        i.putExtra("fixedlineContact", fixedlineContact);
        i.putExtra("broadband", broadband);
        i.putExtra("broadbandNumber", broadbandNumber);
        i.putExtra("broadbandAddress", broadbandAddress);
        i.putExtra("broadbandContact", broadbandContact);
        i.putExtra("phone", phone);
        i.putExtra("phoneNumber", phoneNumber);
        i.putExtra("phoneAddress", phoneAddress);
        i.putExtra("phoneContact", phoneContact);
        i.putExtra("contractmode", contractmode);
        i.putExtra("contractmodeAddType", contractmodeAddType);
        i.putExtra("contractmodeAddNumber", contractmodeAddNumber);
        i.putExtra("contractmodeAddress", contractmodeAddress);
        i.putExtra("contractmodeContact", contractmodeContact);
        i.putExtra("guarantee", guarantee);
        i.putExtra("guaranteeNumber", guaranteeNumber);
        i.putExtra("guaranteeAddress", guaranteeAddress);
        i.putExtra("guaranteeContact", guaranteeContact);

        i.putExtra("choosedNumber", choosedNumber);
        i.putExtra("choosedNumberTeleUse", choosedNumberTeleUse);
        i.putExtra("contractPackageId", contractPackageId);


        i.putExtra("selectItvId", selectItvId);
        i.putExtra("selectItvComboId", selectItvComboId);


        //模式（合约）
        i.putExtra("contractId", getContractId());

        //售卖方式
        i.putExtra("salemodeId", getSaleModeId());

        //串码
        String sn = ((EditText) findViewById(R.id.serialnumber)).getText().toString();
        i.putExtra("sn", sn);
        /* 20150120 改为非必填项
            if(sn.equals("") && (findViewById(R.id.serialnumberview).getVisibility() == View.VISIBLE)) {
			Toast.makeText(this, "请输入调拨串号CRM工号", Toast.LENGTH_LONG).show();
			return;
		}
		*/

        //购买数量
        int countNumber = getCountNumber();
        i.putExtra("count", countNumber + "");
        if (countNumber > stockNumber) {
            Toast.makeText(this, "购买数量超过库存数量", Toast.LENGTH_LONG).show();
            return;
        }
        if (buyLimit != 0 && countNumber > buyLimit) {
            Toast.makeText(this, "单次购买上限最大为" + buyLimit, Toast.LENGTH_LONG).show();
            return;
        }
        if (ProductDetailRechargeableCard.templateId.equals(getTemplateId()) && countNumber > 5) {
            Toast.makeText(this, "电子卡一次性最多只能购买5张", Toast.LENGTH_LONG).show();
            return;
        }

        i.putExtra("templateId", getTemplateId());

        //是否需要输入收货信息
        i.putExtra("requireLogistics", requireLogistics);

        //选号数据库id
        i.putExtra("databaseId", databaseId);

        //20150504 -- 合账号码要求也需要带入客户信息及老用户证件校验的逻辑，如同时存在的情况下的逻辑判断优先级为：担保>加装>合账。
        //其中加装：手机>宽带>固话
        if (hezhang.equals("yes")) {//合帐
            displayName = displayName_HZ;
            displayCitizenId = displayCitizenId_HZ;
            displayCitizenAddress = displayCitizenAddress_HZ;
            verifiedName = verifiedName_HZ;
            verifiedCitizenId = verifiedCitizenId_HZ;
            verifiedCitizenAddress = verifiedCitizenAddress_HZ;
        } else if (contractmode.equals("add")) {//合约机入网方式
            displayName = displayName_HY;
            displayCitizenId = displayCitizenId_HY;
            displayCitizenAddress = displayCitizenAddress_HY;
            verifiedName = verifiedName_HY;
            verifiedCitizenId = verifiedCitizenId_HY;
            verifiedCitizenAddress = verifiedCitizenAddress_HY;
        } else if (fixedline.equals("add")) {//固话
            displayName = displayName_GH;
            displayCitizenId = displayCitizenId_GH;
            displayCitizenAddress = displayCitizenAddress_GH;
            verifiedName = verifiedName_GH;
            verifiedCitizenId = verifiedCitizenId_GH;
            verifiedCitizenAddress = verifiedCitizenAddress_GH;
        } else if (broadband.equals("add")) {//宽带
            displayName = displayName_KD;
            displayCitizenId = displayCitizenId_KD;
            displayCitizenAddress = displayCitizenAddress_KD;
            verifiedName = verifiedName_KD;
            verifiedCitizenId = verifiedCitizenId_KD;
            verifiedCitizenAddress = verifiedCitizenAddress_KD;
        } else if (phone.equals("add")) {//手机
            displayName = displayName_SJ;
            displayCitizenId = displayCitizenId_SJ;
            displayCitizenAddress = displayCitizenAddress_SJ;
            verifiedName = verifiedName_SJ;
            verifiedCitizenId = verifiedCitizenId_SJ;
            verifiedCitizenAddress = verifiedCitizenAddress_SJ;
        } else if (guarantee.equals("fixedline") || guarantee.equals("broadband")) {//担保
            displayName = displayName_DB;
            displayCitizenId = displayCitizenId_DB;
            displayCitizenAddress = displayCitizenAddress_DB;
            verifiedName = verifiedName_DB;
            verifiedCitizenId = verifiedCitizenId_DB;
            verifiedCitizenAddress = verifiedCitizenAddress_DB;
        }

        //存合帐号码到订单中
        i.putExtra("contractAccountNumber", contractAccountNumber);
        i.putExtra("hezhang", hezhang);

        i.putExtra("displayName", displayName);
        i.putExtra("displayCitizenId", displayCitizenId);
        i.putExtra("displayCitizenAddress", displayCitizenAddress);
        i.putExtra("verifiedName", verifiedName);
        i.putExtra("verifiedCitizenId", verifiedCitizenId);
        i.putExtra("verifiedCitizenAddress", verifiedCitizenAddress);

        //20150304分销平台建档及流程调整需求 增加“老客户信息是否需要校验”的管理项
        i.putExtra("verifyOldInfo", verifyOldInfo);
        i.putExtra("modifyOcrName", modifyOcrName);
        i.putExtra("modifyOcrCarNum", modifyOcrCarNum);
        i.putExtra("modifyOcrAddress", modifyOcrAddress);
        i.putExtra("uploadCitizenPhoto", uploadCitizenPhoto);

        //校验批量售卖模板的购买限额
        if (getTemplateId().equals("1009")) {
//			if(dlsName.equals("电子渠道运营中心") && !CRApplication.getId(ProductDetailActivity.this).contains("CS")){
//				Toast.makeText(ProductDetailActivity.this,"对不起，您的账号属于电子渠道运营中心，暂时无法购买成品卡！",Toast.LENGTH_SHORT).show();
//				return ;
//			}else if(branchCompany.equals("电子渠道运营中心") && !CRApplication.getId(ProductDetailActivity.this).contains("CS")) {
//				Toast.makeText(ProductDetailActivity.this,"对不起，您当前登录的号码归属为电渠中心，不能购买成品卡类商品！",Toast.LENGTH_SHORT).show();
//				return ;
//			}else{
//
//			}

            cheakCardNum(countNumber, i);
        } else if (getTemplateId().equals(ProductDetailRechargeableCard.templateId)) {//如果是1003卡密模板，直接提交生成订单

            cardSaleSaveOrder(goodsCode, String.valueOf(countNumber), getProductCode());

        } else {
            startActivity(i);
        }
    }


    private void cardSaleSaveOrder(final String goodsCode, final String countNumber, final String productCode) {
        new BaseTask<String, String, JSONObject>(this, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().cardSaleSaveOrder(ProductDetailActivity.this, goodsCode, countNumber, productCode);
            }

            @Override
            protected void onSuccess(JSONObject result) {

                Intent i = new Intent(ProductDetailActivity.this, OrderResultActivity.class);
                i.putExtra("templateId", getTemplateId());
                i.putExtra("orderId", result.optString("orderId"));
                i.putExtra("goodsId", result.optString("goodsId"));
                i.putExtra("orderName", result.optString("orderName"));
                i.putExtra("needPay", result.optBoolean("needPay"));
                i.putExtra("payValue", result.optString("payValue"));
                i.putExtra("originalPrice", result.optString("originalPrice"));
                i.putExtra("buyNumber", result.optString("buyNumber"));
                i.putExtra("hasDiscount", result.optBoolean("hasDiscount"));
                i.putExtra("discountPrice", result.optString("discountPrice"));
                startActivity(i);
                ProductDetailActivity.this.finish();

            }

        }.execute("");
    }

    //
    private void cheakCardNum(int countNumber, final Intent i) {
        new BaseTask<String, String, Boolean>(this, "请稍后...") {

            @Override
            protected Boolean doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().cheakCardNum(ProductDetailActivity.this, params[0]);
            }

            @Override
            protected void onSuccess(Boolean result) {
                startActivity(i);
            }

        }.execute(String.valueOf(countNumber));

    }

    //购买数量
    public int getCountNumber() {
        String count = ((EditText) findViewById(R.id.count)).getText().toString();
        int countNumber = 1;
        try {
            countNumber = Integer.valueOf(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countNumber;
    }

    private String getProductCode() {
        String productCode = "";
        for (int j = 0; j < products_rg.getChildCount(); j++) {
            RadioButton radio = (RadioButton) products_rg.getChildAt(j);
            if (radio.isChecked()) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);
                    productCode = json.optString("productCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return productCode;
    }

    private String getContractId() {
        String contractId = "";
        for (int j = 0; j < contract_rg.getChildCount(); j++) {
            RadioButton radio = (RadioButton) contract_rg.getChildAt(j);
            if (radio.isChecked()) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);
                    contractId = json.optString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return contractId;
    }

    //库存数量
    private int getStockNumber() {
        int stockNumber = 0;
        for (int j = 0; j < products_rg.getChildCount(); j++) {
            RadioButton radio = (RadioButton) products_rg.getChildAt(j);
            if (radio.isChecked()) {
                try {
                    String jsonString = (String) radio.getTag();
                    JSONObject json = new JSONObject(jsonString);
                    stockNumber = json.optInt("stockNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return stockNumber;
    }

    abstract void updateProductPrice(String productCode, String salemode, String contractId);

    void updateDiscount(String productCode, int buyNumber) {
        String discountPrice = originalProductPrice;
        int discountNumber = 0;

        if (!hasDiscount) {
            return;
        }

        try {
            for (int i = 0; i < discountArray.length(); i++) {
                JSONObject json = discountArray.getJSONObject(i);
                if (json.optString("productCode").equals(productCode)) {
                    JSONArray discountPrices = json.getJSONArray("discountPrices");
                    for (int j = 0; j < discountPrices.length(); j++) {
                        int num = discountPrices.getJSONObject(j).getInt("number");
                        String price = discountPrices.getJSONObject(j).getString("price");
                        if (num >= discountNumber) {
                            discountNumber = num;
                        }
                        if (buyNumber >= discountNumber) {
                            discountPrice = price;
                        }
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*20150127修改内容：如果折扣价格等于原价，则只显示折扣价格，不显示原价*/
        /*start*/
        String originalProductPriceToDisplay = originalProductPrice;
        if (originalProductPriceToDisplay.equals(discountPrice)) {
            originalProductPriceToDisplay = "";
        }
        /*end*/
        SpannableString ss = new SpannableString("商品零售价: " + originalProductPriceToDisplay + " " + discountPrice + "元");
        ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), 6, 6 + originalProductPriceToDisplay.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StrikethroughSpan(), 6, 6 + originalProductPriceToDisplay.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) findViewById(R.id.price)).setText(ss);
    }

    private void updateContractPackage(String contractId) {

        new BaseTask<String, String, JSONArray>(this, "") {

            @Override
            protected JSONArray doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().contractpackage(ProductDetailActivity.this, arg0[0], arg0[1]);
            }

            @Override
            protected void onSuccess(JSONArray result) {
                contractPackage = result;

                contractPackageId = "";
                String contractPackageName = "选择套餐";

                if (contractPackage.length() > 0) {
                    //默认取第一个套餐
                    JSONObject json = contractPackage.optJSONObject(0);
                    contractPackageId = json.optString("id");
                    contractPackageName = json.optString("name");
                }
                contract_package_rb.setText(contractPackageName);

                //选择模式(合约)后，套餐会发生变化，而且价格也会变化，所以在更新套餐后还要更新价格
                updateProductPrice(getProductCode(), getSaleModeId(), getContractId());
            }
        }.execute(goodsCode, contractId);
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
        if (url.equals(lastOpenedNetworkImage)) {
            openLocalImage(path);
            return;
        }

        new BaseTask<String, String, String>(this, "打开图片中...") {

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

    private void openLocalImage(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "image/*");
        startActivity(intent);
    }

    public void doWebview(View v) {
        Intent i = new Intent(this, ProductDetailWebviewActivity.class);
        i.putExtra("goodsCode", goodsCode);
        i.putExtra("productCode", getProductCode());
        startActivity(i);
        overridePendingTransition(R.anim.activity_open, 0);
    }


    //监听向上滑动手势
    GestureDetector activityGuestureDetector;

    private void initGestureDetector() {

        //监听MyScrollView的滑动事件
        MyScrollView mysv = (MyScrollView) findViewById(R.id.mysv);
        mysv.setOnScrollToBottomLintener(new OnScrollToBottomListener() {
            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom) {
                    count++;
                    if (count > 10) {
                        count = 0;

                        if (detailview.getVisibility() == View.VISIBLE) {
                            doWebview(null);
                        }
                    }
                }
            }
        });

        //监听Activity的滑动事件
        activityGuestureDetector = new GestureDetector(this, new MyGestureDetector());
    }

    class MyGestureDetector implements OnGestureListener {

        @Override
        public boolean onDown(MotionEvent arg0) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getY() - e2.getY() > 200) {

                if (detailview.getVisibility() == View.VISIBLE) {
                    doWebview(null);
                }
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent arg0) {
        }

        @Override
        public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent arg0) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent arg0) {
            return false;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        activityGuestureDetector.onTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }


    public void call(View view) {
        if (businePhone == null || businePhone.length == 0) {
            Toast.makeText(this, "不好意思，商家电话暂时没有哦", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, businePhone);
        new AlertDialog.Builder(this).setTitle("请选择商家号码").setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + businePhone[which]));
                startActivity(intent);
                dialog.dismiss();
            }
        }).show();
    }


}
