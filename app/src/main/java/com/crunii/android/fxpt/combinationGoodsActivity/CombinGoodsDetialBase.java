package com.crunii.android.fxpt.combinationGoodsActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.ChooseNumberActivity;
import com.crunii.android.fxpt.activity.ProductDetailWebviewActivity;
import com.crunii.android.fxpt.activity.VerifyNumberActivity;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.view.MyScrollView;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组合商品基类
 * Created by ct on 2015/11/20.
 */
public abstract class CombinGoodsDetialBase extends BaseActivity {

    public Context mContext;
    Map<Integer, View> map = new HashMap<Integer, View>();
    public ProductDetailResultDemo productDetailResultDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    public View findViewById(int id) {
        View view = map.get(id);
        if (view == null) {
            view = super.findViewById(id);
            map.put(id, view);
        }
        return view;
    }

    /**
     * 获取商品详情数据数据
     *
     * @param goodsCode
     * @param CRMId     CRM工号
     */
    public void getProductDetailData(final String goodsCode, final String CRMId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("goodsCode", goodsCode);
        map.put("requestType", "combine");
        map.put("CRMId", CRMId);
        sendPost(Constant.CTX_PATH + "productdetail", map, new HttpPostProp() {
                    @Override
                    public void successJson(JSONObject jsonObject) {
                        productDetailResultDemo = new ProductDetailResultDemo(jsonObject);
                        initData(productDetailResultDemo);
                    }
                }
        );
    }

    /**
     * 号码验证所需的参数
     *
     * @param tibmTypeId         合账类型 eg:TibmGoodsPlan.YZHM_JZ
     * @param verifyType         号码验证类型 eg: 固话: NumberVerifyResult.verifyType.fixedline.toString();
     * @param numberVerifyResult
     * @return
     */
    public Bundle getVerifyNumberBundle(String tibmTypeId, String verifyType, NumberVerifyResult numberVerifyResult) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("contractAccount",productDetailResultDemo.contractAccount);
        bundle.putBoolean("isContractAccount", productDetailResultDemo.isContractAccount);
        bundle.putString("typeId", tibmTypeId);
        bundle.putString("type", verifyType);
        bundle.putString("number", numberVerifyResult.number);
        bundle.putString("address", numberVerifyResult.address);
        return bundle;
    }


    /**
     * @param bundle      号码验证所需的参数
     * @param requestCode 请求标示码
     */
    public void verifyNumber(Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, VerifyNumberActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 封装号码验证返回数据
     *
     * @param intent
     * @return
     */
    public NumberVerifyResult initNumberVerifyResult(Intent intent) {
        NumberVerifyResult numberVerifyResult = new NumberVerifyResult();
        if (intent != null) {
            String number = intent.getExtras().getString("number");
            String address = intent.getExtras().getString("address");
            String bestContact = intent.getExtras().getString("bestContact");
            String displayName = intent.getExtras().getString("displayName");
            String displayCitizenId = intent.getExtras().getString("displayCitizenId");
            String displayCitizenAddress = intent.getExtras().getString("displayCitizenAddress");
            String verifiedName = intent.getExtras().getString("verifiedName");
            String verifiedCitizenId = intent.getExtras().getString("verifiedCitizenId");
            String verifiedCitizenAddress = intent.getExtras().getString("verifiedCitizenAddress");
            numberVerifyResult.setNumber(number);
            numberVerifyResult.setAddress(address);
            numberVerifyResult.setBestContact(bestContact);
            numberVerifyResult.setDisplayName(displayName);
            numberVerifyResult.setDisplayCitizenId(displayCitizenId);
            numberVerifyResult.setDisplayCitizenAddress(displayCitizenAddress);
            numberVerifyResult.setVerifiedName(verifiedName);
            numberVerifyResult.setVerifiedCitizenId(verifiedCitizenId);
            numberVerifyResult.setVerifiedCitizenAddress(verifiedCitizenAddress);
        }
        return numberVerifyResult;
    }

    /**
     * @param productCode  商品编码
     * @param contractId   合约id
     */
    public void updateProductPrice(String productCode, String contractId){
        //20150610  终端铺货一次结 2
        String salemode = "2"; //售卖方式
        String templateId = setTemplateId();
        switch(templateId){
            case "1001"://合约模板：主产品、销售模式、合约 决定价格
                if(productCode.equals("") || salemode.equals("") || contractId.equals("")) {
                    return;
                }
                getPrice(productCode,salemode,contractId);
                break;
            case "1008"://固宽模板：主产品、销售模式 决定价格
                if(productCode.equals("") || salemode.equals("")) {
                    return;
                }
                getPrice(productCode,salemode,contractId);
                break;
            default:
                Toast.makeText(this,"模版有误，模版号："+templateId,Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void getPrice(String productCode, String salemode, String contractId){
        Map<String,String> params = new HashMap<String,String>();
        params.put("productCode", productCode);
        params.put("salemode", salemode);
        params.put("goodsCode", productDetailResultDemo.goodsCode);
        params.put("contractId", contractId);
        sendPost(Constant.CTX_PATH + "goodsprice", params, new HttpPostProp() {
                    @Override
                    public void successJson(JSONObject jsonObject) {
                        String price = jsonObject.optString("price");
                        updatePriceView(price);
                    }
                }
        );
    }
    /**
     *
     * @param list
     * @param radioGroupId    需要动态添加自空间的id
     * @param onClickListener 处理事件
     */
    public void initRaido(List<ItemCommon> list,int radioGroupId,View.OnClickListener onClickListener){
        if(list == null || list.size() == 0){
            return ;
        }
        RadioGroup rg = (RadioGroup)findViewById(radioGroupId);
//        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(100,100);
//        layoutParams.setMargins(5, 5, 5, 5);
//        rg.setLayoutParams(layoutParams);

        int productsCount = list.size();
        for(int i = 0; i < productsCount; i ++){
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate( R.layout.radiobutton,null);

            radioButton.setText(list.get(i).name);
            radioButton.setTag(list.get(i).id);
            radioButton.setOnClickListener(onClickListener);
            rg.addView(radioButton);
        }
        //默认选中第一个
        ((RadioButton)rg.getChildAt(0)).setChecked(true);

    }

    /**
     * 选择号码
     *
     * @param requestCode 请求码
     */
    public void chooseNumber(int requestCode){
        if(productDetailResultDemo.choooseNumberDemo == null){
            Toast.makeText(this,"选择号码数据有误" ,Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, CombinChooseNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("choooseNumberDemo",productDetailResultDemo.choooseNumberDemo);
        //i.putExtras(bundle);
        i.putExtra("choooseNumberDemo",productDetailResultDemo.choooseNumberDemo);
        startActivityForResult(i, requestCode);
    }

    /**
     * 用于价格更新过后的视图更新
     * @param price  更新过后的价格
     */
    public abstract void updatePriceView(String price);

    /**
     * 显示或者隐藏控件
     *
     * @param productDetailResultDemo 商品详情请求后的数据
     */
    public abstract void initData(ProductDetailResultDemo productDetailResultDemo);

    /**
     * 设置模版号 eg:"1008"
     * @return
     */
    public abstract String setTemplateId();


    //监听向上滑动手势
    private GestureDetector activityGuestureDetector;
    private int count = 0;
    public void initGestureDetector(final View detailview, final CreateOrderDemo createOrderDemo) {

        //监听MyScrollView的滑动事件
        MyScrollView mysv = (MyScrollView)findViewById(R.id.mysv);
        mysv.setOnScrollToBottomLintener(new MyScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if(isBottom) {
                    count ++;
                    if(count > 10) {
                        count = 0;

                        if(detailview.getVisibility() == View.VISIBLE) {
                            doWebview(null,createOrderDemo);
                        }
                    }
                }
            }
        });

        //监听Activity的滑动事件
        activityGuestureDetector = new GestureDetector(this, new MyGestureDetector(createOrderDemo));
    }

    public void doWebview(View v,CreateOrderDemo createOrderDemo) {
        Intent i = new Intent(this, ProductDetailWebviewActivity.class);
        i.putExtra("goodsCode", createOrderDemo.goodsCode);
        i.putExtra("productCode", createOrderDemo.productCode);
        startActivity(i);
        overridePendingTransition(R.anim.activity_open, 0);
    }

    private class MyGestureDetector implements GestureDetector.OnGestureListener {

        CreateOrderDemo createOrderDemo;

        public MyGestureDetector(CreateOrderDemo createOrderDemo){
            this.createOrderDemo = createOrderDemo;
        }
        @Override
        public boolean onDown(MotionEvent arg0) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getY() - e2.getY() > 200) {

                    doWebview(null,createOrderDemo);
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
}
