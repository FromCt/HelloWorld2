package com.crunii.android.fxpt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.base.ViewEvent;
import com.crunii.android.fxpt.base.ViewEventType;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ct on 15/11/18.
 * 网店管理查看二维码
 */
public class shopManagementSeeCodeActivity extends BaseActivity {

    private EditText shopUrl;//网店活动链接
    private ImageView imgView;//二维码链接
    private String shop_url;
    private String imgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_management_seecode);

        CRApplication.getInstance().addActivity(this);//为了退出应用程序

        shopUrl = (EditText) findViewById(R.id.sms_shopUrl);
        imgView= (ImageView) findViewById(R.id.sms_img);
        TextView textView = (TextView) findViewById(R.id.head_tv_user);
        textView.setText(CRApplication.getName(shopManagementSeeCodeActivity.this));

        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("id", CRApplication.getId(shopManagementSeeCodeActivity.this));
        sendPost(Constant.URL.SHOPMANAGEMENTSEECODE, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                super.dealRecord(record);
                shop_url = (String) record.get("shopUrl");
                imgUri = (String) record.get("imgUrl");
                if (shop_url != null) {
                    shopUrl.setHint(shop_url);
                } else {
                    Log.i("sms", "shopUrl==null");
                }
//java.net.URLEncoder.encode(imgUri)
                getImg(imgUri, imgView);

            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(shopManagementSeeCodeActivity.this);
                builder.setTitle("提示")
                        .setMessage("    保存图片到本地（将保存到本地fxpt文件夹下）。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Bitmap bitmap = HttpTool.getImg(imgUri, 5000, 5000);
                                    boolean bool = storeBitmap(bitmap);
                                    if (bool == true) {
                                        MyToast.show("保存成功", shopManagementSeeCodeActivity.this);
                                    } else {
                                        MyToast.show("保存失败", shopManagementSeeCodeActivity.this);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

    }

//存储图片到fxpt目录下
    private boolean storeBitmap(Bitmap bitmap) {
        String dirName="fxpt";

        File dir=new File(Environment.getExternalStorageDirectory(),dirName);
        if(dir.exists()){
            Log.i("smsa","getDireByName dir is exists==");
            File file= new File(dir, "seeCode.png");
            if (file==null){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
               boolean bool= bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                return  bool;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.i("smsa","fxpt dir is not exists==");
        }
        return false;
    }


    //店铺短息分享
    @ViewEvent(id = R.id.smsButton, eventType = {ViewEventType.CLICK})
    public void smsButton(View v) {

        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("shopUrl", shop_url);
        postparams.put("id", CRApplication.getId(shopManagementSeeCodeActivity.this));
        postparams.put("phone", CRApplication.getPhone(shopManagementSeeCodeActivity.this));

        sendPost(Constant.URL.SHOPMANAGEMWNTSMS, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                String des = (String) record.get("des");

                //showMessage("已将你的店铺网站发送到你的手机短息上");

                AlertDialog.Builder builder = new AlertDialog.Builder(shopManagementSeeCodeActivity.this);
                builder.setTitle("提示")
                        .setMessage("    "+des)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

            }
        });

    }

    private static final String  WOWENWEN = "1";//1 带表设置我问问。

    //店铺我问问设置
    @ViewEvent(id = R.id.askSet, eventType = {ViewEventType.CLICK})
    public void askSet(View v) {

        AlertDialog.Builder builder=new AlertDialog.Builder(shopManagementSeeCodeActivity.this);
        builder.setTitle("设置“我问问”功能")
                .setMessage("如您同意设置“我问问”功能，客户下单时可选择给您留言咨询，或者查看您的联系方式。")
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setWoWenWen(WOWENWEN);
                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setWoWenWen("");
                    }
                }).show();
    }

    public void setWoWenWen(String str){
        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("yes_no",str);
        postparams.put("id", CRApplication.getId(shopManagementSeeCodeActivity.this));
        postparams.put("phone", CRApplication.getPhone(shopManagementSeeCodeActivity.this));
        sendPost(Constant.URL.SHOPMANAGEMWNTWOWENWEN, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                String des  = (String) record.get("des");
                showMessage(des);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
