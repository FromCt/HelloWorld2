package com.crunii.android.fxpt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PersonalInfoActivity extends FragmentActivity {
	public ViewPager viewPager;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	FragmentManager fragmentManager;
	FragmentPagerAdapter fragmentPagerAdapter;
	
	final static int TAB_A_EDIT_ADDRESS_REQUEST = 0;
	final static int TAB_C_KEYWORD_REQUEST = 1;
	final static int TAB_C_EDIT_PARTNER_REQUEST = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        CRApplication.getInstance().addActivity(this);//为了退出应用程序
		setContentView(R.layout.activity_personalinfo);

		fragments.add(new PersonalInfoTabAFragment());
		fragments.add(new PersonalInfoTabBFragment());
		fragments.add(new PersonalInfoTabCFragment());

		fragmentManager = this.getSupportFragmentManager();
		fragmentPagerAdapter = new MyPagerAdapter(fragmentManager);

		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(fragmentPagerAdapter);
		viewPager.setCurrentItem(0); //默认为第一个tab
		((RadioButton) findViewById(R.id.radio1)).setChecked(true); //默认选中第一个tab
	}

	public void doBack(View v) {
		onBackPressed();
	}
	
	public void doRadio1(View v) {
		viewPager.setCurrentItem(0);
	}

	public void doRadio2(View v) {
		viewPager.setCurrentItem(1);
	}

	public void doRadio3(View v) {
		viewPager.setCurrentItem(2);
	}
	
	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (requestCode == TAB_A_EDIT_ADDRESS_REQUEST) {
			if(resultCode == RESULT_OK) {
				//刷新地址列表
				((PersonalInfoTabAFragment)fragments.get(0)).refresh();
			}
		} else if (requestCode == TAB_C_KEYWORD_REQUEST) {
			if(resultCode == RESULT_OK) {
				//刷新伙伴列表
				((PersonalInfoTabCFragment)fragments.get(2)).setKeyword(intent.getExtras().getString("keyword"));
				((PersonalInfoTabCFragment)fragments.get(2)).getPartnerList();
			}
		} else if (requestCode == TAB_C_EDIT_PARTNER_REQUEST) {
			if(resultCode == RESULT_OK) {
				//刷新伙伴列表
				((PersonalInfoTabCFragment)fragments.get(2)).getPartnerList();
			}
		} 

	}
	
	@Override
	public void onBackPressed() {
		if(viewPager.getCurrentItem() == 2) { //分销伙伴
			if(!((PersonalInfoTabCFragment)fragments.get(2)).getKeyword().equals("")) {
				((PersonalInfoTabCFragment)fragments.get(2)).setKeyword("");
				((PersonalInfoTabCFragment)fragments.get(2)).getPartnerList();
			} else {
				finish();
			}
		} else {
			finish();
		}
	}

    public void doLogout(View v) {
        if(CRApplication.getToken(this) != null) {
            final String[] items = { "退出当前账号", "关闭分销平台客户端" };
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setItems(items,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            logout();
                            break;
                        case 1:
                            exit();
                            break;
                    }
                }
            })
            .setNegativeButton("取消", null)
            .show();
        }

    }

    private void exit(){
        CRApplication.getInstance().exit();
    }
    private void logout() {

        new BaseTask<String, String, Boolean>(this, "正在注销...") {

            @Override
            protected Boolean doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().logout(getApplicationContext());
            }

            @Override
            protected void onSuccess(Boolean arg0) {
				Log.i("ct", "logout onSuccess");
			}

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                CRApplication.setToken(getApplicationContext(), "123");//token为空时候，在mainActivity中会到welcomeViewActivity
                CRApplication.setId(getApplicationContext(), null);
                CRApplication.setName(getApplicationContext(), null);
                CRApplication.setUnread(0);
				Log.i("ct", "logout onPostExecute");
                setResult(RESULT_OK);
				Intent intent = new Intent(PersonalInfoActivity.this, LoginActivity.class);
				startActivity(intent);

                finish();
            }

        }.execute("");

    }

}
