package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deng on 2016/5/23.
 */
public class AddPartnerActivity extends Activity {
    private Context mContext;
    private String phoneNumber = "", codeNumber = "", partnerName = "", channelId = "", localPhoneNumber = "";
    private String URL;
    private String QRcodeURL;
    private Spinner spinnerchannel;
    private ArrayAdapter<String> channeladapter;
    private AsynImageLoaderWithSDCache imageLoader;
    private JSONArray list;
    private List<String> channelnamelist;
    private List<String> channelIdlist;
    int sendLimitTime = 0;
    private EditText name, phone, code;
    private Button sendcode;
    private ImageView QRcodeImageView;
    private LinearLayout MsgLinearLayout, QRcodeLinearLayout;
    private List<JSONObject> channelList = new ArrayList<JSONObject>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);
        mContext = this;
        init();
        getchannel();
    }

    private void init() {
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        code = (EditText) findViewById(R.id.code);
        sendcode = (Button) findViewById(R.id.sendcode);
        spinnerchannel = (Spinner) findViewById(R.id.spinner_channel);
        MsgLinearLayout = (LinearLayout) findViewById(R.id.layout_Msg);
        QRcodeLinearLayout = (LinearLayout) findViewById(R.id.layout_QRcode);
        QRcodeImageView = (ImageView) findViewById(R.id.img_QRcode);
        imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), mContext);
        localPhoneNumber = CRApplication.getPhone(mContext);  //获取本地电话号码
        URL = "http://wap.crunii.com/ceds-server/apkWap/addWapOpen/" + localPhoneNumber;
        QRcodeURL = "http://img.cq.ct10000.com/img-manager/barcode/erweima?str=" + URL + "&logo=1/xietong.gif"; //生成二维码
        imageLoader.showImageAsyn(new SoftReference<Object>(QRcodeImageView), QRcodeURL, R.drawable.mode_loading);
        ((RadioButton) findViewById(R.id.radioMsg)).setChecked(true);
    }

    public void getchannel() {
        new BaseTask<String, String, JSONObject>(mContext, "请稍后...") {
            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                String type = "add";
                return CRApplication.getApp().checkPartnerSystem(mContext, "", "", type, "");
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                list = jsonObject.optJSONArray("channelList");
                channelIdlist = new ArrayList<String>();
                channelnamelist = new ArrayList<String>();
                for (int i = 0; i < list.length(); i++) {
                    try {
                        channelList.add(list.getJSONObject(i));
                        channelnamelist.add(list.getJSONObject(i).optString("name"));
                        channelIdlist.add(list.getJSONObject(i).optString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                channeladapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_layout2, channelnamelist);
                spinnerchannel.setAdapter(channeladapter);
                //注册事件
                spinnerchannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        channelId = channelIdlist.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(mContext, "没有改变的处理", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.execute("");

        QRcodeImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).create();
                dialog.show();
                Window window = dialog.getWindow();
                window.setContentView(R.layout.spinner_item_layouttype);
                TextView savetv = (TextView) window.findViewById(R.id.text1);
                savetv.setText("保存为本地图片");
                window.findViewById(R.id.save).

                        setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   saveQRcodepic(v);
                                                   dialog.dismiss();
                                               }
                                           }

                        );
            }


        });

    }

    /*
    短信验证码
     */
    public void Msg(View view) {
        MsgLinearLayout.setVisibility(View.VISIBLE);
        QRcodeLinearLayout.setVisibility(View.GONE);
        ((RadioButton) findViewById(R.id.radioQR)).setChecked(false);
    }

    /*
    二维码
     */
    public void QRcode(View view) {
        MsgLinearLayout.setVisibility(View.GONE);
        QRcodeLinearLayout.setVisibility(View.VISIBLE);
        ((RadioButton) findViewById(R.id.radioMsg)).setChecked(false);
    }

    /*
    保存二维码图片到本地
     */
    public void saveQRcodepic(View view) {
        //将ImageView中的图片转换成Bitmap

        QRcodeImageView.buildDrawingCache();

        Bitmap bitmap = QRcodeImageView.getDrawingCache();

//将Bitmap 转换成二进制，写入本地

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture");
        if (!dir.isFile()) {
            dir.mkdir();
        }
        File file = new File(dir, "xietong.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(byteArray, 0, byteArray.length);

            fos.flush();

            //用广播通知相册进行更新相册

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            mContext.sendBroadcast(intent);
            MyToast.show("保存成功", mContext);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * 获取验证码
     */
    public void doGetCode(View view) {
        if (!checkCode()) return;
        new BaseTask<String, String, Boolean>(mContext, "请稍后...") {
            @Override
            protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().partnerVerify(mContext, phoneNumber);
            }

            @Override
            protected void onSuccess(Boolean arg0) {
                Toast.makeText(mContext, "验证码已发送到您的手机", Toast.LENGTH_LONG)
                        .show();
                sendLimitTime = 30;
                sendcode.setEnabled(false);
                handler.postDelayed(runnable, 1000);
            }
        }.execute("");
    }

    /*
    保存信息
    */
    public void save(View view) {
        if (!checkCode()) return;
        if (!checkNextStep()) return;
        partnerName = name.getText().toString();
        if (partnerName.trim().length() == 0) {
            Toast.makeText(mContext, "请输入伙伴姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("phoneNumber", phoneNumber);
        postparams.put("code", codeNumber);
        postparams.put("partnerName", partnerName);
        postparams.put("channelId", channelId);
        HttpTool.sendPost(mContext, Constant.URL.SAVEPARTNERSYSTEM, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                Boolean isSuccess = (Boolean) record.get("isSuccess");
                if (isSuccess) {
                    MyToast.show("保存成功", mContext);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    MyToast.show(record.get("failReason").toString(), mContext);
                }
            }

        });

    }

    private boolean checkCode() {
        phoneNumber = phone.getText().toString();
        if (phoneNumber.trim().length() == 0) {
            Toast.makeText(mContext, "请输入伙伴手机号", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (phoneNumber.trim().length() != 11) {
            Toast.makeText(mContext, "请正确输入伙伴手机号", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private boolean checkNextStep() {
        codeNumber = code.getText().toString();
        if (codeNumber.trim().length() == 0) {
            Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendLimitTime--;
            if (sendLimitTime > 0) {
                sendcode.setText("" + sendLimitTime + "秒后重试");
                handler.postDelayed(this, 1000);
            } else {
                sendcode.setText(R.string.getYanzhengma);
                sendcode.setEnabled(true);
            }
        }
    };


    public void doBack(View v) {
        onBackPressed();
    }
}
