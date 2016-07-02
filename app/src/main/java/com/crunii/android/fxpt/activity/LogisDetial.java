package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * Created by Administrator on 2015/8/4.
 */
public class LogisDetial extends Activity {

    private ImageButton button;

    private TextView logis_dt_compName;
    private TextView logis_dt_id;
    private TextView logis_dt_state;
    private ImageView logis_dt_image;

    private TextView logis_dt_lastmess;
    private TextView logis_dt_lasttime;

    private RelativeLayout logis_dt_f2message;
    private TextView logis_dt_f2mess;
    private TextView logis_dt_f2time;

    private RelativeLayout logis_dt_f3message;
    private TextView logis_dt_f3mess;
    private TextView logis_dt_f3time;

    private String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logis_detial);

        button = (ImageButton) findViewById(R.id.logis_dt_ibutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* *//*init orderId
        orderId="FXA201312111112253175";*/
        Intent intent=getIntent();
        orderId= intent.getStringExtra("orderId");

        initComponent();

        //get json date
        getJson();



    }

    public void getJson(){
        new BaseTask<String, String, JSONObject>(this){

            private ProgressDialog loadMask;
            @Override
            protected void onPreExecute() {
                this.loadMask = ProgressDialog.show(context, null, "加载中...");
            }

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
               // Log.i("ct", "doInBack==============" );
                return CRApplication.getApp().logisticDetial(LogisDetial.this, orderId);
            }

            @Override
            protected void onSuccess(final JSONObject result) {
               // Log.i("ct", "onSuccess logis_dt_compName==============" + result.optString("logis_dt_compName"));
                logis_dt_compName.setText(result.optString("logis_dt_compName"));
                logis_dt_id.setText(result.optString("logis_dt_id"));
                logis_dt_state.setText(result.optString("logis_dt_state"));
               // logis_dt_image.setText(result.optString("logis_dt_id"));
                MyAsynImageLoader imageLoader=new MyAsynImageLoader(CRApplication.getApp().getHttpClient(),LogisDetial.this);
                imageLoader.showImageAsyn(new SoftReference<ImageView>(logis_dt_image),result.optString("logis_dt_image"),R.drawable.phone);

                logis_dt_lastmess.setText(result.optString("logis_dt_lastmess"));
                logis_dt_lasttime.setText(result.optString("logis_dt_lasttime"));

                if (result.optBoolean("logis_dt_f2message")){
                    logis_dt_f2mess.setText(result.optString("logis_dt_f2mess"));
                    logis_dt_f2time.setText(result.optString("logis_dt_f2time"));
                    logis_dt_f2message.setVisibility(View.VISIBLE);
                }else{
                    logis_dt_f2message.setVisibility(View.GONE);
                }

                if (result.optBoolean("logis_dt_f3message")){
                    logis_dt_f3mess.setText(result.optString("logis_dt_f3mess"));
                    logis_dt_f3time.setText(result.optString("logis_dt_f3time"));
                    logis_dt_f3message.setVisibility(View.VISIBLE);
                }else{
                    logis_dt_f3message.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);

                this.loadMask.dismiss();
            }
        }.execute("");
    }

    public void initComponent(){
        logis_dt_compName=(TextView)findViewById(R.id.logis_dt_compName);
        logis_dt_id=(TextView)findViewById(R.id.logis_dt_id);
        logis_dt_state=(TextView)findViewById(R.id.logis_dt_state);
        logis_dt_image=(ImageView)findViewById(R.id.logis_dt_image);

        logis_dt_lastmess=(TextView)findViewById(R.id.logis_dt_lastmess);
        logis_dt_lasttime=(TextView)findViewById(R.id.logis_dt_lasttime);

        logis_dt_f2message=(RelativeLayout)findViewById(R.id.logis_dt_f2message);
        logis_dt_f2mess=(TextView)findViewById(R.id.logis_dt_f2mess);
        logis_dt_f2time=(TextView)findViewById(R.id.logis_dt_f2time);

        logis_dt_f3message=(RelativeLayout)findViewById(R.id.logis_dt_f3message);
        logis_dt_f3mess=(TextView)findViewById(R.id.logis_dt_f3mess);
        logis_dt_f3time=(TextView)findViewById(R.id.logis_dt_f3time);
    }


}
