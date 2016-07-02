package com.crunii.android.fxpt.invs;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.crunii.android.fxpt.util.ImageHelper;
import com.invs.BtReaderClient;
import com.invs.IClientCallBack;
import com.invs.InvsConst;
import com.invs.InvsDes3;
import com.invs.InvsIdCard;
import com.invs.InvsUtil;
import com.invs.invswlt;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class InvsMainActivity extends Activity implements OnClickListener, IClientCallBack {

    public final int REQUEST_IMAGE1 = 1;
    static int id[] = {R.id.button1, R.id.button20, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8};
    public EditText medit = null;
    private BtReaderClient mClient = null;
    private int mValue = 1;
    String filePath1 = "", image1Id = "";

    public static final String msg = "invs.blt.readcard";

    @Override
    protected void onDestroy() {
        mClient.disconnectBt();
        super.onDestroy();
        //System.exit(0);
    }

    void initBtn() {
        for (int i = 0; i < id.length; i++) {
            Button b = (Button) findViewById(id[i]);
            b.setOnClickListener(this);
        }
        switchBtn(new int[]{R.id.button1, R.id.button4}, true);
    }

    void initView(String szTxt) {
        TextView v = (TextView) findViewById(R.id.textView1);
        //setTitle();
//		v.setText("姓名 ");
//		v = (TextView)findViewById(R.id.textView2);
//		v.setText("性别  ");
//		v = (TextView)findViewById(R.id.textView3);
//		v.setText("民族  ");
//		v = (TextView)findViewById(R.id.textView4);
//		v.setText("出生 ");
//		v = (TextView)findViewById(R.id.textView5);
//		v.setText("住址 ");
//		v = (TextView)findViewById(R.id.textView6);
//		v.setText("身份证号");
//		v = (TextView)findViewById(R.id.textView7);
//		v.setText("签发机关 ");
//		v = (TextView)findViewById(R.id.textView8);
//		v.setText("有效期限  ");
//		v = (TextView)findViewById(R.id.textView9);
//		v.setText(new String(" "));
//		v = (TextView)findViewById(R.id.textView10);
//		v.setText(new String(" "));
//		v = (TextView)findViewById(R.id.textView11);
//		v.setText(new String(szTxt));
        ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.tmp));
    }

    void displayView(InvsIdCard invsIdCard) {

        cardnum = invsIdCard.idNo;
        name = invsIdCard.name;
        address = invsIdCard.address;

        TextView v = (TextView) findViewById(R.id.textView1);
        v.setText(invsIdCard.name);//姓名

        v = (TextView) findViewById(R.id.textView2);
        v.setText(invsIdCard.sex); //性别
        v = (TextView) findViewById(R.id.textView3);
        v.setText(invsIdCard.nation); //民族

        String szTmp = invsIdCard.birth;
        String year = szTmp.substring(0, 4);
        String month = szTmp.substring(4, 6);
        String day = szTmp.substring(6, 8);


//		szTmp = szTmp.substring(0, 4) + "年"
//			     + szTmp.substring(4, 6) + "月"
//				 + szTmp.substring(6, 8) + "日";
//		v.setText("出生  " + szTmp);
        v = (TextView) findViewById(R.id.textView4);
        v.setText(year);
        v = (TextView) findViewById(R.id.textView14);
        v.setText(month);
        v = (TextView) findViewById(R.id.textView24);
        v.setText(day);
        v = (TextView) findViewById(R.id.textView5);
        v.setText(invsIdCard.address);   //住址
        v = (TextView) findViewById(R.id.textView6);
        v.setText(invsIdCard.idNo);//"身份证号 " +
        v = (TextView) findViewById(R.id.textView7);
        v.setText(invsIdCard.police);//"签发机关  " +
        v = (TextView) findViewById(R.id.textView8);

        szTmp = invsIdCard.start;
        szTmp = szTmp.substring(0, 4) + "." +
                szTmp.substring(4, 6) + "." +
                szTmp.substring(6, 8) + "-";
        if (invsIdCard.end.length() == 8) {
            szTmp = szTmp + invsIdCard.end.substring(0, 4) + "." +
                    invsIdCard.end.substring(4, 6) + "." +
                    invsIdCard.end.substring(6, 8);
        } else {
            szTmp = szTmp + invsIdCard.end;
        }

        v.setText(szTmp);//"有效期限  " +
        v = (TextView) findViewById(R.id.textView11);
        v.setText(new String("读卡成功"));


        byte[] szBmp = invswlt.Wlt2Bmp(invsIdCard.wlt);

        if ((szBmp != null) && (szBmp.length == 38862)) {
            Bitmap bmp = BitmapFactory.decodeByteArray(szBmp, 0, szBmp.length);
            ImageView iv = (ImageView) findViewById(R.id.imageView1);
            iv.setImageBitmap(bmp);
        } else {
            ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.tmp));
        }
    }

    public static String byte2hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(((int) data[i]) & 0xFF);
            for (int t = temp.length(); t < 2; t++) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        byte bb[] = InvsUtil.hexStringToByte("3031");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.invs_activity_main_new);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            finish();
            return;
        }

        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
        }

        initBtn();
        initView("");

        regRecv();

        mClient = new BtReaderClient(this);
        mClient.setCallBack(this);
    }

    private String getAddr() {
        SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
        return sp.getString("Address", "");
    }

    void testMap(Map map) throws Exception {
        Integer i = (Integer) map.get("resultFlag");
        String xml = "";
        int ii = i;
        String mac = (String) map.get("mac");
        if (ii != 0) {
            initView("读卡失败:");
            xml = "mac:" + mac + "\r\n" + "errorMsg:" + map.get("errorMsg");
            new AlertDialog.Builder(this)
                    .setMessage(xml)
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }
        String data = (String) map.get("resultContent");
        InvsDes3 des3 = new InvsDes3();
        String key = "0123456789ABCDEF";

        if (true) {
            byte[] des = null;
            try {
                des = des3.des3DecodeCBC(key.getBytes(), null, Base64.decode(data, Base64.DEFAULT));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            xml = "mac:" + mac + "\r\n" + "errorMsg:" + map.get("errorMsg") + "\r\n" + new String(des, "UTF8");
        } else {
            //String key_str = "303132333435363738394142434445463031323334353637";
            //xml = ThreeDes.decryptMode(key_str, data);
            //xml = "mac:" + mac + "\r\n" + xml;
        }
        //Toast.makeText(this, xml, 10000).show();
        new AlertDialog.Builder(this)
                .setMessage(xml)
                .setPositiveButton("Ok", null)
                .show();

        xml = mClient.readPhoto();

        //Toast.makeText(this, xml, 3000).show();
        xml = "";
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                switchBtn(new int[]{}, true);
                initView("正在连接设备");
                SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);

                if (!mClient.connectBt(getAddr())) {
                    initView("连接失败");
                    switchBtn(new int[]{R.id.button1, R.id.button4}, true);
                } else {
                    ((TextView) findViewById(R.id.textView11)).setText("连接成功！");
                }
                break;
            case R.id.button20: {
                Button b = (Button) findViewById(R.id.button20);
                String name = b.getText().toString();
                if (name.equalsIgnoreCase("循环读卡")) {
                    switchBtn(new int[]{R.id.button20}, true);
                    startThreadReadCard();
                    b.setText("停止读卡");
                    return;
                } else {
                    switchBtn(new int[]{R.id.button1, R.id.button4}, false);
                    stopThreadReadCard();
                    b.setText("循环读卡");
                }
                break;
            }
            case R.id.button2: {
                switchBtn(new int[]{}, true);
                initView("正在读卡");
                long iTick = SystemClock.uptimeMillis();
                if (true) {
                    InvsIdCard card = mClient.readCard();
                    if (card != null) {
                        displayView(card);
                    } else {
                        initView("读卡失败");
                    }
                    iTick = SystemClock.uptimeMillis() - iTick;
                    //Toast.makeText(this, "读卡时间：" + iTick, 2000).show();
                } else {
                    Map map = mClient.readCert();
                    if (map != null) {
                        try {
                            iTick = SystemClock.uptimeMillis() - iTick;
                            //Toast.makeText(this, "读卡时间：" + iTick, 2000).show();
                            initView("读卡成功");
                            testMap(map);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        initView("读卡失败");
                    }
                }
                switchBtn(new int[]{R.id.button1, R.id.button4}, false);
            }
            break;
            case R.id.button3:
                switchBtn(new int[]{}, true);
                mClient.disconnectBt();
                switchBtn(new int[]{R.id.button1, R.id.button4}, true);
                break;
            case R.id.button4:
                Intent intent = new Intent(InvsMainActivity.this, FindBtActivity.class);
                InvsMainActivity.this.startActivity(intent);
                break;
            case R.id.button5:
                switchBtn(new int[]{}, true);
                initView("正在获取电量");
                int bat = mClient.readBat();
                if (bat == -1) {
                    initView("获取电量失败");
                } else {
                    initView("获取电量成功");
                    TextView v1 = (TextView) findViewById(R.id.value);
                    v1.setText("电量" + bat + "%");
                }

                switchBtn(new int[]{R.id.button1, R.id.button4}, false);
                break;

		/*
		case R.id.button5:				
			switchBtn(new int[] {}, true);	 	        
	        initView("正在发送待机唤醒指令");
	        boolean ok = mClient.wakeupCmd();
	        if (ok){
				initView("发送待机唤醒指令成功");   
			}else{
				initView("发送待机唤醒指令失败"); 			
			}
	        
	        switchBtn(new int[] {R.id.button1, R.id.button4}, false);
			break;
		*/
            case R.id.button6:
                switchBtn(new int[]{}, true);
                initView("正在获取密钥");
                byte[] key1 = mClient.getKey();

                if (key1 == null) {
                    initView("获取密钥失败");
                } else {
                    initView("获取密钥成功");
                    TextView v2 = (TextView) findViewById(R.id.value);
                    String s = InvsUtil.bytesToHexString(key1, 0, key1.length);
                    //String s=new String(key1);
                    v2.setText("密钥" + s);
                }
                switchBtn(new int[]{R.id.button1, R.id.button4}, false);
                break;
            case R.id.button7:
                switchBtn(new int[]{}, true);
                initView("正在设置密钥");
                String key = "0123456789ABCDEF";
                if (mClient.setKey(key.getBytes())) {
                    initView("设置密钥成功");
                } else {
                    initView("设置密钥失败");
                }
                switchBtn(new int[]{R.id.button1, R.id.button4}, false);
                break;

            case R.id.button8:


                verifyUserInputIDCard(name, cardnum);

                break;
        }
    }

    String cardnum = "";
    String name = "";
    String address = "";


    private void switchBtn(int btn[], boolean open) {
        if (open) {
            for (int i = 0; i < id.length; i++) {
                Button b = (Button) findViewById(id[i]);
                b.setEnabled(false);
            }
            for (int i = 0; i < btn.length; i++) {
                Button b = (Button) findViewById(btn[i]);
                b.setEnabled(true);
            }
        } else {
            for (int i = 0; i < id.length; i++) {
                Button b = (Button) findViewById(id[i]);
                b.setEnabled(true);
            }
            for (int i = 0; i < btn.length; i++) {
                Button b = (Button) findViewById(btn[i]);
                b.setEnabled(false);
            }
        }
    }

    public void onBtState(final boolean is_connect) {
        //Toast.makeText(this, "connect state is " + is_connect, 2000).show();
        if (is_connect) {
            switchBtn(new int[]{R.id.button1, R.id.button4}, false);
            initView("连接成功");
        } else {
            switchBtn(new int[]{R.id.button1, R.id.button4}, true);
            initView("连接关闭");
            stopThreadReadCard();
        }
        Button b = (Button) findViewById(R.id.button20);
        b.setText("循环读卡");
    }

    public class BaseThread extends Thread {
        public boolean mOver = false;

        public boolean isOver() {
            return (this.interrupted() || mOver);
        }

        public void over() {
            this.interrupted();
            mOver = true;
        }
    }

    public class ReadThread extends BaseThread {
        public InvsIdCard mCard = null;

        protected void sendMsg(int cmd, boolean succ) {
            Intent intent = new Intent();
            intent.setAction(msg);

            intent.putExtra("cmd", cmd);
            intent.putExtra("tag", succ);

            if (succ && cmd == InvsConst.Cmd_ReadCard) {
                intent.putExtra("InvsIdCard", mCard);
            }

            InvsMainActivity.this.sendBroadcast(intent);
        }

        void readCard() {
            try {
                SystemClock.sleep(100);

                int iResult = mClient.findCardCmd();
                if (iResult == -1) {
                    mClient.disconnectBt();
                    over();
                    return;
                } else if (iResult == 0x9f) {
                } else {
                    sendMsg(InvsConst.Cmd_ReadCard, false);
                    return;
                }

                iResult = mClient.readCardCmd();
                if (iResult == -1) {
                    mClient.disconnectBt();
                    over();
                    return;
                } else if (iResult == 0x90) {
                    mCard = mClient.mInvsIdCard;
                    sendMsg(InvsConst.Cmd_ReadCard, true);
                } else {
                    sendMsg(InvsConst.Cmd_ReadCard, false);
                    return;
                }

                while (!isOver()) {
                    SystemClock.sleep(50);
                    iResult = mClient.readAppCmd();
                    if (iResult == 0x90 || iResult == 0x91)
                        continue;

                    if (iResult == -1) {
                        mClient.disconnectBt();
                        over();
                    }

                    break;
                }
            } catch (Exception e) {

            }

        }

        public void run() {
            while (!isOver()) {
                readCard();
            }

            SystemClock.sleep(5);
        }
    }

    ReadThread mReadThread = null;

    public void startThreadReadCard() {
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    public void stopThreadReadCard() {
        if (mReadThread != null && mReadThread.isAlive()) {
            mReadThread.over();
            mReadThread = null;
        }
    }

    void regRecv() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(msg);
        registerReceiver(mBltReceiver, intentFilter);
    }

    InvsIdCard invsIdCard;
    //接收蓝牙传回的消息
    private final BroadcastReceiver mBltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (msg.equals(action)) {
                if (intent.getBooleanExtra("tag", false)) {
                    invsIdCard = (InvsIdCard) intent.getSerializableExtra("InvsIdCard");
                    displayView(invsIdCard);
                } else {
                    initView("读卡失败");
                }
            }
        }
    };


    private void verifyUserInputIDCard(final String nameStr, final String idStr) {

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

                if (!result) {
                    Toast.makeText(InvsMainActivity.this, "身份证校验失败,请正确填写姓名和身份证号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(InvsMainActivity.this, UploadImageVerifyActivity.class);
                i.putExtra("validate", "0");//1是身份证识别 ，0不需要识别，只上传
                i.putExtra("isBL", false);//用于表示是否是报录，报录上传身份证正面不需要输入电话号码  --wyp

                startActivityForResult(i, REQUEST_IMAGE1);


                //finish();
            }

        }.execute("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.setResult(RESULT_CANCELED);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_IMAGE1:
                    filePath1 = data.getExtras().getString("filePath");
                    image1Id = data.getExtras().getString("imageId");
                    Intent i = new Intent();
                    i.putExtra("filePath", filePath1);
                    i.putExtra("imageId", image1Id);
                    i.putExtra("cardnum", cardnum);
                    i.putExtra("name", name);
                    i.putExtra("address", address);
                    InvsMainActivity.this.setResult(RESULT_OK, i);
                    finish();
                    break;

            }
    }

}
