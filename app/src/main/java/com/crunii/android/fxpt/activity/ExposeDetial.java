package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by ct on 2015/8/7.
 */
public class ExposeDetial extends Activity {

    private TextView ep_date;
    private ImageView ep_image;
    private ImageView ep_image2;
    private ImageView ep_image3;
    private String orderId;

    private TextView orderId1;
    private TextView transcribePoint;
    private TextView payAmount;
    private TextView chooseNumber;
    private TextView orderStatus;
    private TextView failReason;
    private TextView CRMStatus;
    private TextView custName;
    private TextView custIdCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expose_detial);

        orderId = getIntent().getStringExtra("orderId");
       // Log.i("ct", "exepose orderid================================" + orderId);

        initComponent();

        //获取json数据
        getJson();


    }

    public void initComponent() {
        CRMStatus=(TextView) findViewById(R.id.CRMStatus);
        ep_date = (TextView) findViewById(R.id.ep_date);
        orderId1 = (TextView) findViewById(R.id.orderId);
        transcribePoint = (TextView) findViewById(R.id.transcribePoint);
        chooseNumber = (TextView) findViewById(R.id.chooseNumber);
        payAmount = (TextView) findViewById(R.id.payAmount);
        orderStatus = (TextView) findViewById(R.id.orderStatus);
        TextView CRMStatus = (TextView) findViewById(R.id.CRMStatus);
        failReason = (TextView) findViewById(R.id.failReason);

        ep_image = (ImageView) findViewById(R.id.ep_image);
        ep_image2 = (ImageView) findViewById(R.id.ep_image2);
        ep_image3 = (ImageView) findViewById(R.id.ep_image3);

        ep_date = (TextView) findViewById(R.id.ep_date);
        custName=(TextView)findViewById(R.id.custName);
        custIdCard=(TextView)findViewById(R.id.custIdCard);
    }

    public void getJson() {
        new BaseTask<String, String, JSONObject>(this) {

            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                this.dialog = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected void onSuccess(JSONObject result) {
                /*private TextView orderId1;
                private TextView transcribePoint;
                private TextView payAmount;
                private TextView chooseNumber;
                private TextView orderStatus;
                private TextView failReason;*/

                CRMStatus.setText( result.optString("CRMStatus"));
                orderId1.setText( orderId);
                transcribePoint.setText( result.optString("transcribePoint"));
                payAmount.setText( result.optString("payAmount"));
                chooseNumber.setText( result.optString("chooseNumber"));
                orderStatus.setText( result.optString("orderStatus"));
                failReason.setText( result.optString("failReason"));

                custName.setText( result.optString("custName"));
                custIdCard.setText( result.optString("custIdCard"));
                ep_date.setText( result.optString("ep_date"));


                String url = "";
                //url = "https://www.baidu.com/img/bdlogo.png";
                MyAsynImageLoader imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), context);
                url = result.optString("ep_image");
                imageLoader.showImageAsyn(new SoftReference<ImageView>(ep_image), url, R.drawable.adbanner);

                /*url = result.optString("ep_image2");
                imageLoader.showImageAsyn(new SoftReference<Object>(ep_image2), url, R.drawable.adbanner);
                url = result.optString("ep_image3");
                imageLoader.showImageAsyn(new SoftReference<Object>(ep_image3), url, R.drawable.adbanner);*/
            }

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().exeposeDetial(context, orderId);
            }


            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                dialog.dismiss();

            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
