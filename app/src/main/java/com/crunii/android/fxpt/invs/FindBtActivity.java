package com.crunii.android.fxpt.invs;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crunii.android.fxpt.R;
import com.invs.BtReaderClient;
import com.invs.DeviceInfo;
import com.invs.InvsConst;

import java.util.ArrayList;
import java.util.Collections;


public class FindBtActivity extends Activity implements OnClickListener
{
	private BtReaderClient mClient = null;//蓝牙类
	private boolean mBle = false;//蓝牙4.0支持变量
    private boolean mScanning=false;//是否正在扫描

	private ListView listView;
	MyAdapter adapter = new MyAdapter();
	private ArrayList<DeviceInfo> mDeviceInfoList = new ArrayList<DeviceInfo>();
    
	public void onClick(View v) {
		Button b;
		switch(v.getId()){
		case R.id.button1:
	        b = (Button) findViewById(R.id.button1);
	        b.setEnabled(false);
	        b = (Button) findViewById(R.id.button2);
	        b.setEnabled(true);
			setProgressBarIndeterminateVisibility(true);
			scan(true);
			break;
		case R.id.button2:
	        b = (Button) findViewById(R.id.button1);
	        b.setEnabled(true);
	        b = (Button) findViewById(R.id.button2);
	        b.setEnabled(false);
			setProgressBarIndeterminateVisibility(false);
			scan(false);
			break;
		}
	}
	
    void scan(boolean enable){
    	if (enable){
    		mDeviceInfoList.clear();
    		listView.setAdapter(new MyAdapter());  
    		
        	RadioButton r = (RadioButton)findViewById(R.id.radioBtn1);
        	r.setEnabled(false);  
        	r = (RadioButton)findViewById(R.id.radioBtn2);
        	r.setEnabled(false);
        	if (r.isChecked()){
        		mBle = true;
        	}else{
        		mBle = false;
        	}
    	}else{
    		//stopTimer();
        	RadioButton r = (RadioButton)findViewById(R.id.radioBtn1);
        	r.setEnabled(true);  
        	r = (RadioButton)findViewById(R.id.radioBtn2);
        	r.setEnabled(true);    		
    	}
    	mScanning = enable;
    	mClient.scanDevice(mBle, enable);
    }
    
    
	void SetSelDev(){
		SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
		String szAddress = sp.getString("Address", "");
		String szName = sp.getString("Name", "");
		boolean ble = sp.getBoolean("bleAddress", true); 
		
		TextView v = (TextView)findViewById(R.id.tv1);
		
		if (szAddress == null || szAddress == "") {		
			v.setText("no dev");
		}else{
			if (ble){
				v.setText("last 4.0 dev is:"+szName+"\n                          "+szAddress);
			}else{
				v.setText("last 2.0 dev is:"+szName+"\n                          "+szAddress);
			}
		}		
	}
    
    RadioGroup raGroup1;
    void initRadioBtn(){
    	raGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
        raGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
              
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
        		mDeviceInfoList.clear();
        		listView.setAdapter(new MyAdapter());  
                if(checkedId==R.id.radioBtn1){                  	
                	mBle = false;		        	
                } else if(checkedId==R.id.radioBtn2){ 
                	mBle = true;             	
                }
            }  
        });
    }
    
    void checkBltMod(){
    	if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
        	RadioButton r = (RadioButton)findViewById(R.id.radioBtn1);
        	r.setEnabled(false);
        	r.setChecked(true);
        	r = (RadioButton)findViewById(R.id.radioBtn2);
        	r.setEnabled(false);
    	}else{
    		SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
    		boolean ble = sp.getBoolean("bleAddress", true);    	
	    	if (ble){
	    		RadioButton r = (RadioButton)findViewById(R.id.radioBtn2);
	    		r.setChecked(true);
	    	}else{
	    		RadioButton r = (RadioButton)findViewById(R.id.radioBtn1);
	    		r.setChecked(true);	  
	    	}
    	}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.findbt);
       
		mClient = new BtReaderClient(this);
		
		checkBltMod();
		
		SetSelDev();
		
		listView = (ListView) this.findViewById(R.id.list);
		listView.setAdapter(adapter);
		
			
		regRecv();
		
	
		initRadioBtn();
		
        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(this);		
        
        b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(this);	
        b.setEnabled(false);
	}
	
	private class MyAdapter extends BaseAdapter {
		private int temp = -1;

		public int getCount() {
		// TODO Auto-generated method stub
			return mDeviceInfoList.size();
		}
	
		public Object getItem(int position) {
			return mDeviceInfoList.get(position);
		}
	
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			try{
			convertView = FindBtActivity.this.getLayoutInflater().inflate(R.layout.item, null); //����취�� ÿ�ζ����»�ȡView
			TextView tv = (TextView) convertView.findViewById(R.id.tv);
			
			DeviceInfo info = mDeviceInfoList.get(position); //�������ݣ�ģ�ͣ�
			if (info != null) {  
				tv.setText(info.name + "\n" + info.address);
				RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
				
				radioButton.setId(position); 			
				radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						try{
						if(isChecked){
							if(temp != -1){
								RadioButton tempButton = (RadioButton) FindBtActivity.this.findViewById(temp);
								if(tempButton != null){
									tempButton.setChecked(false);
								}						
							}
								
							temp = buttonView.getId();
							
							DeviceInfo info = mDeviceInfoList.get(temp); //�������ݣ�ģ�ͣ�
					    	SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
				        	SharedPreferences.Editor edit = sp.edit();
				        	edit.putString("Name", info.name);
				        	edit.putString("Address", info.address);
				        	edit.putBoolean("bleAddress", mBle);
				        	edit.commit();						
						}else{
							
						}
						} catch (Exception e) {
						}
					}
				});
				
	    		SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
	    		String szAddress = sp.getString("Address", "");
	    		
				if (info.address.equalsIgnoreCase(szAddress) && !radioButton.isChecked()){
					radioButton.setChecked(true);
				}
	
				if(temp == position){
					radioButton.setChecked(true);
				}
			}
			} catch (Exception e) {
				return null;
			}
			return convertView;
		}
	}
	
	void regRecv()
	{
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InvsConst.msg);
		registerReceiver(mReceiver, intentFilter);
	}
	
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
		try{
            if (!InvsConst.msg.equals(intent.getAction())) {
            	return;          	
            }
            
           	DeviceInfo dev = (DeviceInfo) intent.getSerializableExtra("DeviceInfo");
        	           
           	if (dev.name == null || dev.name == ""){
           		return;
           	}
           	
    		if (dev.name.indexOf("INVS300-") != 0 && dev.name.indexOf("CDINVS300-") != 0){ 
    			return;    			
    		}
    		
      		for (int i=0; i<mDeviceInfoList.size(); i++){  
    			if (dev.address.equalsIgnoreCase(mDeviceInfoList.get(i).address)){
    				return;
    			}
    		}   		
    		mDeviceInfoList.add(dev);
    		Collections.sort(mDeviceInfoList, new DeviceInfo.ComparatorValues());
    		listView.setAdapter(new MyAdapter());     
        } catch (Exception e) {
		}
        }
    };	
    
    @Override
    protected void onDestroy() {
    	scan(false);
    	super.onDestroy();
    }    
}
