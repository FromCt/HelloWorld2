package com.crunii.android.fxpt.mobileReader;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.UploadImageVerifyActivity;

import cn.com.senter.helper.ConsantHelper;
import cn.com.senter.helper.ShareReferenceSaver;
import cn.com.senter.model.IdentityCardZ;
import cn.com.senter.sdkdefault.helper.Error;

/**
 * Created by speedingsnail on 15/9/7.
 */
public class MobileReaderActivity extends Activity{

    String cardnum = "";
    String name = "";
    String address = "";

    public final int REQUEST_IMAGE1 = 3;


    private final static String SERVER_KEY1 = "CN.COM.SENTER.SERVER_KEY1";
    private final static String PORT_KEY1 = "CN.COM.SENTER.PORT_KEY1";
    private final static String BLUE_ADDRESSKEY = "CN.COM.SENTER.BLUEADDRESS";
    private final static String KEYNM = "CN.COM.SENTER.KEY";

    private TextView tv_info;
    private TextView nameTextView;
    private TextView sexTextView;
    private TextView folkTextView;
    private TextView birthTextView;
    private TextView monthTextView;
    private TextView dayTextView;

    private TextView addrTextView;
    private TextView codeTextView;
    private TextView policyTextView;
    private TextView validDateTextView;
    private TextView dnTextView;
    private ImageView photoView;
    private TextView samidTextView;
    private Button buttonNFC, buttonOTG, buttonBT;
    private TextView readsamtype;

//    private TextView mplaceHolder;

    private String server_address = "";
    private int server_port = 0;

    public static Handler uiHandler;

    private NFCReaderHelper mNFCReaderHelper;
    private OTGReaderHelper mOTGReaderHelper;
    private BlueReaderHelper mBlueReaderHelper;

    private AsyncTask<Void, Void, String> nfcTask = null;

    //----蓝牙功能有关的变量----
    private BluetoothAdapter mBluetoothAdapter = null;            ///蓝牙适配器

    private int iselectNowtype = 0;
    private String Blueaddress = null;
    private boolean bSelServer = false;

//    private int totalcount;
//    private int failecount;

    private boolean isbule;
    private boolean isNFC;
    private boolean openblueok;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("MainActivity", "NFC 返回调用 onNewIntent");
        super.onNewIntent(intent);

        if (mNFCReaderHelper.isNFC(intent)) {

            if (nfcTask == null) {
                buttonNFC.setBackgroundResource(R.drawable.frame_button_p);
                buttonOTG.setBackgroundResource(R.drawable.frame_button_d);
                buttonBT.setBackgroundResource(R.drawable.frame_button_d);

                Log.e("MainActivity", "返回的intent可用");
                nfcTask = new NFCReadTask(intent).executeOnExecutor(Executors
                        .newCachedThreadPool());
            }
        } else {
            Log.e("MainActivity", "返回的intent不可用");
        }
    }

    public void  backfx(View view){
        if(cardnum.equals("") || name.equals("") || address.equals("")){
            Toast.makeText(MobileReaderActivity.this,"身份证信息未读出来",Toast.LENGTH_SHORT).show();
        }
        verifyUserInputIDCard(name,cardnum);
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
                    Toast.makeText(MobileReaderActivity.this,"身份证校验失败,请正确填写姓名和身份证号码",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(MobileReaderActivity.this, UploadImageVerifyActivity.class);
                i.putExtra("validate", "0");//1是身份证识别 ，0不需要识别，只上传
                i.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp

                startActivityForResult(i, REQUEST_IMAGE1);


//                Intent i = new Intent();
//                i.putExtra("cardnum", cardnum);
//                i.putExtra("name", name);
//                i.putExtra("address", address);
//                MobileReaderActivity.this.setResult(RESULT_OK,i);
//                finish();
            }

        }.execute("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mobile_reader_activity_main_new);

        uiHandler = new MyHandler(MobileReaderActivity.this);
        //setTitle("信通身份证阅读软件--OTG读卡");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        isbule = false;
        openblueok = false;

        mNFCReaderHelper = new NFCReaderHelper(this, uiHandler);
        mOTGReaderHelper = new OTGReaderHelper(this, uiHandler);
        mBlueReaderHelper = new BlueReaderHelper(this, uiHandler);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViews();
        Blueaddress = ShareReferenceSaver.getData(this, BLUE_ADDRESSKEY);
        initShareReference();
    }

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        Log.e("blue", "activity onStart");
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e("blue", "activity isEnabled");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 2);
            // Otherwise, setup the chat session
        } else {
            //此处应添加一个toast提示用户打开蓝牙功能---
        }
    }

    @Override
    public void onStop() {
        Log.e("blue", "activity onStop");
        isNFC = false;
//		if (openblueok == true && bSelServer == false)
//		 {
//			 mBlueReaderHelper.closeBlueConnect();
//		 }
        super.onStop();
    }

    @Override
    public void onPause() {
        isNFC = false;
        Log.e("blue", "onPause");
//			if (openblueok == true && bSelServer == false)
//			 {
//				 mBlueReaderHelper.closeBlueConnect();
//			 }

        super.onPause();
    }

    private static final int REQUEST_CONNECT_DEVICE = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("MAIN", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {


                    Blueaddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    if (!Blueaddress.matches("([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])")) {
                        tv_info.setText("address:" + Blueaddress + " is wrong, length = " + Blueaddress.length());
                        return;
                    }

                    ShareReferenceSaver.saveData(MobileReaderActivity.this, BLUE_ADDRESSKEY, Blueaddress);

                }
                break;
            case 2:
                if (resultCode == 100) {

                    this.server_address = data.getExtras().getString("address");
                    this.server_port = data.getExtras().getInt("port");

                    Log.e("MAIN", "onActivityResult: " + server_address);
                    Log.e("MAIN", "onActivityResult: " + server_port);

                    initShareReference();

                    bSelServer = false;
                }
                break;

            case REQUEST_IMAGE1:
            if (resultCode == RESULT_OK)
                        filePath1 = data.getExtras().getString("filePath");
                        image1Id = data.getExtras().getString("imageId");
                        Intent i = new Intent();
                        i.putExtra("filePath", filePath1);
                        i.putExtra("imageId", image1Id);
                        i.putExtra("cardnum", cardnum);
                        i.putExtra("name", name);
                        i.putExtra("address", address);
                        MobileReaderActivity.this.setResult(RESULT_OK, i);
                        finish();
                        break;

        }
    }

    private void initShareReference() {

        if (this.server_address.length() <= 0) {
            if (ShareReferenceSaver.getData(this, SERVER_KEY1).trim().length() < 1) {
                this.server_address = "senter-online.cn";
            } else {
                this.server_address = ShareReferenceSaver.getData(this, SERVER_KEY1);
            }
            if (ShareReferenceSaver.getData(this, PORT_KEY1).trim().length() < 1) {
                this.server_port = 10002;
            } else {
                this.server_port = Integer.valueOf(ShareReferenceSaver.getData(this, PORT_KEY1));
            }
        }


        mNFCReaderHelper.setServerAddress(this.server_address);
        mNFCReaderHelper.setServerPort(this.server_port);


        mOTGReaderHelper.setServerAddress(this.server_address);
        mOTGReaderHelper.setServerPort(this.server_port);

        //----实例化help类---

        mBlueReaderHelper.setServerAddress(this.server_address);
        mBlueReaderHelper.setServerPort(this.server_port);


    }

    private void initViews() {
        tv_info = (TextView) findViewById(R.id.tv_info);
        nameTextView = (TextView) findViewById(R.id.tv_name);
        sexTextView = (TextView) findViewById(R.id.tv_sex);
        folkTextView = (TextView) findViewById(R.id.tv_ehtnic);
        birthTextView = (TextView) findViewById(R.id.tv_birthday);
        monthTextView = (TextView) findViewById(R.id.tv_month);
        dayTextView = (TextView) findViewById(R.id.tv_day);
        addrTextView = (TextView) findViewById(R.id.tv_address);
        codeTextView = (TextView) findViewById(R.id.tv_number);
        policyTextView = (TextView) findViewById(R.id.tv_signed);
        validDateTextView = (TextView) findViewById(R.id.tv_validate);
        //dnTextView = (TextView) findViewById(R.id.tv_dn);
        //samidTextView = (TextView) findViewById(R.id.tv_samid);
        photoView = (ImageView) findViewById(R.id.iv_photo);
        buttonOTG = (Button) findViewById(R.id.buttonOTG);
        buttonNFC = (Button) findViewById(R.id.buttonNFC);
        buttonBT = (Button) findViewById(R.id.buttonBT);
//        mplaceHolder = (TextView) findViewById(R.id.placeHolder);
//		mtv_info1 = (TextView) findViewById(R.id.tv_info1);

        //屏幕大小
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
//        android.view.ViewGroup.LayoutParams p = mplaceHolder.getLayoutParams();
//        p.height = height / 6;
//        mplaceHolder.setLayoutParams(p);

        int width = wm.getDefaultDisplay().getWidth();
        android.view.ViewGroup.LayoutParams w = addrTextView.getLayoutParams();
        w.width = (width / 2) - 10;
        addrTextView.setLayoutParams(w);

        tv_info.setTextColor(Color.rgb(240, 65, 85));

        //20150910   ----屏蔽NFC功能，用蓝牙扫描替代
        buttonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serverIntent = new Intent(MobileReaderActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

//                isNFC = true;
//                isbule = false;
//                iselectNowtype = 1;
//                buttonNFC.setBackgroundResource(R.drawable.frame_button_p);
//                buttonOTG.setBackgroundResource(R.drawable.frame_button_d);
//                buttonBT.setBackgroundResource(R.drawable.frame_button_d);
//                readCardNFC();
                //setTitle("信通身份证阅读软件--NFC读卡");

            }
        });
        buttonOTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//				totalcount = 0;
//				failecount = 0;

                isbule = false;
                iselectNowtype = 2;
//                buttonNFC.setBackgroundResource(R.drawable.frame_button_d);
//                buttonOTG.setBackgroundResource(R.drawable.frame_button_p);
//                buttonBT.setBackgroundResource(R.drawable.frame_button_d);
                readCardOTG();
                //setTitle("信通身份证阅读软件--OTG读卡");
            }
        });

        buttonBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                isbule = true;
                iselectNowtype = 3;
//                buttonNFC.setBackgroundResource(R.drawable.frame_button_d);
//                buttonOTG.setBackgroundResource(R.drawable.frame_button_d);
//                buttonBT.setBackgroundResource(R.drawable.frame_button_p);

//				totalcount = 0;
//				failecount = 0;

                readCardBlueTooth();

            }
        });
    }

    public void ButtondefDrawable() {

//        buttonNFC.setBackgroundResource(R.drawable.frame_button_d);
//        buttonOTG.setBackgroundResource(R.drawable.frame_button_d);
//        buttonBT.setBackgroundResource(R.drawable.frame_button_d);

//        switch (iselectNowtype) {
//            case 1:
//                buttonNFC.setBackgroundResource(R.drawable.frame_button_p);
//                break;
//            case 2:
//                buttonOTG.setBackgroundResource(R.drawable.frame_button_p);
//                break;
//            case 3:
//                buttonBT.setBackgroundResource(R.drawable.frame_button_p);
//                break;
//        }

    }

    /**
     * NFC 方式读卡
     */
    protected void readCardNFC() {


        mNFCReaderHelper.read(this);
    }

    /**
     * OTG方式读卡
     *
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    protected void readCardOTG() {
        if (isNFC) {
            mNFCReaderHelper.disable(MobileReaderActivity.this);
            isNFC = false;
        }


        boolean bRet = mOTGReaderHelper.registerotg(this);
        if (bRet == true) {
            buttonOTG.setEnabled(false);
            buttonNFC.setEnabled(false);
            buttonBT.setEnabled(false);

            new OTGReadTask()
                    .executeOnExecutor(Executors.newCachedThreadPool());

        }
    }

    private class OTGReadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String strCardInfo) {

            buttonOTG.setEnabled(true);
            buttonNFC.setEnabled(true);
            buttonBT.setEnabled(true);

            if (TextUtils.isEmpty(strCardInfo)) {
                uiHandler.sendEmptyMessage(ConsantHelper.READ_CARD_FAILED);
                ButtondefDrawable();
//				failecount++;
                nfcTask = null;
                return;
            }

            if (strCardInfo.length() <= 2) {
                readCardFailed(strCardInfo);
                ButtondefDrawable();
                nfcTask = null;
//				failecount++;
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            IdentityCardZ mIdentityCardZ = new IdentityCardZ();

            try {
                mIdentityCardZ = (IdentityCardZ) objectMapper.readValue(
                        strCardInfo, IdentityCardZ.class);
            } catch (Exception e) {
                e.printStackTrace();
                nfcTask = null;
                return;
            }
            readCardSuccess(mIdentityCardZ);
            //totalcount++;
            try {

                Bitmap bm = BitmapFactory.decodeByteArray(mIdentityCardZ.avatar,
                        0, mIdentityCardZ.avatar.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                photoView.setMinimumHeight(dm.heightPixels);
                photoView.setMinimumWidth(dm.widthPixels);
                photoView.setImageBitmap(bm);
                Log.e(ConsantHelper.STAGE_LOG, "图片成功");
            } catch (Exception e) {
                Log.e(ConsantHelper.STAGE_LOG, "图片失败" + e.getMessage());
            }

            ButtondefDrawable();
            nfcTask = null;
//			 mtv_info1.setText("成功:" + String.format("%d", totalcount) + " 失败:" + String.format("%d", failecount));

            super.onPostExecute(strCardInfo);
//			readCardOTG();
        }

        @Override
        protected String doInBackground(Void... params) {
            String strCardInfo = mOTGReaderHelper.Read();
            return strCardInfo;
        }

    }

    ;

    private class NFCReadTask extends AsyncTask<Void, Void, String> {

        private Intent mIntent = null;

        public NFCReadTask(Intent i) {
            mIntent = i;
        }

        @Override
        protected String doInBackground(Void... params) {

            String strCardInfo = mNFCReaderHelper.readCardWithIntent(mIntent);

            return strCardInfo;
        }

        @Override
        protected void onPostExecute(String strCardInfo) {

            if (TextUtils.isEmpty(strCardInfo)) {
                uiHandler.sendEmptyMessage(ConsantHelper.READ_CARD_FAILED);
                ButtondefDrawable();
                nfcTask = null;
                return;
            }

            if (strCardInfo.length() <= 2) {
                readCardFailed(strCardInfo);
                ButtondefDrawable();
                nfcTask = null;
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            IdentityCardZ mIdentityCardZ = new IdentityCardZ();

            try {
                mIdentityCardZ = (IdentityCardZ) objectMapper.readValue(
                        strCardInfo, IdentityCardZ.class);
            } catch (Exception e) {
                e.printStackTrace();
                nfcTask = null;
                return;
            }
            readCardSuccess(mIdentityCardZ);

            try {

                Bitmap bm = BitmapFactory.decodeByteArray(mIdentityCardZ.avatar,
                        0, mIdentityCardZ.avatar.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                photoView.setMinimumHeight(dm.heightPixels);
                photoView.setMinimumWidth(dm.widthPixels);
                photoView.setImageBitmap(bm);
                Log.e(ConsantHelper.STAGE_LOG, "图片成功");
            } catch (Exception e) {
                Log.e(ConsantHelper.STAGE_LOG, "图片失败" + e.getMessage());
            }

            ButtondefDrawable();
            nfcTask = null;
            super.onPostExecute(strCardInfo);
        }

    }


    /**
     * 蓝牙读卡方式
     */
    private class BlueReadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String strCardInfo) {

            buttonOTG.setEnabled(true);
            buttonNFC.setEnabled(true);
            buttonBT.setEnabled(true);


            if (TextUtils.isEmpty(strCardInfo)) {
                uiHandler.sendEmptyMessage(ConsantHelper.READ_CARD_FAILED);
                ButtondefDrawable();
                nfcTask = null;

                return;
            }

            if (strCardInfo.length() <= 2) {
                readCardFailed(strCardInfo);
                ButtondefDrawable();
                nfcTask = null;

                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            IdentityCardZ mIdentityCardZ = new IdentityCardZ();

            try {
                mIdentityCardZ = (IdentityCardZ) objectMapper.readValue(
                        strCardInfo, IdentityCardZ.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(ConsantHelper.STAGE_LOG, "mIdentityCardZ failed");
                nfcTask = null;
                return;
            }

            readCardSuccess(mIdentityCardZ);

            try {

                Bitmap bm = BitmapFactory.decodeByteArray(mIdentityCardZ.avatar,
                        0, mIdentityCardZ.avatar.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                photoView.setMinimumHeight(dm.heightPixels);
                photoView.setMinimumWidth(dm.widthPixels);
                photoView.setImageBitmap(bm);
                Log.e(ConsantHelper.STAGE_LOG, "图片成功");
            } catch (Exception e) {
                Log.e(ConsantHelper.STAGE_LOG, "图片失败" + e.getMessage());
            }

            ButtondefDrawable();
            nfcTask = null;
            super.onPostExecute(strCardInfo);

        }

        @Override
        protected String doInBackground(Void... params) {

            String strCardInfo = mBlueReaderHelper.read();
            return strCardInfo;
        }


    }

    ;


    /**
     * 蓝牙读卡方式
     */
    protected void readCardBlueTooth() {
        if (isNFC) {
            mNFCReaderHelper.disable(MobileReaderActivity.this);
            isNFC = false;
        }

        if (Blueaddress == null) {
            Toast.makeText(this, "请选择蓝牙设备，再读卡!", Toast.LENGTH_LONG).show();
            return;
        }

        if (Blueaddress.length() <= 0) {
            Toast.makeText(this, "请选择蓝牙设备，再读卡!", Toast.LENGTH_LONG).show();
            return;
        }


        if (mBlueReaderHelper.openBlueConnect(Blueaddress)) {
            buttonOTG.setEnabled(false);
            buttonNFC.setEnabled(false);
            buttonBT.setEnabled(false);

            new BlueReadTask().executeOnExecutor(Executors.newCachedThreadPool());
        } else {
            Toast.makeText(this, "请确认蓝牙设备已经连接，再读卡!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                resetUI();
                break;
            case R.id.action_server:
                Intent intents = new Intent();
                intents.setClass(MobileReaderActivity.this, ActServerConfig.class);
                startActivityForResult(intents, 2);
                bSelServer = true;
                break;
            case R.id.action_about:
                Intent intent = new Intent();
                intent.setClass(MobileReaderActivity.this, ActAbout.class);
                this.startActivity(intent);
                bSelServer = true;
                break;
            case R.id.action_blue:
                Intent serverIntent = new Intent(MobileReaderActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void resetUI() {
        this.nameTextView.setText("");
        this.sexTextView.setText("");
        this.folkTextView.setText("");
        this.birthTextView.setText("");
        this.codeTextView.setText("");
        this.policyTextView.setText("");
        this.addrTextView.setText("");
        this.validDateTextView.setText("");
        this.tv_info.setText("");
        //dnTextView.setText("");
        //samidTextView.setText("");

        // this.errorLogTextView.setText("");
        this.photoView.setImageResource(android.R.color.transparent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    class MyHandler extends Handler {
        private MobileReaderActivity activity;

        MyHandler(MobileReaderActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ConsantHelper.READ_CARD_SUCCESS:
                    buttonOTG.setEnabled(true);
                    buttonNFC.setEnabled(true);
                    buttonBT.setEnabled(true);
                    ButtondefDrawable();
                    break;

                case ConsantHelper.SERVER_CANNOT_CONNECT:
                    activity.tv_info.setText("服务器连接失败! 请检查网络。");
                    buttonOTG.setEnabled(true);
                    buttonNFC.setEnabled(true);
                    buttonBT.setEnabled(true);
                    ButtondefDrawable();
                    break;

                case ConsantHelper.READ_CARD_FAILED:
                    activity.tv_info.setText("无法读取信息请重试!");
                    buttonOTG.setEnabled(true);
                    buttonNFC.setEnabled(true);
                    buttonBT.setEnabled(true);
                    ButtondefDrawable();
                    break;

                case ConsantHelper.READ_CARD_WARNING:
                    String str = (String) msg.obj;

                    if (str.indexOf("card") > -1) {
                        activity.tv_info.setText("读卡失败: 卡片丢失,或读取错误!");
                    } else {
                        String[] datas = str.split(":");

                        activity.tv_info.setText("网络超时 错误码: " + Integer.toHexString(new Integer(datas[1])));
                    }
                    //activity.tv_info.setText("请移动卡片在合适位置!");
                    buttonOTG.setEnabled(true);
                    buttonNFC.setEnabled(true);
                    buttonBT.setEnabled(true);
                    ButtondefDrawable();
                    break;

                case ConsantHelper.READ_CARD_PROGRESS:

                    int progress_value = (Integer) msg.obj;
                    //Log.e("main", String.format("progress_value = %d", progress_value));
                    activity.tv_info.setText("正在读卡......,进度：" + progress_value + "%");
                    break;

                case ConsantHelper.READ_CARD_START:
                    activity.resetUI();
                    activity.tv_info.setText("开始读卡......");
                    break;
                case Error.ERR_CONNECT_SUCCESS:
                    String devname = (String) msg.obj;
                    activity.tv_info.setText(devname + "连接成功!");
                    //mtv_info1.setText("成功:" + String.format("%d", totalcount) + " 失败:" + String.format("%d", failecount));
                    break;
                case Error.ERR_CONNECT_FAILD:
                    String devname1 = (String) msg.obj;
                    activity.tv_info.setText(devname1 + "连接失败!");
                    //mtv_info1.setText("成功:" + String.format("%d", totalcount) + " 失败:" + String.format("%d", failecount));
                    break;
                case Error.ERR_CLOSE_SUCCESS:
                    activity.tv_info.setText((String) msg.obj + "断开连接成功");
                    break;
                case Error.ERR_CLOSE_FAILD:
                    activity.tv_info.setText((String) msg.obj + "断开连接失败");
                    break;
                case Error.RC_SUCCESS:
                    String devname12 = (String) msg.obj;
                    activity.tv_info.setText(devname12 + "连接成功!");
                    break;

            }
        }

    }

    private void readCardFailed(String strcardinfo) {
        int bret = Integer.parseInt(strcardinfo);
        switch (bret) {
            case -1:
                tv_info.setText("服务器连接失败!");
                break;
            case 1:
                tv_info.setText("读卡失败!");
                break;
            case 2:
                tv_info.setText("读卡失败!");
                break;
            case 3:
                tv_info.setText("网络超时!");
                break;
            case 4:
                tv_info.setText("读卡失败!");
                break;
            case -2:
                tv_info.setText("读卡失败!");
                break;
            case 5:
                tv_info.setText("照片解码失败!");
                break;
        }
    }

    private void readCardSuccess(IdentityCardZ identityCard) {

        if (identityCard != null) {
            nameTextView.setText(identityCard.name);
            sexTextView.setText(identityCard.sex);
            folkTextView.setText(identityCard.ethnicity);

            String szTmp = identityCard.birth;
            String year = szTmp.substring(0, 4);
            String month = szTmp.substring(7, 9);
            String day = szTmp.substring(12, 14);

//            birthTextView.setText(identityCard.birth);
            birthTextView.setText(year);
            monthTextView.setText(month);
            dayTextView.setText(day);

            codeTextView.setText(identityCard.cardNo);
            policyTextView.setText(identityCard.authority);
            addrTextView.setText(identityCard.address);
            //dnTextView.setText(identityCard.dn);
            //samidTextView.setText(identityCard.SAMID);
            validDateTextView.setText(identityCard.period);

            name = identityCard.name;
            address = identityCard.address;
            cardnum = identityCard.cardNo;
        }
        tv_info.setText("读取成功!");
        Log.e(ConsantHelper.STAGE_LOG, "读卡成功!");
//		totalcount++;
//		mtv_info1.setText("成功:" + String.format("%d", totalcount) + " 失败:" + String.format("%d", failecount));

    }


    private String filePath1,image1Id;

}