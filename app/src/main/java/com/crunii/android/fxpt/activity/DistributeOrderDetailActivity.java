package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by speedingsnail on 16/1/15.
 */
public class DistributeOrderDetailActivity extends Activity {

    private TextView name, phone, intention, time, receiver_name, state, shopDiscount, yewupay, zhongduanpay, choujinpay,chuliren;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_distribute_order_detail_a);
        int id = getIntent().getExtras().getInt("id");
        String type = getIntent().getExtras().getString("type");
        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
        intention = (TextView) findViewById(R.id.intention);
        time = (TextView) findViewById(R.id.time);
        receiver_name = (TextView) findViewById(R.id.receiver_name);
        state = (TextView) findViewById(R.id.state);
        yewupay = (TextView) findViewById(R.id.yewuPay);
        zhongduanpay = (TextView) findViewById(R.id.zhongfuanfanli);
        choujinpay = (TextView) findViewById(R.id.choujinPay);
        shopDiscount = (TextView) findViewById(R.id.shopDiscount);
        chuliren= (TextView) findViewById(R.id.chuliren);

        Map<String, Object> params = new HashMap<>();
        params.put("id", String.valueOf(id));
        params.put("type", type);
        HttpTool.sendPost(this, Constant.CTX_PATH + "distributeOrderDetail", params, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                name.setText((String) record.get("customerName"));
                phone.setText((String) record.get("phone"));
                intention.setText((String) record.get("intention"));
                time.setText((String) record.get("time"));
                receiver_name.setText((String) record.get("receiverName"));
                chuliren.setText((String) record.get("deal_seller_name"));
                if (record.get("discount") != null) {
                    shopDiscount.setText((String) record.get("discount"));
                }
                state.setText((String) record.get("state"));
                if (record.get("combo_money").toString().equals("null")) {
                    yewupay.setText("¥ 0");
                } else {
                    int combo = Integer.parseInt(record.get("combo_money").toString());
                    String combo_money = String.valueOf(combo / 100);
                    yewupay.setText("¥ " + combo_money);
                }
                if (record.get("goods_money").toString().equals("null")) {
                    zhongduanpay.setText("¥ 0");
                } else {
                    int goods = Integer.parseInt(record.get("goods_money").toString());
                    String goods_money = String.valueOf(goods / 100);
                    zhongduanpay.setText("¥ " + goods_money);
                }
                if (record.get("tibm_state").toString().equals("1")) {
                    choujinpay.setText("已支付");

                } else {
                    choujinpay.setText("未支付");
                }


            }
        });
    }

    public void doBack(View v) {
        onBackPressed();
    }
}
