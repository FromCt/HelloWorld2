package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.ChooseContractPackageActivity;
import com.crunii.android.fxpt.activity.ChooseNumberActivity;
import com.crunii.android.fxpt.business.Item;
import com.crunii.android.fxpt.business.TibmGoodsPlan;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 固宽模版  1008
 * Created by speedingsnail on 15/12/1.
 */
public class CombinGoodsBroadbandActivity extends CombinGoodsDetialBase {

    public final static String templateId = "1008";
    final static int FIXEDLINE_VERIFY = 1;//固话
    final static int BROADBAND_VERIFY = 2;//宽带
    final static int PHONE_VERIFY = 3;//手机
    final static int CHOOSE_NUMBER = 4;//选择号码
    final static int CHOOSE_ITV = 5;
    final static int CONTRACTACCOUNT_VERIFY = 6;//合账
    private CreateOrderDemo createOrderDemo = new CreateOrderDemo();
    private NumberVerifyResult fixedlineResult = new NumberVerifyResult();
    private NumberVerifyResult broadbandResult = new NumberVerifyResult();
    private NumberVerifyResult phoneResult = new NumberVerifyResult();
    private NumberVerifyResult contractAccountResult = new NumberVerifyResult();
    private NumberVerifyResult finalCustomerResult = new NumberVerifyResult();
    //顶部轮播图
    ViewPager  adBannerVp ;
    LinearLayout adBannerDots ;
    AdsUtil adsUtil ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_productdetail_broadband);

         Bundle bundle = getIntent().getExtras();
        String CRMId = bundle.getString("CRMId");    //bundle.getString("CRMId", "");
        String goodsCode = bundle.getString("goodsCode"); //bundle.getString("goodsCode", "");
        createOrderDemo.CRMId = CRMId;
        createOrderDemo.goodsCode = goodsCode;
        getProductDetailData(goodsCode, CRMId);
    }

    @Override
    public void initData(ProductDetailResultDemo productDetailResultDemo) {

        View detailview = findViewById(R.id.detailview);
        initGestureDetector(detailview, createOrderDemo);
        detailview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWebview(v, createOrderDemo);
            }
        });
        //顶部轮播图
        adsUtil = new AdsUtil(mContext);
        adBannerVp = (android.support.v4.view.ViewPager) findViewById(R.id.home_ad_vp);
        adBannerDots = (LinearLayout) findViewById(R.id.home_ad_index_ll);



        //商品名字
                ((TextView) findViewById(R.id.name)).setText(productDetailResultDemo.name);
        //未选择产品时的价格
        ((TextView) findViewById(R.id.price)).setText("用户支付金额:" + productDetailResultDemo.price + "元");

        //主产品
        ProductChangeListener productChangeListener = new ProductChangeListener();
        RadioGroup products_rg = (RadioGroup) findViewById(R.id.products_rg);
        int productsCount = productDetailResultDemo.products.size();
        for (int i = 0; i < productsCount; i++) {
            ProductDetailResultDemo.Product product = productDetailResultDemo.products.get(i);
            RadioButton radioButton = (RadioButton) View.inflate(mContext, R.layout.radiobutton, null);
            radioButton.setText(product.name);
            radioButton.setTag(product);
            radioButton.setOnClickListener(productChangeListener);
            products_rg.addView(radioButton);
        }
        //默认选择第一个主产品
        if (productsCount > 0) {
            RadioButton radioButton = (RadioButton) products_rg.getChildAt(0);
            radioButton.setChecked(true);
            ProductDetailResultDemo.Product product = (ProductDetailResultDemo.Product)radioButton.getTag();
            createOrderDemo.productCode = product.productCode;
            adsUtil.updateTopAds(product.detailimg,adBannerVp,adBannerDots);
        }
        if(productDetailResultDemo.fixedline.equals("")){
            findViewById(R.id.fixedlineview).setVisibility(View.GONE);
        }else{
            findViewById(R.id.fixedlineview).setVisibility(View.VISIBLE);
            List<ItemCommon> fixedlineList = paintView(PaintType.FIXEDLINE, productDetailResultDemo.fixedline);
            initRaido(fixedlineList, R.id.fixedline_rg, new FixedlineOnClickListener());
            if(fixedlineList.size() > 0){
                createOrderDemo.fixedline = fixedlineList.get(0).id;
            }
        }
        if(productDetailResultDemo.broadband.equals("")){
            findViewById(R.id.broadbandview).setVisibility(View.GONE);
        }else {
            findViewById(R.id.broadbandview).setVisibility(View.VISIBLE);
            List<ItemCommon> broadbandList = paintView(PaintType.BROADBAND, productDetailResultDemo.broadband);
            initRaido(broadbandList, R.id.broadband_rg, new BroadbandOnClickListener());
            if(broadbandList.size() > 0){
                createOrderDemo.broadband = broadbandList.get(0).id;
            }
        }
        if(productDetailResultDemo.phone.equals("")){
            findViewById(R.id.phoneview).setVisibility(View.GONE);
        }else {
            findViewById(R.id.phoneview).setVisibility(View.VISIBLE);
            List<ItemCommon> phoneList = paintView(PaintType.PHONE, productDetailResultDemo.phone);
            initRaido(phoneList, R.id.phone_rg, new PhoneOnClickListener());
            if (phoneList.size() > 0) {
                createOrderDemo.phone = phoneList.get(0).id;
            }
        }

        if(productDetailResultDemo.ITVType.equals("")){
            findViewById(R.id.ITV_view).setVisibility(View.GONE);
        }else {
            findViewById(R.id.ITV_view).setVisibility(View.VISIBLE);
            List<ItemCommon> itvList = paintView(PaintType.ITV, productDetailResultDemo.ITVType);
            initRaido(itvList, R.id.ITV_rg, new ITVOnClickListener());
            if(itvList.size() > 0){
                createOrderDemo.selectItvId = itvList.get(0).id;
            }
        }

        if(productDetailResultDemo.contractAccountType.equals("")){
            findViewById(R.id.tr_contractAccount).setVisibility(View.GONE);
        }else {
            findViewById(R.id.tr_contractAccount).setVisibility(View.VISIBLE);
            List<ItemCommon> contractAccountList = paintView(PaintType.CONTRACTACCOUNTTYPE, productDetailResultDemo.contractAccountType);
            initRaido(contractAccountList, R.id.rg_contractAccount, new ContractAccountOnClickListener());
            if(contractAccountList.size() > 0){
                createOrderDemo.broadband = contractAccountList.get(0).id;
            }
        }

        if(!productDetailResultDemo.choooseNumberDemo.needChooseNumber){
            findViewById(R.id.choose_number_view).setVisibility(View.GONE);
        }else{
            findViewById(R.id.choose_number_view).setVisibility(View.VISIBLE);
            ((RadioButton)findViewById(R.id.choose_number_rb)).setOnClickListener(new ChooseNumberOnClickListener());
        }

        ((Button) findViewById(R.id.doSubmit)).setOnClickListener(new SubmitOnClickListener());



    }

    /**
     * 固话点击事件
     */
    private class FixedlineOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();

            switch (value) {
                //固话
                case CombinConstant.Fixedline.NONE://无需固话
                    createOrderDemo.fixedline = CombinConstant.Fixedline.NONE;
                    break;

                case CombinConstant.Fixedline.NEW://新用户入网
                    createOrderDemo.fixedline = CombinConstant.Fixedline.NEW;
                    break;

                case CombinConstant.Fixedline.ADD://老用户加装
                    createOrderDemo.fixedline = CombinConstant.Fixedline.ADD;
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_JZ,
                            NumberVerifyResult.verifyType.fixedline.toString(), broadbandResult);
                    verifyNumber(bundle, FIXEDLINE_VERIFY);
                    break;
            }
        }
    }

    /**
     * 宽带点击事件
     */
    private class BroadbandOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value) {
                case CombinConstant.Broadband.NEW: //新用户入网
                    createOrderDemo.broadband = CombinConstant.Broadband.NEW;
                    break;

                case CombinConstant.Broadband.ADD://老用户加装
                    createOrderDemo.broadband = CombinConstant.Broadband.ADD;
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_JZ,
                            NumberVerifyResult.verifyType.broadband.toString(), broadbandResult);
                    verifyNumber(bundle, BROADBAND_VERIFY);
                    break;
            }
        }
    }

    /**
     * 手机点击事件
     */
    private class PhoneOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value) {
                case CombinConstant.Phone.NEW://新用户入网
                    createOrderDemo.phone = CombinConstant.Phone.NEW;
                    break;
            }
        }
    }

    /**
     * 选择号码
     */
    private class ChooseNumberOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            chooseNumber(CHOOSE_NUMBER);
        }
    }

    /**
     * ITV
     */
    private class ITVOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value) {
               case CombinConstant.ITV.NONE:
                    createOrderDemo.selectItvId = CombinConstant.ITV.NONE;
                    break;
                case CombinConstant.ITV.NEW:
                    createOrderDemo.selectItvId = CombinConstant.ITV.NEW;
                    chooseITV();
                    break;
                case CombinConstant.ITV.MODIFY:
                    createOrderDemo.selectItvId = CombinConstant.ITV.MODIFY;
                    chooseITV();
                    break;
            }
        }
    }

    private void chooseITV(){
        Intent i = new Intent(this, CombinChooseContractPackageActivity.class);
        i.putExtra("ITV",productDetailResultDemo.ITV);//
        startActivityForResult(i, CHOOSE_ITV);
    }

    /**
     * 合账
     */
    private class ContractAccountOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value) {
                case CombinConstant.ContractAccountType.NO:
                    createOrderDemo.contractAccount = CombinConstant.ContractAccountType.NO;
                    break;
                case CombinConstant.ContractAccountType.YES:
                    createOrderDemo.contractAccount = CombinConstant.ContractAccountType.YES;
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_HZ,
                            NumberVerifyResult.verifyType.contractAccount.toString(), contractAccountResult);
                    verifyNumber(bundle, CONTRACTACCOUNT_VERIFY);
                    break;
            }
        }
    }


    public enum PaintType {
        FIXEDLINE, BROADBAND, PHONE,ITV,CONTRACTACCOUNTTYPE
    }

    private List<ItemCommon> paintView(PaintType flag, String value) {
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        switch (flag) {
            case FIXEDLINE:
                list = CombinConstant.getFixedlineCommons(value);
                break;
            case BROADBAND:
                list = CombinConstant.getBroadband(value);
                break;
            case PHONE:
                list = CombinConstant.getPhone(value);
                break;
            case ITV:
              list = CombinConstant.getITV(value);
                break;
            case CONTRACTACCOUNTTYPE:
                list = CombinConstant.getContractAccountType(value);
                break;
        }
        return list;
    }

    private class ProductChangeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof ProductDetailResultDemo.Product) {
                ProductDetailResultDemo.Product product = (ProductDetailResultDemo.Product) v.getTag();
                updateProductPrice(product.productCode, "");
                //更新顶部广告图
                // updateTopAds(json);
                createOrderDemo.productCode = product.productCode;
                //切换轮播图
                adsUtil.updateTopAds(product.detailimg,adBannerVp,adBannerDots);
            }
        }
    }

    @Override
    public String setTemplateId() {
        return templateId;
    }

    @Override
    public void updatePriceView(String price) {
        ((TextView) findViewById(R.id.price)).setText("price");
    }



    //startActivityForResult 回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FIXEDLINE_VERIFY://固话验证号码
                    fixedlineResult = initNumberVerifyResult(data);
                    createOrderDemo.fixedlineNumber = fixedlineResult.number;
                    break;

                case BROADBAND_VERIFY://宽带验证号码
                    broadbandResult = initNumberVerifyResult(data);
                    createOrderDemo.broadbandNumber = broadbandResult.number;
                    break;

                case PHONE_VERIFY://手机验证号码
                    phoneResult = initNumberVerifyResult(data);
                    createOrderDemo.phoneNumber = phoneResult.number;
                    break;

                case CHOOSE_NUMBER://选择号码
                    String number = data.getExtras().getString("number");
                    String tele_use = data.getExtras().getString("tele_use");
                    createOrderDemo.choosedNumber = number;
                    createOrderDemo.choosedNumberTeleUse = tele_use;
                    ((RadioButton)findViewById(R.id.choose_number_rb)).setText(number);
                    break;

                case CHOOSE_ITV://选择ITV
                    createOrderDemo.selectItvComboId = data.getExtras().getString("contractPackageId");
                    String desc = "";
                    for(int i = 0; i < productDetailResultDemo.ITV.size(); i++){
                        ProductDetailResultDemo.Common common = productDetailResultDemo.ITV.get(i);
                        desc = common.desc;
                    }
                    ((TextView)findViewById(R.id.tv_itv_desc)).setText(desc);
                    break;

                case CONTRACTACCOUNT_VERIFY:
                    contractAccountResult = initNumberVerifyResult(data);
                    createOrderDemo.contractAccountNumber = contractAccountResult.number;
                    break;
            }

        }
    }

    private class SubmitOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            Toast.makeText(mContext, createOrderDemo.productCode + "--"+createOrderDemo.fixedline + "--"+ createOrderDemo.broadband + "--" + createOrderDemo.phone, Toast.LENGTH_SHORT).show();
            if(createOrderDemo.fixedline.equals(CombinConstant.Fixedline.ADD) && createOrderDemo.fixedlineNumber.equals("")){
                Toast.makeText(mContext, "请验证固话号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if(createOrderDemo.broadband.equals(CombinConstant.Fixedline.ADD) && createOrderDemo.broadbandNumber.equals("")){
                Toast.makeText(mContext, "请验证宽带号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if((createOrderDemo.selectItvId.equals(CombinConstant.ITV.NEW)
                    || createOrderDemo.selectItvId.equals(CombinConstant.ITV.MODIFY)) && createOrderDemo.selectItvComboId.equals("")){
                Toast.makeText(mContext, "请选择ITV套餐", Toast.LENGTH_SHORT).show();
                return;
            }
            if(createOrderDemo.contractAccount.equals(CombinConstant.ContractAccountType.YES) && createOrderDemo.contractAccountNumber.equals("")){
                Toast.makeText(mContext, "请验证合帐号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if(productDetailResultDemo.choooseNumberDemo.needChooseNumber && createOrderDemo.choosedNumber.equals("")){
                Toast.makeText(mContext, "请选择甩单选号", Toast.LENGTH_SHORT).show();
                return;
            }

            //客户信息及老用户证件校验逻辑
            initCustomer();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("系统提示")
                    .setMessage("您选择的是分公司甩单，平台支付金额由线下支付给分公司。")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext,CombinGoodsImageSubmitActivity.class);
                            intent.putExtra("createOrderDemo",createOrderDemo);
                            intent.putExtra("productDetailResultDemo",productDetailResultDemo);
                            intent.putExtra("numberVerifyResult",finalCustomerResult);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initCustomer() {
        //20150504 -- 合账号码要求也需要带入客户信息及老用户证件校验的逻辑，如同时存在的情况下的逻辑判断优先级为：担保>加装>合账。
        //其中加装：手机>宽带>固话
        if(createOrderDemo.contractAccount.equals(CombinConstant.ContractAccountType.YES)){//合帐
            finalCustomerResult = contractAccountResult;
        }else if(createOrderDemo.fixedline.equals(CombinConstant.Fixedline.ADD)){//固话
            finalCustomerResult = fixedlineResult;
        }else if(createOrderDemo.broadband.equals(CombinConstant.Broadband.ADD)){//宽带
            finalCustomerResult = broadbandResult;
        }else if(createOrderDemo.phone.equals(CombinConstant.Phone.ADD)) {//手机
            finalCustomerResult = phoneResult;
        }
    }


    public void call(View view){
        ArrayList<String> businePhone = productDetailResultDemo.businePhone;
        if (businePhone.size() == 0) {
            Toast.makeText(this, "不好意思，商家电话暂时没有哦", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, businePhone);
        new AlertDialog.Builder(this).setTitle("请选择商家号码").setSingleChoiceItems(itemAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + productDetailResultDemo.businePhone.get(which)));
                startActivity(intent);
                dialog.dismiss();
            }
        }).show();
    }

}
