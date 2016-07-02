package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by Administrator on 2015/8/3.
 */
public class OrderADetial extends Activity {

    private RelativeLayout rl_sellerName;
    private RelativeLayout rl_od_receiver;
    private RelativeLayout rl_custName;

    private TextView tv_sellerName;
    private TextView tv_orderId;
    private TextView tv_goodsName;
    private TextView tv_receiver;
    private TextView tv_custName;
    private TextView tv_payAmount;
    private TextView tv_createTime;
    private TextView tv_orderState;
    private TextView tv_crmState;

    private Button bt_cancle;
    private Button bt_pay;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detial);

        //获取orderId
        Intent intent=getIntent();
        orderId= intent.getStringExtra("orderId");

        //初始化布局组件
        innitCompont();

        //解析jison数据格式
        refresh();

    }

    private void innitCompont() {

        rl_sellerName= (RelativeLayout) findViewById(R.id.rl_sellerName);
        rl_od_receiver= (RelativeLayout) findViewById(R.id.rl_od_receiver);
        rl_custName= (RelativeLayout) findViewById(R.id.rl_custName);

        bt_cancle= (Button) findViewById(R.id.od_cancle);
        bt_pay= (Button) findViewById(R.id.od_pay);

        tv_sellerName = (TextView) findViewById(R.id.tv_sellerName);
        tv_orderId = (TextView) findViewById(R.id.tv_orderId);
        tv_goodsName = (TextView) findViewById(R.id.tv_goodsName);
        tv_receiver= (TextView) findViewById(R.id.tv_receiver);
        tv_custName= (TextView) findViewById(R.id.tv_custName);
        tv_payAmount= (TextView) findViewById(R.id.tv_payAmount);
        tv_createTime= (TextView) findViewById(R.id.tv_createTime);
        tv_orderState= (TextView) findViewById(R.id.tv_orderState);
        tv_crmState= (TextView) findViewById(R.id.tv_crmState);

    }

    protected void refresh() {
        new BaseTask<String, String, JSONObject>(this) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                    this.loadMask = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
               return CRApplication.getApp().orderDetial(OrderADetial.this, orderId);
            }

            @Override
            protected void onSuccess(final JSONObject result) {
               // Log.i("ct", "onSuccess JsonList().size() ");
                String orderType = result.optString("orderType");
                String payType = result.optString("payType");

                tv_sellerName.setText(result.optString("sellerName"));
                tv_orderId.setText(orderId);
                tv_goodsName.setText(result.optString("goodsName"));
                tv_receiver.setText(result.optString("od_receiver"));
                tv_custName.setText(result.optString("customerName"));
                tv_payAmount.setText(result.optString("odPrice"));
                tv_createTime.setText(result.optString("createTime"));
                tv_orderState.setText(result.optString("stateDesc"));
                tv_crmState.setText(result.optString("crmState"));

                Log.i("ct","or_state code="+result.optString("od_state_code"));
                //订单按钮状态改变
                switch (result.optInt("od_state_code")){
                    case 0:
                        bt_cancle.setVisibility(View.GONE);
                        bt_pay.setVisibility(View.GONE);
                        break;

                    case 1:
                        bt_cancle.setVisibility(View.VISIBLE);
                        if(payType.equals("offLine"))
                        {
                            bt_pay.setVisibility(View.GONE);
                        }else{
                            bt_pay.setVisibility(View.VISIBLE);
                            bt_pay.setText("支付");
                        }
                        bt_cancle.setText("取消订单");
                        bt_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderADetial.this, CancelOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                i.putExtra("isBL", false);
                                startActivityForResult(i, 0);
                            }
                        });

                        bt_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(OrderADetial.this,WebPayActivity.class);
                                intent.putExtra("orderid", orderId);
                                startActivityForResult(intent,1);
                            }
                        });
                        break;

                    case 2:
                        bt_pay.setVisibility(View.GONE);
                    case 3:
                        bt_pay.setVisibility(View.GONE);
                        if(payType.equals("offLine"))
                        {
                            bt_cancle.setVisibility(View.GONE);
                        }else{
                            bt_cancle.setVisibility(View.VISIBLE);
                            bt_cancle.setText("申请退款");
                        }

                        bt_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderADetial.this, RefundOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                startActivityForResult(i, 2);
                            }
                        });
                        break;

                    case 4:
                        bt_pay.setVisibility(View.GONE);
                        bt_cancle.setVisibility(View.VISIBLE);
                        bt_cancle.setText("申请退货");
                        bt_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(OrderADetial.this, ReturnOrderActivity.class);
                                i.putExtra("orderId", orderId);
                                startActivityForResult(i, 3);
                            }
                        });
                        break;

                    case 5:
                        bt_pay.setVisibility(View.GONE);
                        bt_cancle.setVisibility(View.GONE);
                        bt_cancle.setText("填写退货单号");
                        bt_cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(OrderADetial.this,"建设中",Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

                if("myOrder".equals(orderType)){//判断订单类别
                    rl_od_receiver.setVisibility(View.VISIBLE);
                    rl_sellerName.setVisibility(View.GONE);
                    rl_custName.setVisibility(View.GONE);
                }else{
                    rl_od_receiver.setVisibility(View.GONE);
                    rl_sellerName.setVisibility(View.VISIBLE);
                    rl_custName.setVisibility(View.VISIBLE);
                    bt_pay.setVisibility(View.GONE);
                    bt_cancle.setVisibility(View.GONE);
                }

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
        bt_cancle.invalidate();
    }
}
