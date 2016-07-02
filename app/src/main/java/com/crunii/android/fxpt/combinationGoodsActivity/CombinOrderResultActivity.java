package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.MainActivity;

/**
 * Created by speedingsnail on 15/12/16.
 */
public class CombinOrderResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_order_result);


        String orderId = getIntent().getExtras().getString("orderId");
        String goodsId = getIntent().getExtras().getString("goodsId");
        String  orderName = getIntent().getExtras().getString("orderName");
        String payValue = getIntent().getExtras().getString("payValue");
        String  originalPrice = getIntent().getExtras().getString("originalPrice");
        String buyNumber = getIntent().getExtras().getString("buyNumber");
        String  discountPrice = getIntent().getExtras().getString("discountPrice");
        String templateId = getIntent().getExtras().getString("templateId");
        String payType = getIntent().getExtras().getString("payType");


        TextView tv_message = (TextView) findViewById(R.id.tv_message);
        String payText = "\n\n线下支付金额： ¥";

            String msg = "订单号： " + orderId +
                    "\n\n商    品： " + orderName +
                    "\n\n价    格： ¥" + originalPrice +
                    "\n\n数    量： " + buyNumber +
                    payText + payValue;
            tv_message.setText(msg);


    }


    public void doClearTop(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
