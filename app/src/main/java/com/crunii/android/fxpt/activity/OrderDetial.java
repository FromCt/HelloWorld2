package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.MyAsynImageLoader;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;


/**
 * Created by Administrator on 2015/8/3.
 */
public class OrderDetial extends Activity {

    private ImageView od_image;
    private TextView od_state;
    private TextView od_price;
    private TextView od_date;
    private TextView od_cm;

    private RelativeLayout od_receiver_state;
    private TextView od_receiver;
    private TextView od_tel;
    private TextView od_id;

    private RelativeLayout od_logis_infostate;
    private TextView od_logis_state;
    private TextView od_logis_date;

    private RelativeLayout od_innet_state;
    private TextView od_innet_tel;
    private TextView od_innet_id;
    private TextView od_innet_name;
    private TextView od_innet_idcode;

    private ImageView od_goods_image;
    private TextView  od_provider_name;
    private TextView od_goods_name;
    private TextView od_goods_price;
    private TextView od_goods_pay;

    private Button od_cancle;
    private Button od_pay;
    private Button od_goods_logis;
    private ImageButton logistics_detial;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetial);

        od_logis_infostate= (RelativeLayout) findViewById(R.id.od_logis_infostate);
        od_cancle= (Button) findViewById(R.id.od_cancle);
        od_pay= (Button) findViewById(R.id.od_pay);
        od_goods_logis= (Button) findViewById(R.id.od_goods_logis);
        od_cm = (TextView) findViewById(R.id.od_cm);

        //获取orderId
       // orderId="FXA201312301354163770";
        Intent intent=getIntent();
        orderId= intent.getStringExtra("orderId");

        //初始化布局组件
        innitCompont();

        //解析jison数据格式
        refresh();


        od_goods_logis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetial.this, LogisDetial.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });

        logistics_detial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetial.this, LogisDetial.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });


    }

    private void innitCompont() {
        od_date=(TextView)findViewById(R.id.od_date);
        logistics_detial=(ImageButton)findViewById(R.id.logistics_detial);
        od_image=(ImageView)findViewById(R.id.od_image);
        od_state= (TextView) findViewById(R.id.od_state);

        od_receiver_state=(RelativeLayout)findViewById(R.id.od_receiver_state);
        od_price= (TextView) findViewById(R.id.od_price);
        od_receiver= (TextView) findViewById(R.id.od_receiver);
        od_tel= (TextView) findViewById(R.id.od_tel);
        od_id= (TextView) findViewById(R.id.od_id);

        od_logis_state= (TextView) findViewById(R.id.od_logis_state);
        od_logis_date= (TextView) findViewById(R.id.od_logis_date);
        od_innet_name= (TextView) findViewById(R.id.od_innet_name);
        od_innet_idcode= (TextView) findViewById(R.id.od_innet_idcode);

        od_innet_state=(RelativeLayout)findViewById(R.id.od_innet_state);
        od_innet_tel= (TextView) findViewById(R.id.od_innet_tel);
        od_innet_id= (TextView) findViewById(R.id.od_innet_id);

        od_provider_name= (TextView)  findViewById(R.id.od_provider_name);
        od_goods_image= (ImageView) findViewById(R.id.od_goods_image);
        od_goods_name= (TextView) findViewById(R.id.od_goods_name);
        od_goods_price= (TextView) findViewById(R.id.od_goods_price);
        od_goods_pay= (TextView) findViewById(R.id.od_goods_pay);

    }

    protected void refresh() {
        new BaseTask<String, String, JSONObject>(this) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
               // if (showProgressDialog) {
                    this.loadMask = ProgressDialog.show(context, null, "请稍候...");
               // }
            }

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
               return CRApplication.getApp().orderDetial(OrderDetial.this, orderId);
            }

            @Override
            protected void onSuccess(final JSONObject result) {
               // Log.i("ct", "onSuccess JsonList().size() ");
                String payType = result.optString("payType");
                od_date.setText("竣工时间："+result.optString("od_date"));
                od_state.setText(result.optString("od_state"));
                od_price.setText("订单金额："+result.optString("od_price")+"元");
                od_cm.setText(result.optString("od_cm"));

                od_provider_name.setText(result.optString("od_provider_name"));
                od_goods_name.setText(result.optString("od_goods_name"));
               // Log.i("ct", "goods name:" + result.optString("od_goods_name"));
                od_goods_price.setText("¥ "+result.optString("od_goods_price")+"X"+result.optString("od_goods_number"));
                od_goods_pay.setText("实付：¥ "+result.optString("od_goods_pay"));

                if(result.optBoolean("od_logis_stateCode")){//判断是否显示物流信息
                    od_logis_infostate.setVisibility(View.VISIBLE);
                    od_goods_logis.setVisibility(View.VISIBLE);
                    od_logis_state.setText(result.optString("od_logis_state"));
                    od_logis_date.setText(result.optString("od_logis_date"));

                }else{
                    od_logis_infostate.setVisibility(View.GONE);
                    od_goods_logis.setVisibility(View.GONE);
                }

                if(result.optBoolean("od_innet_state")){//判断是否显入网信息
                    od_innet_state.setVisibility(View.VISIBLE);
                    od_innet_name.setText(result.optString("od_innet_name"));
                    od_innet_id.setText(result.optString("od_innet_id"));
                    od_innet_tel.setText(result.optString("od_innet_tel"));
                    od_innet_idcode.setText(result.optString("od_innet_idcode"));
                }else{
                    od_innet_state.setVisibility(View.GONE);
                }

                if(result.optBoolean("od_receiver_state")){//判断是否显收货信息
                    od_receiver_state.setVisibility(View.VISIBLE);
                    od_receiver.setText("收货人："+result.optString("od_receiver"));
                    od_tel.setText(result.optString("od_tel"));
                    od_id.setText(result.optString("收货地址：" + "od_id"));

                }else{
                    od_receiver_state.setVisibility(View.GONE);
                }
                Log.i("ct","or_state code="+result.optString("od_state_code"));
                //订单按钮状态改变  result.optInt("od_state_code")
                switch (result.optInt("od_state_code")){
                    case 0:
                        od_cancle.setVisibility(View.GONE);
                        od_pay.setVisibility(View.GONE);
                        break;

                    case 1:
                        od_cancle.setVisibility(View.VISIBLE);
                        if(payType.equals("offLine"))
                        {
                            od_pay.setVisibility(View.GONE);
                        }else{
                            od_pay.setVisibility(View.VISIBLE);
                            od_pay.setText("支付");
                        }
                        od_cancle.setText("取消订单");
                        od_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderDetial.this, CancelOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                i.putExtra("isBL", false);
                                startActivityForResult(i, 0);
                            }
                        });

                        od_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(OrderDetial.this,WebPayActivity.class);
                                intent.putExtra("orderid", orderId);
                                startActivityForResult(intent,1);
                            }
                        });
                        break;

                    case 2:
                        od_pay.setVisibility(View.GONE);
                     /*   od_cancle.setVisibility(View.VISIBLE);
                        od_cancle.setText("申请退款");
                        od_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        break;*/

                    case 3:
                        od_pay.setVisibility(View.GONE);
                        if(payType.equals("offLine"))
                        {
                            od_cancle.setVisibility(View.GONE);
                        }else{
                            od_cancle.setVisibility(View.VISIBLE);
                            od_cancle.setText("申请退款");
                        }

                        od_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderDetial.this, RefundOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                startActivityForResult(i, 2);
                            }
                        });
                        break;

                    case 4:
                        od_pay.setVisibility(View.GONE);
                        od_cancle.setVisibility(View.VISIBLE);
                        od_cancle.setText("申请退货");
                        od_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderDetial.this, ReturnOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                startActivityForResult(i, 3);
                            }
                        });
                        break;

                    case 5:
                        od_pay.setVisibility(View.GONE);
                        od_cancle.setVisibility(View.GONE);
                        od_cancle.setText("填写退货单号");
                        od_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(OrderDetial.this,"建设中",Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }



                String url=result.optString("od_goods_image");
                //url="https://www.baidu.com/img/bdlogo.png";
                MyAsynImageLoader imageLoader=new MyAsynImageLoader(CRApplication.getApp().getHttpClient(),OrderDetial.this);
                imageLoader.showImageAsyn(new SoftReference<ImageView>(od_goods_image), url,R.drawable.phone);

                url=result.optString("od_image");
                //url="http://c.hiphotos.baidu.com/image/w%3D400/sign=2258af2f4334970a4773112fa5cbd1c0/b7fd5266d01609242193c51bd60735fae6cd347f.jpg";
                imageLoader.showImageAsyn(new SoftReference<ImageView>(od_image), url,R.drawable.phone);
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);

                Log.i("ct","over================");
                this.loadMask.dismiss();
            }

        }.execute("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
        od_cancle.invalidate();
    }
}
