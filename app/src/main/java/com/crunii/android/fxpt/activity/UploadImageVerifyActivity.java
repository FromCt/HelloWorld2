/**
 * Copyright 2011-2012. Chongqing CRun Information Industry Co.,Ltd.
 * All rights reserved. <a>http://www.crunii.com</a>
 */
package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.Encryption;
import com.crunii.android.fxpt.util.FileProgressListener;
import com.crunii.android.fxpt.util.ImageHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadImageVerifyActivity extends Activity {
    private boolean isIdCard = false;
    private boolean verifyImagePassed = false;
	private ImageView imageView1;
	private Button mBt_upimg, mBt_reshoot;

	private String filePath = "";
    private String validate = "0";//1是身份证识别 ，0不需要识别，只上传
    private boolean isBL = false; //用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp
    private String imageId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            isIdCard = extras.getBoolean("isIdCard");
            validate = extras.getString("validate");  //20150828修改为不识别图片 ，    原来： extras.getString("validate");
            isBL = extras.getBoolean("isBL");
        }
        //20150828 修改身份证读取数据方式改为机器读取+上传照片（上传照片根据后台权限控制）
		if(isIdCard) {
            if(isBL){//身份证不允许修改，地址和名字可以修改---20150320
                findViewById(R.id.ll_contact_phone).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.ll_contact_phone).setVisibility(View.GONE);
        }

        if(isIdCard) {
            if(isBL){//身份证不允许修改，地址和名字可以修改---20150320
                findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_contact_phone).setVisibility(View.GONE);
                ((EditText)findViewById(R.id.ocr_cardnum)).setEnabled(false);
                ((EditText)findViewById(R.id.ocr_name)).setEnabled(true);
                ((EditText)findViewById(R.id.ocr_address)).setEnabled(true);
            }else{
                findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                ((EditText)findViewById(R.id.ocr_cardnum)).setEnabled(extras.getBoolean("modifyOcrCarNum"));
                ((EditText)findViewById(R.id.ocr_name)).setEnabled(extras.getBoolean("modifyOcrName"));
                ((EditText)findViewById(R.id.ocr_address)).setEnabled(extras.getBoolean("modifyOcrAddress"));
            }
        } else {
            findViewById(R.id.card_view).setVisibility(View.GONE);
        }

        imageView1 = (ImageView) findViewById(R.id.iv_photo);
        mBt_upimg = (Button) findViewById(R.id.bt_upimg);
        mBt_reshoot = (Button) findViewById(R.id.bt_reshoot);


        handleEvent();
		
		launchCamera();
	}
	
	public void doBack(View v) {
		onBackPressed();
	}
	
	private void handleEvent() {


		mBt_upimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


                //如果拍摄的是身份证正面，则需要校验身份证信息
                if(isIdCard) {
                    if(!isBL && ((EditText)findViewById(R.id.contact_phone)).getText().toString().length() != 11) {
                        Toast.makeText(getBaseContext(), "联系电话必须是11位", Toast.LENGTH_SHORT).show();
                        return;
                    }
					//doSubmit();//20150828添加


                    //由于ocr的汉字识别率不高，因此可能需要允许用户手动输入，如果允许手动输入，那么就需要在提交前再次进行校验
                    if( ((EditText)findViewById(R.id.ocr_cardnum)).isEnabled() ||
                            ((EditText)findViewById(R.id.ocr_name)).isEnabled() ) {
                        verifyUserInputIDCard(((EditText)findViewById(R.id.ocr_name)).getText().toString(),
                                ((EditText)findViewById(R.id.ocr_cardnum)).getText().toString());
                    } else {
                        //如果不允许手动输入，则使用ocr接口的校验结果
                        if (!verifyImagePassed) {
                            Toast.makeText(UploadImageVerifyActivity.this, "身份证未通过校验，请重拍。", Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            doSubmit();
                        }
                    }
                }else{
                    doSubmit();
                }




			}
		});


        mBt_reshoot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				File f;
				f = new File(filePath);
				f.delete();
				
				launchCamera();

			}
		});
	}

    private void doSubmit(){
        Intent i = new Intent();
        if(isIdCard) {
            i.putExtra("cardnum", ((EditText)findViewById(R.id.ocr_cardnum)).getText().toString());
            i.putExtra("name", ((EditText)findViewById(R.id.ocr_name)).getText().toString());
            i.putExtra("address", ((EditText)findViewById(R.id.ocr_address)).getText().toString());
            if(!isBL){
                i.putExtra("contactNumber", ((EditText)findViewById(R.id.contact_phone)).getText().toString());
            }
        }
        i.putExtra("filePath", filePath);
        i.putExtra("imageId", imageId);
        if(imageId.equals("")){
            Toast.makeText(getApplicationContext(), "上传失败,请检查网络是否连接", Toast.LENGTH_LONG).show();
            new UploadImageTask(UploadImageVerifyActivity.this, "上传中...").execute();
            return ;
        }
        UploadImageVerifyActivity.this.setResult(RESULT_OK, i);
        finish();
    }


    private void verifyUserInputIDCard(final String nameStr, final String idStr){

        new BaseTask<String, String, Boolean>(this, "正在校验身份证...") {

            @Override
            protected Boolean doInBack(String... params) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().IDCardValidate(getApplicationContext(),
                        nameStr,
                        idStr);
            }

            @Override
            protected void onSuccess(Boolean result) {

                if(!result){
                    Toast.makeText(UploadImageVerifyActivity.this,"身份证校验失败,请正确填写姓名和身份证号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                doSubmit();
            }

        }.execute("");
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	    if (resultCode != RESULT_OK) {   
	        finish();
	        return;
	    }
	    
		switch (requestCode) {

			case 0: {
				filePath = data.getExtras().getString("path");
				ImageHelper.showImagePreview(UploadImageVerifyActivity.this, new File(filePath), imageView1);
                new UploadImageTask(UploadImageVerifyActivity.this, "上传中...").execute();

//                if(isIdCard) {
//                    new VerifyImageTask(UploadImageVerifyActivity.this, "正在识别身份证信息，请耐心等待...").execute();
//                }
			}
			break;
		
		}
	}

	private void launchCamera() {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UploadImageVerifyActivity.this);
			dialogBuilder.setTitle("错误");
			dialogBuilder.setMessage("需要SD卡");
			dialogBuilder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
						}
					});
			dialogBuilder.setCancelable(true);
			dialogBuilder.create().show();
		} else {
			Intent i = new Intent(UploadImageVerifyActivity.this, CustCamera.class);
			startActivityForResult(i, 0);
		}
	}

	private class UploadImageTask extends BaseTask<String, String, JSONObject> {

		public UploadImageTask(Context context, String loadingMsg) {
			super(context, loadingMsg);
		}

		@Override
		protected JSONObject doInBack(String... arg0)
				throws HttpException, IOException,
                TaskResultException {
			Map<String, Object> params = new HashMap<String, Object>();
			String userId = CRApplication.getId(context);
			String time = String.valueOf(new Date().getTime());
			String key= Encryption.MD5(Constant.localKeyStr + time + userId);
			params.put("userId", userId);
			params.put("time", time);
			params.put("key", key);
            params.put("validate", validate);

			return CRApplication.getApp().postFileUpload(
					new File(filePath), params,
					new FileProgressListener() {
						@Override
						public void onDownloadSize(long size) {
						}

						@Override
						public void onUpSize(long size) {
						}
					});
		}

		@Override
		protected void onSuccess(JSONObject jsonObject) {
			Log.e("onSuccess", jsonObject.toString());
			if (jsonObject.optString("result").equals("0")) {

                if(validate.equals("1")){//身份证
                    if (jsonObject.optString("validCode").equals("0")) {
                        verifyImagePassed = true;
                    } else {
                        verifyImagePassed = false;
                        Toast.makeText(getBaseContext(), "身份证信息未能正确识别", Toast.LENGTH_LONG).show();
                    }
                    ((EditText)findViewById(R.id.ocr_cardnum)).setText(jsonObject.optString("cardnum"));
                    ((EditText)findViewById(R.id.ocr_name)).setText(jsonObject.optString("name"));
                    ((EditText)findViewById(R.id.ocr_address)).setText(jsonObject.optString("address"));
                }
                imageId = jsonObject.optString("path");


			} else {
				Toast.makeText(getApplicationContext(), "上传失败 " + message, Toast.LENGTH_LONG).show();

			}

		}

		@Override
		protected void onError() {
            verifyImagePassed = false;
			Toast.makeText(getApplicationContext(), "上传失败 " + message, Toast.LENGTH_LONG).show();
		}

	}



}
