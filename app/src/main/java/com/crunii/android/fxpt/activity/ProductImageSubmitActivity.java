package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ProductImageSubmitActivity extends Activity {
    public final int REQUEST_IMAGE1 = 1;
    public final int REQUEST_IMAGE2 = 2;
    public final int REQUEST_IMAGE3 = 3;
    public final int REQUEST_IMAGE4 = 5;

    //read IDCard
    public final int REQUEST_IDCARD = 4;


    Bundle extras;

    ImageView camera1, image1, watermark1, camera2, image2, watermark2, camera3, image3, watermark3, camera4, image4, watermark4;
    String image1Id = "";
    String image2Id = "";
    String image3Id = "";
    String image4Id = "";
    String filePath1, filePath2, filePath3,filePath4;
    String ocr_cardnum = "";
    String ocr_name = "";
    String ocr_address = "";
    String contactNumber = "";
    String readIDCardType = "";
    Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productimagesubmit);
        mContext = this;
        extras = getIntent().getExtras();

        camera1 = (ImageView) findViewById(R.id.camera1);
        image1 = (ImageView) findViewById(R.id.image1);
        watermark1 = (ImageView) findViewById(R.id.watermark1);
        camera2 = (ImageView) findViewById(R.id.camera2);
        image2 = (ImageView) findViewById(R.id.image2);
        watermark2 = (ImageView) findViewById(R.id.watermark2);
        camera3 = (ImageView) findViewById(R.id.camera3);
        image3 = (ImageView) findViewById(R.id.image3);
        watermark3 = (ImageView) findViewById(R.id.watermark3);
        camera4 = (ImageView) findViewById(R.id.camera4);
        image4 = (ImageView) findViewById(R.id.image4);
        watermark4 = (ImageView) findViewById(R.id.watermark4);

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

        if (!extras.getBoolean("uploadCitizenPhoto")) {
           // findViewById(R.id.ll_custMessage).setVisibility(View.GONE); // 20151016 身份证读取改为两种方式：读卡设备＋上传照片
            findViewById(R.id.image1view).setVisibility(View.GONE);
            findViewById(R.id.image2view).setVisibility(View.GONE);
            findViewById(R.id.image4view).setVisibility(View.GONE);
        }
    }

    public void doBack(View v) {
        onBackPressed();
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
         new AlertDialog.Builder(ProductImageSubmitActivity.this).setTitle("请选择身份实名认证设备").setItems(titleList, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 Intent intent = new Intent();
                 switch (i){
                     case 0://成都因纳伟盛
                         intent.setClass(ProductImageSubmitActivity.this,InvsMainActivity.class);
                         break;
                     case 1://山东信通
                         intent.setClass(ProductImageSubmitActivity.this,MobileReaderActivity.class);
                         break;
                 }
                 startActivityForResult(intent, REQUEST_IDCARD);
             }
         }).show();
//        Intent intent = new Intent(this, InvsMainActivity.class);
//        startActivityForResult(intent, REQUEST_IDCARD);

    }


    public void doCamera1(View v) {


        new BaseTask<String, String, JSONObject>(mContext, "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().isUploadImage(mContext);
            }
            @Override
            protected void onSuccess(JSONObject result) {
                boolean flag = result.optBoolean("flag");
                final View dialogView = View.inflate(mContext, R.layout.choose_idcard_way_dialog, null);
                if(!flag){
                    dialogView.findViewById(R.id.upload_image).setVisibility(View.GONE);
                }
                final RadioGroup rg = (RadioGroup) dialogView.findViewById(R.id.rg_choose_read_card_way);
                final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
                dialog.show();
                dialog.getWindow().setContentView(dialogView);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Intent intent = new Intent();
                        switch (checkedId) {
                            case R.id.upload_image://上传图片
                                readIDCardType = "1";
                                intent.setClass(ProductImageSubmitActivity.this, UploadImageVerifyActivity.class);
                                intent.putExtra("isIdCard", true);
                                intent.putExtra("validate", "1");//1是身份证识别 ，0不需要识别，只上传
                                intent.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
                                intent.putExtra("modifyOcrCarNum", extras.getBoolean("modifyOcrCarNum"));
                                intent.putExtra("modifyOcrName", extras.getBoolean("modifyOcrName"));
                                intent.putExtra("modifyOcrAddress", extras.getBoolean("modifyOcrAddress"));
                                startActivityForResult(intent, REQUEST_IMAGE1);
                                dialog.dismiss();
                                break;
                            case R.id.cdyws_read://成都因纳伟盛
                                readIDCardType = "2";
                                intent.setClass(ProductImageSubmitActivity.this, InvsMainActivity.class);
                                startActivityForResult(intent, REQUEST_IMAGE1);
                                dialog.dismiss();
                                break;
                            case R.id.sdxt_read://山东信通
                                readIDCardType = "3";
                                intent.setClass(ProductImageSubmitActivity.this, MobileReaderActivity.class);
                                startActivityForResult(intent, REQUEST_IMAGE1);
                                dialog.dismiss();
                                break;
                            default:
                                Toast.makeText(mContext, "请选择身份证读取方式", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
//                Button commit = (Button) dialogView.findViewById(R.id.commit);
//                commit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int checkedId = rg.getCheckedRadioButtonId();
//
//                    }
//                });

            }
        }.execute("");

    }

    public void doCamera2(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("validate", "0");//1是身份证识别 ，0不需要识别，只上传
        i.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        startActivityForResult(i, REQUEST_IMAGE2);
    }

    public void doCamera3(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("validate", "0");//1是身份证识别 ，0不需要识别，只上传
        i.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        startActivityForResult(i, REQUEST_IMAGE3);
    }

    public void doCamera4(View v) {
        Intent i = new Intent(this, UploadImageVerifyActivity.class);
        i.putExtra("validate", "0");//1是身份证识别 ，0不需要识别，只上传
        i.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
        startActivityForResult(i, REQUEST_IMAGE4);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_IMAGE1:
                    filePath1 = data.getExtras().getString("filePath");
                    image1Id = data.getExtras().getString("imageId");

                    ocr_cardnum = data.getExtras().getString("cardnum");
                    ocr_name = data.getExtras().getString("name");
                    ocr_address = data.getExtras().getString("address");
                    contactNumber = data.getExtras().getString("contactNumber");

                    ((TextView)findViewById(R.id.userName)).setText(ocr_name);
                    ((TextView)findViewById(R.id.card_number)).setText(ocr_cardnum);
                    ((TextView) findViewById(R.id.card_address)).setText(ocr_address);

                    camera1.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                    watermark1.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(ProductImageSubmitActivity.this, new File(filePath1), image1);
                    break;
                case REQUEST_IMAGE2:
                    filePath2 = data.getExtras().getString("filePath");
                    image2Id = data.getExtras().getString("imageId");
                    camera2.setVisibility(View.GONE);
                    image2.setVisibility(View.VISIBLE);
                    watermark2.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(ProductImageSubmitActivity.this, new File(filePath2), image2);
                    break;
                case REQUEST_IMAGE3:
                    filePath3 = data.getExtras().getString("filePath");
                    image3Id = data.getExtras().getString("imageId");
                    camera3.setVisibility(View.GONE);
                    image3.setVisibility(View.VISIBLE);
                    watermark3.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(ProductImageSubmitActivity.this, new File(filePath3), image3);
                    break;
                case REQUEST_IMAGE4:
                    filePath4 = data.getExtras().getString("filePath");
                    image4Id = data.getExtras().getString("imageId");
                    camera4.setVisibility(View.GONE);
                    image4.setVisibility(View.VISIBLE);
                    watermark4.setVisibility(View.VISIBLE);
                    ImageHelper.showImagePreview(ProductImageSubmitActivity.this, new File(filePath4), image4);
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

    public void doDel4(View v) {
        camera4.setVisibility(View.VISIBLE);
        image4.setVisibility(View.GONE);
        watermark4.setVisibility(View.GONE);
        image4Id = "";

        File f;
        if (filePath4 != null) {
            f = new File(filePath4);
            f.delete();
        }
    }

    public void doSubmit(View v) {

        if (extras.getBoolean("uploadCitizenPhoto")) {
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

            if(image4Id.equals("")){
                Toast.makeText(this, "请上传手持证件照", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (image3Id.equals("")) {
            Toast.makeText(this, "请上传预受理单", Toast.LENGTH_SHORT).show();
            return;
        }

        //如果是加装且后台配置了验证老用户信息，则需要验证扫描信息与原信息匹配
        if (ProductInfoSubmitActivity.hasAdd(extras) && extras.getBoolean("verifyOldInfo")) {
            if (!extras.getString("verifiedCitizenId").equals(ocr_cardnum)) {
                Toast.makeText(this, "对不起，您所读取的身份证信息和客户原信息不符，请重新读卡，谢谢", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "对不起，您所上传的身份证信息与客户的原信息不符，请重新拍照上传。", Toast.LENGTH_LONG).show();
                return;
            }
        }


        Intent i = new Intent(ProductImageSubmitActivity.this, ProductInfoSubmitActivity.class);
        i.putExtras(extras);

        i.putExtra("ocr_cardnum", ocr_cardnum);
        i.putExtra("ocr_name", ocr_name);
        i.putExtra("ocr_address", ocr_address);
        i.putExtra("contactNumber", contactNumber);

        i.putExtra("image1Id", image1Id);
        i.putExtra("image2Id", image2Id);
        i.putExtra("image3Id", image3Id);
        i.putExtra("image4Id", image4Id);
        i.putExtra("readIDCardType", readIDCardType);

        startActivity(i);

        cleanFiles();
        finish();
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
        if (filePath4 != null) {
            f = new File(filePath4);
            f.delete();
        }
    }

}
