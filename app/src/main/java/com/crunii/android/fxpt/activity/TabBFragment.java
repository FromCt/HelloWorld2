package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import java.util.ArrayList;

/**
 *  网店管理 shopManagement
 *
 */
public class TabBFragment extends Fragment {

	ArrayList<Fragment> list= null;
	public shopManagementFragment1 fragment1;
	public shopManagementFragment2 fragment2;

	public static ListView listView;//商品listView


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	private MainViewPager viewPager;
	private RadioButton radioButton1;
	private RadioButton radioButton2;
	private RelativeLayout seeCodePicture;//查看二维码

	public void initView(View view){

	    viewPager = (MainViewPager) view.findViewById(R.id.fsm_viewPager);

		radioButton1= (RadioButton) view.findViewById(R.id.radioButton1);
		radioButton1.setChecked(true);
		radioButton1.setTextColor(getResources().getColor(R.color.shopManagementRadio));

		radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
		radioButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(0);
				fragment1.loadData();
				listView=fragment1.listView;

				//Log.i("tabB", "viewPager setCurrentItem 0 ");
			}
		});

		radioButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(1);
				fragment2.loadData();
				listView=fragment2.listView;

				//Log.i("tabB", "viewPager setCurrentItem 1 ");
			}
		});

		//查看二维码
		seeCodePicture= (RelativeLayout) view.findViewById(R.id.packageLayout);
		seeCodePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),shopManagementSeeCodeActivity.class);
				startActivity(intent);

			}
		});

		ImageView imageView= (ImageView) view.findViewById(R.id.smv1_image);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("TabBF", "image set listView on item 0");
				if(listView!=null){
					listView.smoothScrollToPosition(0);
				}else{
					Log.i("TabBF", "image set listView on item 0  and listView=null");
				}
			}
		});

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_shop_management, container,false);

		list = new ArrayList<Fragment>();
		initAdapterData(list);
		initView(view);


		if(viewPager!=null){
			viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
				@Override
				public Fragment getItem(int position) {
					return list.get(position);
				}
				@Override
				public int getCount() {
					return list.size();
				}
			});
		}else{
			Log.i("tabB","viewPager is null");
		}


		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("tabB", "onActivityCreated");
		TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
		title.setText(CRApplication.getName(getActivity()));

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("tabB", "onResume");
	}

	@Override
	public void onStart() {//当onStart时 来后去listview 和加载子fragment中的数据
		super.onStart();
		//fragment1.loadData();

	}

	//为viewpager 的list添加数据
	public void initAdapterData(ArrayList<Fragment> list) {

		 fragment1=new shopManagementFragment1();
		 fragment2=new shopManagementFragment2();

		list.add(fragment1);
		list.add(fragment2);
	}

}
