package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2015/2/2.
 */
public class SpeedRegisterActivity extends Activity {

    private Context mContext;
    private TextView v_subCompanyName;
    private TextView v_stationName;
    private Button v_handlePoint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_speed_register);
        v_subCompanyName = (TextView) findViewById(R.id.subCompanyName);
        v_stationName = (TextView) findViewById(R.id.stationName);
        v_handlePoint = (Button) findViewById(R.id.handlePoint);

        refresh();
    }

    private String subCompanyId ="",stationId = "",handlePointId = "";
    private JSONArray handlePointList = new JSONArray();
    private void refresh(){
        new BaseTask<String, String, JSONObject>(this) {
            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().transcribe(mContext);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                subCompanyId = jsonObject.optString("subCompanyId");
                stationId = jsonObject.optString("stationId");
                String subCompanyName = jsonObject.optString("subCompanyName");
                String stationName = jsonObject.optString("stationName");
                String defaultPointId = jsonObject.optString("defaultPointId");
                String defaultPointName = jsonObject.optString("defaultPointName");
                handlePointList = jsonObject.optJSONArray("handlePointList");
                v_subCompanyName.setText(subCompanyName);
                v_stationName.setText(stationName);
                v_handlePoint.setText(defaultPointName);
                handlePointId = defaultPointId;
//                if(handlePointList.length() > 0){
//                    v_handlePoint.setText(handlePointList.optJSONObject(0).optString("name"));
//                    handlePointId = handlePointList.optJSONObject(0).optString("id");
//                }
            }
        }.execute("");
    }


    public void doHandlePoint(View view){
        if(handlePointList.length() <= 0){
            return;
        }
        String[] handlePointNameList = new String[handlePointList.length()];
        for(int i = 0;i < handlePointList.length();i++){
            handlePointNameList[i] = handlePointList.optJSONObject(i).optString("name");
        }
        new AlertDialog.Builder(this).setItems(handlePointNameList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handlePointId = handlePointList.optJSONObject(which).optString("id");
                String handlePointName = handlePointList.optJSONObject(which).optString("name");
                v_handlePoint.setText(handlePointName);
            }
        }).show();

    }

    public void doNext(View view){
        Intent intent = new Intent(this,TranscribeCameraActivity.class);
        intent.putExtra("subCompanyId",subCompanyId);
        intent.putExtra("stationId",stationId);
        intent.putExtra("handlePointId", handlePointId);
        startActivity(intent);


        File file = new File("");
        file.exists();
    }

    public void doBack(View view){
        onBackPressed();
    }

}
