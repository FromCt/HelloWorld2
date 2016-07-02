package com.crunii.android.fxpt.combinationGoodsActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.crunii.android.fxpt.base.SetValue;
import com.crunii.android.fxpt.business.TibmGoodsPlan;
import com.crunii.android.fxpt.util.Constant;

import org.json.JSONArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 合约模版  1001
 * Created by speedingsnail on 15/12/1.
 */
public class CombinGoodsContractActivity extends CombinGoodsDetialBase {

     final static String templateId = "1001";

    private NumberVerifyResult finalCustomerResult = new NumberVerifyResult();
    private CreateOrderDemo createOrderDemo = new CreateOrderDemo();
    final static int CHOOSE_NUMBER = 4;//选择号码
    final static int CONTRACTACCOUNT_VERIFY=1;//合账
    final static int CONTRACT_NUMBER=2;//新用户入网
    final static int CHOOSE_CONTRACT_PACKAGE_REQUEST=3;//选择套餐
    final static int ITV=5;//ITV
    final static int GUARANTEE=6;//担保

    private RadioGroup products_rg;//商品 RadioGroup
    private View serialnumberview;//
    private RadioButton contract_package_rb;//选择套餐 button
    private TextView text_goodPrice;//商品价格
    private TextView text_stock;//商品库存


    private View contractmodeview;//入网方式
    private View contractAccount;//合账方式
    private View choose_number_rg;//选择号码

    private String goodsCode;
    private String contractPackageId;//套餐id

    private NumberVerifyResult contractResult = new NumberVerifyResult();
    private NumberVerifyResult  contractAccountResult=new NumberVerifyResult();
    private NumberVerifyResult  guaranteeResult=new NumberVerifyResult();

    //顶部轮播图
    ViewPager adBannerVp ;
    LinearLayout adBannerDots ;
    AdsUtil adsUtil ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_productdetail_contract);

        serialnumberview = findViewById(R.id.serialnumberview);
        contract_package_rb = (RadioButton) findViewById(R.id.contract_package_rb);
        contractmodeview= findViewById(R.id.contractmodeview);
        contractAccount=  findViewById(R.id.tr_contractAccount);
        choose_number_rg =  findViewById(R.id.choose_number_rg);

        Intent intent = getIntent();



        String CRMId = intent.getStringExtra("CRMId");
        goodsCode = intent.getStringExtra("goodsCode");

        if(CRMId==null||goodsCode==null){
            goodsCode = "M1291C1001-1002S20150423100298";
             CRMId="QDCB1ZGY1&goodsCode=M1291C1001-1002S20150423100298";
        }
        if(Constant.TEST_FLAG)
        myToast.show("goodsCode===" + goodsCode + "-----crmid===" + CRMId);

        getProductDetailData(goodsCode, CRMId);

        createOrderDemo.goodsCode=goodsCode;
        createOrderDemo.salemodeId="2";
        createOrderDemo.CRMId = CRMId;
        ((Button) findViewById(R.id.doSubmit)).setOnClickListener(new SubmitOnClickListener());

    }

    private class SubmitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //入网方式 提交验证
            if(contractmodeview.getVisibility()==View.VISIBLE){
                if(createOrderDemo.contractmode.equals(CombinConstant.Contractmode.ADD) &&
                        (createOrderDemo.contractmodeAddNumber==null||createOrderDemo.contractmodeAddNumber.equals("")
            ||createOrderDemo.contractmodeAddType==null||createOrderDemo.contractmodeAddType.equals(""))){
                    myToast.show("请验证入网方式号码");
                    return;
                }
            }

            //担保方式验证
            if(createOrderDemo.guarantee!=null&&(createOrderDemo.guarantee.equals("fixedline") || createOrderDemo.guarantee.equals("broadband"))) {
                if(createOrderDemo.guaranteeNumber.equals("")) {
                    myToast.show("请验证担保号码");
                    return;
                }
            }

            //合账方式 提交验证
            if(contractAccount.getVisibility()==View.VISIBLE){
                if(createOrderDemo.contractAccount.equals(CombinConstant.ContractAccountType.YES) && createOrderDemo.contractAccountNumber.equals("")){
                    myToast.show("请验证合帐号码");
                    return;
                }
            }


            //选择号码 提交验证
            if( choose_number_rg.getVisibility()==View.VISIBLE&&(createOrderDemo.number.equals("")|| createOrderDemo.choosedNumberTeleUse.equals(""))){
                myToast.show("请确定选择号码");
                return;
            }

            //iITV 提交验证

            if(findViewById(R.id.ITV_view).getVisibility() == View.VISIBLE) {
                if((createOrderDemo.selectItvId.equals(CombinConstant.ITV.NEW)
                        || createOrderDemo.selectItvId.equals(CombinConstant.ITV.MODIFY)) && createOrderDemo.selectItvComboId.equals("")){
                    myToast.show("请选择ITV套餐");
                    return;
                }
            }

            //合作工号
            createOrderDemo.sn=((EditText)findViewById(R.id.serialnumber)).getText().toString();


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

    private void initCustomer() {
        //20150504 -- 合账号码要求也需要带入客户信息及老用户证件校验的逻辑，如同时存在的情况下的逻辑判断优先级为：担保>加装>合账。
        //其中加装：手机>宽带>固话
        if(createOrderDemo.contractAccount.equals(CombinConstant.ContractAccountType.YES)){//合帐
            finalCustomerResult = contractAccountResult;
        }else if(createOrderDemo.contractmode.equals("add")){//合约机入网方式
            finalCustomerResult = contractResult;
        }else if(createOrderDemo.guarantee.equals("fixedline") || createOrderDemo.guarantee.equals("broadband")){//担保
            finalCustomerResult = guaranteeResult;
        }

    }

    @Override
    public void initData(final ProductDetailResultDemo productDetailResultDemo) {

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

        TextView text_goodName = (TextView) findViewById(R.id.name);
        SetValue.setText(text_goodName, productDetailResultDemo.name);
         text_goodPrice = (TextView) findViewById(R.id.price);
        SetValue.setText(text_goodPrice,"用户支付金额:" + productDetailResultDemo.price + "元");
         text_stock= (TextView) findViewById(R.id.stock);
        SetValue.setText(text_stock, productDetailResultDemo.stock);

        //选择产品
        ProductChangeListener productChangeListener = new ProductChangeListener();
        products_rg = (RadioGroup)findViewById(R.id.products_rg);
        int productsCount = productDetailResultDemo.products.size();

        for(int i = 0; i < productsCount; i ++){
            ProductDetailResultDemo.Product product = productDetailResultDemo.products.get(i);
            RadioButton radioButton = (RadioButton)View.inflate(mContext,R.layout.radiobutton,null);
            radioButton.setText(product.name);
            radioButton.setTag(product);
            radioButton.setOnClickListener(productChangeListener);
            products_rg.addView(radioButton);
        }
        //默认选择第一个主产品
        if(productsCount > 0){
            ((RadioButton)products_rg.getChildAt(0)).setChecked(true);
            createOrderDemo.productCode = productDetailResultDemo.products.get(0).productCode;
            updateSnView(productDetailResultDemo.products.get(0).requireSn);//是否展示合作工号
            adsUtil.updateTopAds(productDetailResultDemo.products.get(0).detailimg,adBannerVp,adBannerDots);
        }

        //默认终端串号调拨合作伙伴门户工号
        ((EditText)findViewById(R.id.serialnumber)).setText(productDetailResultDemo.defaultSn);


        //入网方式
        List<ItemCommon> contractmodeList = paintView(PaintType.contractmode, productDetailResultDemo.contractmode);
        if(contractmodeList.size() > 0){
            initRaido(contractmodeList, R.id.contractmode_rg, new contractmodeOnClickListener());
            createOrderDemo.contractmode=contractmodeList.get(0).id;
        }else{
            contractmodeview.setVisibility(View.GONE);

        }


        //担保方式
        List<ItemCommon> guaranteeList = paintView(PaintType.guarantee, productDetailResultDemo.guarantee);
        if(guaranteeList.size() > 0){
            findViewById(R.id.guaranteeview).setVisibility(View.VISIBLE);
            initRaido(guaranteeList, R.id.guarantee_rg, new guaranteeOnClickListener());
            createOrderDemo.guarantee=guaranteeList.get(0).id;
        }else{
            findViewById(R.id.guaranteeview).setVisibility(View.GONE);
        }


        //合账方式   "contractAccountType":"no,yes",               //合帐方式（不合帐,合帐）
        List<ItemCommon> contractAccountTypeList = paintView(PaintType.contractAccountType, productDetailResultDemo.contractAccountType);
        if(contractAccountTypeList.size() > 0){
            contractAccount.setVisibility(View.VISIBLE);
            initRaido(contractAccountTypeList,R.id.rg_contractAccount,new contractAccountTypeOnClickListener());
        }else{
            contractAccount.setVisibility(View.GONE);
        }

        //选择号码
        RadioButton choose_number_Button= (RadioButton) findViewById(R.id.choose_number_rb);
        choose_number_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CombinGoodsContractActivity.this,CombinChooseNumber.class);
                Bundle bundle = new Bundle();
                //bundle.
                        //productDetailResultDemo.sec
                intent.putExtras(bundle);
                       startActivity(intent);
            }
        });


        //选择模式
        if(productDetailResultDemo.contract.size() == 0) {
            findViewById(R.id.contract_view).setVisibility(View.GONE);
            Log.i("contract", "productDetailResultDemo.contract.size() == 0   contract_view).setVisibility(View.GONE ");
        } else {
            RadioGroup contract_rg = (RadioGroup) findViewById(R.id.contract_rg);

            for (int i = 0; i < productDetailResultDemo.contract.size(); i++) {
                RadioButton radio = (RadioButton) View.inflate(this, R.layout.radiobutton, null);
                final ProductDetailResultDemo.Common comm = productDetailResultDemo.contract.get(i);
                radio.setText(comm.name);
                radio.setTag(comm.id);
                Log.i("contract", "set contract radio onclickListener");
                radio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id= (String) v.getTag();

                        if(id==null){
                            Log.i("contract radio click", "contractId===null");
                        }else{
                            if(!createOrderDemo.contractId.equals(id)){
                                Log.i("选择模式", "createOrderDemo.contractId==="+createOrderDemo.contractId);
                                createOrderDemo.contractId=id;
                                //选择模式（合约）后，相应的套餐也要发生变化
                                updateContractPackage(id);
                            }else{
                                Log.i("选择模式", "contractId don't change ");
                            }

                        }
                    }
                });
                contract_rg.addView(radio);
            }
            //默认选择第一种模式(合约)
            ((RadioButton) contract_rg.getChildAt(0)).setChecked(true);
            createOrderDemo.contractId=productDetailResultDemo.contract.get(0).id;
            //刷新该模式下对应的套餐。
            updateContractPackage(createOrderDemo.contractId);

        }



        //选择套餐
        setContrctPackage();
        contract_package_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CombinGoodsContractActivity.this,CombinChooseContractPackageActivity.class);
                i.putExtra("ITV", productDetailResultDemo.contractPackage);//用itv来实现
                startActivityForResult(i, CHOOSE_CONTRACT_PACKAGE_REQUEST);
            }
        });


        //Itv
        if(productDetailResultDemo.ITVType.equals("")){
            findViewById(R.id.ITV_view).setVisibility(View.GONE);
        }else {
            findViewById(R.id.ITV_view).setVisibility(View.VISIBLE);
            List<ItemCommon> itvList = paintView(PaintType.ITV, productDetailResultDemo.ITVType);
            initRaido(itvList, R.id.ITV_rg, new ITVOnClickListener());
            if(itvList.size() > 0){
                createOrderDemo.selectItvId = itvList.get(0).id;
                Log.i("ITV", "createOrderDemo.selectItvId=="+createOrderDemo.selectItvId);
            }
        }

        //选号
        chooseNumber();

    }

    /**
     * ITV点击事件
     */
    private class ITVOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value) {
                case CombinConstant.ITV.NONE:
                    createOrderDemo.selectItvId = CombinConstant.ITV.NONE;
                    Log.i("ITV", "createOrderDemo.selectItvId=="+createOrderDemo.selectItvId);
                    break;
                case CombinConstant.ITV.NEW:
                    createOrderDemo.selectItvId = CombinConstant.ITV.NEW;
                    Log.i("ITV", "createOrderDemo.selectItvId=="+createOrderDemo.selectItvId);
                    chooseITV();
                    break;
                case CombinConstant.ITV.MODIFY:
                    createOrderDemo.selectItvId = CombinConstant.ITV.MODIFY;
                    Log.i("ITV", "createOrderDemo.selectItvId=="+createOrderDemo.selectItvId);
                    chooseITV();
                    break;
            }
        }
    }

    private void chooseITV(){
        Intent i = new Intent(this, CombinChooseContractPackageActivity.class);
        i.putExtra("ITV",productDetailResultDemo.ITV);//
        startActivityForResult(i, ITV);
    }

    //选择模式   访问网络
    private void updateContractPackage(String contractId) {

        new BaseTask<String, String, JSONArray>(this, "") {

            @Override
            protected JSONArray doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().contractpackage(CombinGoodsContractActivity.this, arg0[0], arg0[1]);
            }

            @Override
            protected void onSuccess(JSONArray result) {
                productDetailResultDemo.contractPackage = new ProductDetailResultDemo().initCommonList(result);

                setContrctPackage();

                //选择模式(合约)后，套餐会发生变化，而且价格也会变化，所以在更新套餐后还要更新价格
                updateProductPrice(createOrderDemo.productCode, createOrderDemo.contractId);
            }
        }.execute(goodsCode, contractId);
    }


    //选择套餐，默认选择第一个套餐
    private void setContrctPackage(){
        contractPackageId = "";
        String contractPackageName = "选择套餐";

        if (productDetailResultDemo.contractPackage.size() > 0) {
            //默认取第一个套餐
            contractPackageId = productDetailResultDemo.contractPackage.get(0).id;
            createOrderDemo.contractPackageId=contractPackageId;
            contractPackageName = productDetailResultDemo.contractPackage.get(0).name;
        }
        contract_package_rb.setText(contractPackageName);
    }


    private List<ItemCommon> paintView(PaintType flag ,String value){
        List<ItemCommon> list = new ArrayList<ItemCommon>();
        switch (flag){
            case products:
                break;
            case contractmode://入网方式
                list = CombinConstant.getContractmode(value);
                break;
            case guarantee://担保方式
                list = CombinConstant.getGuarantee(value);
                break;
            case contractAccountType://合账方式
                list = CombinConstant.getContractAccountType(value);
                break;

            case ITV://ITV
                list = CombinConstant.getContractAccountType(value);
                break;
        }
        return list;
    }

    public  enum PaintType{

        products,// 产品
        contractmode,//入网方式
        guarantee,//担保方式
        contractAccountType,//合账方式
        ITV,//itv
    }



    private class ProductChangeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getTag() instanceof ProductDetailResultDemo.Product){

                ProductDetailResultDemo.Product product = (ProductDetailResultDemo.Product) v.getTag();
                updateSnView(product.requireSn);//是否展示合作工号

                //访问数据参数  productCode    默认String salemodeId = "2";     contractId = json.optString("id");
                //设置价格 price    //设置库存 stock
                updateProductPrice(product.productCode, createOrderDemo.contractId);
                SetValue.setText(text_stock, product.stock);
                createOrderDemo.productCode = product.productCode;
                Log.i("productcode ", "createOrderDemo.productCode=="+createOrderDemo.productCode);
                //切换轮播图
                adsUtil.updateTopAds(product.detailimg, adBannerVp, adBannerDots);
                myToast.show("产品监听事件");
            }
        }
    }


    /**
     * 入网方式击事件
     */
    private class contractmodeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value){
                case CombinConstant.Contractmode.NEW://新用户入网
                    break;

                case CombinConstant.Contractmode.ADD://老用户加装
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_JZ,
                            NumberVerifyResult.verifyType.contractmode.toString(), contractResult);
                    verifyNumber(bundle, CONTRACT_NUMBER);
                    break;
            }

            createOrderDemo.contractmode =value;
            myToast.show("入网方式击事件");

        }
    }


    /**
     * 担保方式击事件  "none,fixedline,broadband",    //担保方式
     */
    private class guaranteeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value){
                case CombinConstant.Guarantee.NONE://无担保

                    break;

                case CombinConstant.Guarantee.FIXESLINE://固话担保
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_DB,
                            NumberVerifyResult.verifyType.fixedline.toString(), contractResult);
                    verifyNumber(bundle, GUARANTEE);


                    break;
                case CombinConstant.Guarantee.BROADBAND://宽带担保
                    Bundle bundle2 = getVerifyNumberBundle(TibmGoodsPlan.YZHM_DB,
                            NumberVerifyResult.verifyType.broadband.toString(), guaranteeResult);
                    verifyNumber(bundle2, GUARANTEE);
                    Log.i("contractmodeListener", ""+v.getTag().toString());

                    break;
            }

            createOrderDemo.guarantee=value;
           if(Constant.TEST_FLAG)myToast.show("担保点击事件");
        }
    }


    /**
     * 合账方式击事件  "no,yes",               //合帐方式（不合帐,合帐）
     */
    private class contractAccountTypeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String value = v.getTag().toString();
            switch (value){

                case CombinConstant.ContractAccountType.NO://不合账
                    createOrderDemo.contractAccount = CombinConstant.ContractAccountType.NO;
                    Log.i("AccountTypeListener", ""+v.getTag().toString());
                    break;

                case CombinConstant.ContractAccountType.YES://合账
                    Bundle bundle = getVerifyNumberBundle(TibmGoodsPlan.YZHM_HZ,
                            NumberVerifyResult.verifyType.contractAccount.toString(), contractAccountResult);
                    verifyNumber(bundle, CONTRACTACCOUNT_VERIFY);
                    Log.i("AccountTypeListener", ""+v.getTag().toString());
                    break;
            }
            if(Constant.TEST_FLAG)Toast.makeText(mContext,v.getTag().toString(),Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 选号
     */
   public void chooseNumber(){
       ((RadioButton)findViewById(R.id.choose_number_rb)).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(productDetailResultDemo.choooseNumberDemo == null){
                   Toast.makeText(CombinGoodsContractActivity.this,"选择号码数据有误" ,Toast.LENGTH_SHORT).show();
                   return;
               }
               Intent i = new Intent(CombinGoodsContractActivity.this, CombinChooseNumberActivity.class);
               Bundle bundle = new Bundle();
               bundle.putSerializable("choooseNumberDemo",productDetailResultDemo.choooseNumberDemo);
               //i.putExtras(bundle);
               i.putExtra("choooseNumberDemo",productDetailResultDemo.choooseNumberDemo);
               startActivityForResult(i, CHOOSE_NUMBER);
           }
       });
   }


    //startActivityForResult 回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTRACT_NUMBER://入网
                    contractResult = initNumberVerifyResult(data);
                    createOrderDemo.contractmodeAddNumber = contractResult.number;
                    createOrderDemo.contractmodeAddType=data.getExtras().getString("type");
                    Log.i("入网","createOrderDemo.contractmodeAddNumber="+createOrderDemo.contractmodeAddNumber
                    +";;createOrderDemo.contractmodeAddType=="+createOrderDemo.contractmodeAddType);

                    break;
                case CONTRACTACCOUNT_VERIFY://合账
                    contractAccountResult = initNumberVerifyResult(data);
                    createOrderDemo.contractAccountNumber = contractAccountResult.number;
                    Log.i("合账","createOrderDemo.contractAccountNumber="+createOrderDemo.contractAccountNumber
                            );

                    break;
                case CHOOSE_CONTRACT_PACKAGE_REQUEST: //选择套餐
                    contractPackageId = data.getExtras().getString("contractPackageId");
                    createOrderDemo.contractPackageId=contractPackageId;
                    Log.i("ITV", "createOrderDemo.contractPackageId="+createOrderDemo.contractPackageId);
                    String contractPackageName = "";
                    for(int i=0; i<productDetailResultDemo.contractPackage.size(); i++) {
                        if(contractPackageId.equals(productDetailResultDemo.contractPackage.get(i).id)) {
                            contractPackageName =productDetailResultDemo.contractPackage.get(i).name;
                            break;
                        }
                    }
                    contract_package_rb.setText(contractPackageName);
                    break;

                case ITV://ITV
                    createOrderDemo.selectItvComboId = data.getExtras().getString("contractPackageId");
                    Log.i("ITV", "createOrderDemo.selectItvComboId="+createOrderDemo.selectItvComboId);
                    String desc = "";
                    for(int i = 0; i < productDetailResultDemo.ITV.size(); i++){
                        ProductDetailResultDemo.Common common = productDetailResultDemo.ITV.get(i);
                        desc = common.desc;
                    }
                    ((TextView)findViewById(R.id.tv_itv_desc)).setText(desc);
                    break;

                case CHOOSE_NUMBER://选择号码
                    String number = data.getExtras().getString("number");
                    String tele_use = data.getExtras().getString("tele_use");
                    Log.i("onActivityResult", "get return number="+number);
                    createOrderDemo.number = number;
                    createOrderDemo.choosedNumberTeleUse = tele_use;
                    Log.i("选择号码", "createOrderDemo.number="+createOrderDemo.number+
                            ";;createOrderDemo.choosedNumberTeleUse"+createOrderDemo.choosedNumberTeleUse);
                    ((RadioButton)findViewById(R.id.choose_number_rb)).setText(number);
                    break;

                case GUARANTEE://担保
                    guaranteeResult=initNumberVerifyResult(data);
                    createOrderDemo.guaranteeNumber = guaranteeResult.number;
                    Log.i("担保", "createOrderDemo.guaranteeNumber ="+createOrderDemo.guaranteeNumber
                           );
                    break;

                default:
                    break;
            }
        }
    }

    //合作伙伴门户工号  是否展示
    private void updateSnView(boolean bool) {
        if(bool) {
            serialnumberview.setVisibility(View.VISIBLE);
        } else {
            serialnumberview.setVisibility(View.GONE);
        }
    }


    @Override
    public void updatePriceView(String price) {
        text_goodPrice.setText("用户支付金额:" + price + "元");
    }

    @Override
    public String setTemplateId() {
        return templateId;
    }


    private MyToast myToast=new MyToast();

    class MyToast{
        public MyToast(){
        }
        private Toast toast = null;
        public  void  show(String string){
            if (toast == null) {
                toast = Toast.makeText(CombinGoodsContractActivity.this, string, Toast.LENGTH_SHORT);
            }else{
                toast.setText(string);
            }
            toast.show();
        }

    }


    public void call(View view) {
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


