package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.invs.InvsMainActivity;
import com.crunii.android.fxpt.mobileReader.MobileReaderActivity;
import com.crunii.android.fxpt.util.ImageHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranscribeCameraActivity extends Activity {

    private static final String tag = "TranscribeCameraActivity";

    private Context mContext;
    public final int REQUEST_IMAGE1 = 1;
    public final int REQUEST_IMAGE2 = 2;
    public final int REQUEST_IMAGE3 = 3;
    public final int CHOOSE_NUMBER_REQUEST = 4;
    public final int REQUEST_IDCARD = 5;

    private static final String VALIDATE_CARD = "1"; // 1是身份证识别
    private static final String VALIDATE_IMG = "0"; // 0是持证上传相片

    ImageView camera1, image1, watermark1, camera2, image2, watermark2, camera3, image3, watermark3;
    String image1Id = "";
    String image2Id = "";
    String image3Id = "";
    String filePath1, filePath2, filePath3;
    String subCompanyId, stationId, handlePointId;
    String ocr_cardnum = "";
    String ocr_name = "";
    String ocr_address = "";
    String choosedNumber = "";               //新选择的手机号码
    String choosedNumberTeleUse = ""; //新选择的手机号码之号码仓库
    Double payValue = 0.00d;                 //支付金额
    LinearLayout ll_number, ll_pay;
    private EditText pay_value;
    Button bn_choosedNumber,bn_choosedNumber_not;
    boolean choosedNumberFlag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcribe_camera);
        mContext = this;

        camera1 = (ImageView) findViewById(R.id.camera1);
        image1 = (ImageView) findViewById(R.id.image1);
        watermark1 = (ImageView) findViewById(R.id.watermark1);
        camera2 = (ImageView) findViewById(R.id.camera2);
        image2 = (ImageView) findViewById(R.id.image2);
        watermark2 = (ImageView) findViewById(R.id.watermark2);
        camera3 = (ImageView) findViewById(R.id.camera3);
        image3 = (ImageView) findViewById(R.id.image3);
        watermark3 = (ImageView) findViewById(R.id.watermark3);
        ll_number = (LinearLayout) findViewById(R.id.ll_number);
        ll_pay = (LinearLayout) findViewById(R.id.ll_pay);

        TextView cdFront = (TextView) findViewById(R.id.cdFront);
        TextView cdBehind = (TextView) findViewById(R.id.cdBehind);

        SpannableStringBuilder builder = new SpannableStringBuilder(cdFront.getText().toString());
        SpannableStringBuilder builder1 = new SpannableStringBuilder(cdBehind.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder1.setSpan(redSpan, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cdFront.setText(builder);
        cdBehind.setText(builder1);

        pay_value = (EditText) findViewById(R.id.pay_value);
        pay_value.addTextChangedListener(new MyTextWatcher(pay_value));
        Intent intent = getIntent();
        subCompanyId = intent.getStringExtra("subCompanyId");
        stationId = intent.getStringExtra("stationId");
        handlePointId = intent.getStringExtra("handlePointId");
        bn_choosedNumber = (Button) findViewById(R.id.bn_choosedNumber);
        bn_choosedNumber_not = (Button) findViewById(R.id.bn_choosedNumber_not);

        refresh();
    }

    private class MyTextWatcher implements TextWatcher {
        private EditText mEditText;
        private CharSequence charSequence;

        public MyTextWatcher(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.i("beforeTextChanged=",s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i("onTextChanged=",s.toString());
            charSequence = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.i("afterTextChanged=",charSequence.toString());
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            if (charSequence.length() > 0 && charSequence.charAt(0) == '.') {
                mEditText.setText("0.");
                mEditText.setSelection(2);
            }
            int dotNum = charSequence.toString().indexOf(".");
            int len = charSequence.length() - 3;
            if (dotNum != -1) {
                if (dotNum < len) {
                    s.delete(start - 1, end);
                }
                if (dotNum > 5 && charSequence.length() >= 8) {
                    s.delete(start - 1, end);
                }
            } else {
                if (charSequence.length() >= 6) {
                    s.delete(start - 1, end);
                }
            }

        }
    }




    public void doBack(View v) {
        onBackPressed();
    }

    private boolean isPay = false, isNumber = false;
    private JSONArray areaList = new JSONArray();
    private JSONArray sectionList = new JSONArray();
    private String databaseId = "";

    private void refresh() {
        new BaseTask<String, String, JSONObject>(this, "请稍后...") {
            @Override
            protected void onSuccess(JSONObject jsonObject) {
                isPay = jsonObject.optBoolean("isPay");
                isNumber = jsonObject.optBoolean("isNumber");
                areaList = jsonObject.optJSONObject("numberData").optJSONArray("areaList");
                sectionList = jsonObject.optJSONObject("numberData").optJSONArray("sectionList");
                databaseId = jsonObject.optJSONObject("numberData").optString("databaseId");
                if (isNumber) {
                    ll_number.setVisibility(View.VISIBLE);
                } else {
                    ll_number.setVisibility(View.GONE);
                }
                if (isPay) {
                    ll_pay.setVisibility(View.VISIBLE);
                } else {
                    ll_pay.setVisibility(View.GONE);
                }
            }

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().isPayOrNumberTranscribe(mContext, handlePointId);
            }
        }.execute("");
    }


    @Override
    public void onBackPressed() {

        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("是否终止录入?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cleanFiles();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    //因纳伟盛读取二代身份证
    public void doReadCard(View view) {

        String[] titleList = {"成都因纳伟盛","山东信通"};
        new AlertDialog.Builder(mContext).setTitle("请选择身份实名认证设备").setItems(titleList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                switch (i){
                    case 0://成都因纳伟盛
                        intent.setClass(mContext,InvsMainActivity.class);
                        break;
                    case 1://山东信通
                        intent.setClass(mContext,MobileReaderActivity.class);
                        break;
                }
                startActivityForResult(intent, REQUEST_IDCARD);
            }
        }).show();
//        Intent intent = new Intent(this, InvsMainActivity.class);
//        startActivityForResult(intent, REQUEST_IDCARD);

    }

    public void doCamera1(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("validate", VALIDATE_CARD);// 1是身份证识别
        i.putExtra("isIdCard", true);
        i.putExtra("isBL", true);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        startActivityForResult(i, REQUEST_IMAGE1);
    }

    public void doCamera2(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("isBL", true);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        i.putExtra("validate", VALIDATE_IMG);// 0不需要识别
        startActivityForResult(i, REQUEST_IMAGE2);
    }

    public void doCamera3(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("isBL", true);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        i.putExtra("validate", VALIDATE_IMG);// 0不需要识别
        startActivityForResult(i, REQUEST_IMAGE3);
    }

    public void selectNumber(View v) {
        choosedNumberFlag = true;
        bn_choosedNumber.setBackgroundResource(R.drawable.background_cicle_red);
        bn_choosedNumber_not.setBackgroundResource(R.drawable.background_cicle_jacinth);
//        bn_choosedNumber.setBackground(getResources().getDrawable(R.drawable.background_cicle_red));
        Intent i = new Intent(this, ChooseNumberActivity.class);
        i.putExtra("areaList", areaList.toString());
        i.putExtra("sectionList", sectionList.toString());
        i.putExtra("databaseId", databaseId);
        startActivityForResult(i, CHOOSE_NUMBER_REQUEST);
    }

    public void selectNumber_not(View v){
        choosedNumberFlag = false;
        choosedNumber = "";
        choosedNumberTeleUse = "";
        bn_choosedNumber.setText("");
        bn_choosedNumber_not.setBackgroundResource(R.drawable.background_cicle_red);
        bn_choosedNumber.setBackgroundResource(R.drawable.background_cicle_jacinth);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_IMAGE1:
                    filePath1 = data.getExtras().getString("filePath");
                    image1Id = data.getExtras().getString("imageId");

//                    ocr_cardnum = data.getExtras().getString("cardnum");
//                    ocr_name = data.getExtras().getString("name");
//                    ocr_address = data.getExtras().getString("address");

                    camera1.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                    watermark1.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(TranscribeCameraActivity.this, new File(filePath1), image1);
                    break;
                case REQUEST_IMAGE2:
                    filePath2 = data.getExtras().getString("filePath");
                    image2Id = data.getExtras().getString("imageId");
                    camera2.setVisibility(View.GONE);
                    image2.setVisibility(View.VISIBLE);
                    watermark2.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(TranscribeCameraActivity.this, new File(filePath2), image2);
                    break;
                case REQUEST_IMAGE3:
                    filePath3 = data.getExtras().getString("filePath");
                    image3Id = data.getExtras().getString("imageId");
                    camera3.setVisibility(View.GONE);
                    image3.setVisibility(View.VISIBLE);
                    watermark3.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(TranscribeCameraActivity.this, new File(filePath3), image3);
                    break;
                case CHOOSE_NUMBER_REQUEST: //选择手机号
                    choosedNumber = data.getExtras().getString("number");
                    choosedNumberTeleUse = data.getExtras().getString("tele_use");
                    bn_choosedNumber.setText(choosedNumber);
                    break;
                case REQUEST_IDCARD:
                    ocr_cardnum = data.getExtras().getString("cardnum");
                    ocr_name = data.getExtras().getString("name");
                    ocr_address = data.getExtras().getString("address");

                    ((TextView)findViewById(R.id.userName)).setText(ocr_name);
                    ((TextView)findViewById(R.id.card_number)).setText(ocr_cardnum);
                    ((TextView)findViewById(R.id.card_address)).setText(ocr_address);
                    break;
                default:
                    break;
            }
        else switch (requestCode){
            case REQUEST_IMAGE1:
                Toast.makeText(mContext,"身份证信息未读取，请读取身份证信息",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void doDel1(View v) {
        camera1.setVisibility(View.VISIBLE);
        image1.setVisibility(View.GONE);
        watermark1.setVisibility(View.GONE);
        image1Id = "";

        File f;
        if (filePath1 != null) {
            f = new File(filePath1);
            f.delete();
        }

        ocr_cardnum = "";
        ocr_name = "";
        ocr_address = "";
    }

    public void doDel2(View v) {
        camera2.setVisibility(View.VISIBLE);
        image2.setVisibility(View.GONE);
        watermark2.setVisibility(View.GONE);
        image2Id = "";

        File f;
        if (filePath2 != null) {
            f = new File(filePath2);
            f.delete();
        }
    }

    public void doDel3(View v) {
        camera3.setVisibility(View.VISIBLE);
        image3.setVisibility(View.GONE);
        watermark3.setVisibility(View.GONE);
        image3Id = "";

        File f;
        if (filePath3 != null) {
            f = new File(filePath3);
            f.delete();
        }
    }

    public void doSubmit(View v) {
        if(ocr_cardnum.equals("") || ocr_name.equals("")){
            Toast.makeText(this, "读取身份证信息失败，请重新读卡", Toast.LENGTH_SHORT).show();
            return;
        }
        if (image1Id.equals("")) {
            Toast.makeText(this, "请上传身份证正面", Toast.LENGTH_SHORT).show();
            return;
        }

        if (image2Id.equals("")) {
            Toast.makeText(this, "请上传身份证反面", Toast.LENGTH_SHORT).show();
            return;
        }

        if (image3Id.equals("")) {
            Toast.makeText(this, "请上传预受理单", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNumber) {
            if (choosedNumberFlag && (choosedNumber.equals("") || choosedNumberTeleUse.equals(""))) {
                Toast.makeText(this, "请选择号码", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String value = ((EditText) findViewById(R.id.pay_value)).getText().toString();
        if(isPay && !value.equals("")){
                payValue =  Double.valueOf(value);
        }
        sub();
    }

    private void sub() {
        new BaseTask<String, String, JSONObject>(this, "订单提交中...") {


            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("idcardFront", image1Id);
                map.put("idcardBack", image2Id);
                map.put("picPre", image3Id);
                map.put("ocr_cardnum", ocr_cardnum);
                map.put("ocr_name", ocr_name);
                map.put("ocr_address", ocr_address);
                map.put("number", choosedNumber);
                map.put("choosedNumberTeleUse", choosedNumberTeleUse);
                map.put("payValue", payValue);


                map.put("subCompanyId", subCompanyId);
                map.put("stationId", stationId);
                map.put("handlePointId", handlePointId);

                return CRApplication.getApp().submitTranscribe(TranscribeCameraActivity.this, map);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {

                Intent i = new Intent(TranscribeCameraActivity.this, TranscribeOrderResultActivity.class);
                i.putExtra("orderId",jsonObject.optString("orderId"));
                i.putExtra("needPay",jsonObject.optBoolean("needPay"));
                i.putExtra("payValue",jsonObject.optString("payValue"));
                startActivity(i);
                cleanFiles();
                finish();
            }

        }.execute("");

    }

    private void cleanFiles() {

        File f;
        if (filePath1 != null) {
            f = new File(filePath1);
            f.delete();
        }
        if (filePath2 != null) {
            f = new File(filePath2);
            f.delete();
        }
        if (filePath3 != null) {
            f = new File(filePath3);
            f.delete();
        }
    }

}
