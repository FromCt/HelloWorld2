package com.crunii.android.fxpt.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.ActivityExOperUtil;
import com.crunii.android.fxpt.base.BaseListViewAdapter;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.combinationGoodsActivity.CombinMain;
import com.crunii.android.fxpt.util.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class TabAFragment extends Fragment {

    private Activity mActivity;
    private String category = "";
    private String title = "";


    public TabAFragment(Activity mActivity){
	  this.mActivity = mActivity;
    }

    public TabAFragment(Activity mActivity, String category, String title){
	  this.mActivity = mActivity;
	  this.category = category;
	  this.title = title;
    }
     BaseListViewAdapter blva ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	  View view  =  inflater.inflate(R.layout.activity_combin_tablist, container, false);
	  ((TextView)view.findViewById(R.id.packagetitle)).setText(title);
	   GridView gridView = (GridView) view.findViewById(R.id.combin_gridview);
	  blva = new BaseListViewAdapter<Map>(mActivity, R.layout.activity_combin_grid_picture_item) {
		@Override
		public void getBaseView(final Map data, HolderView holderView, ViewGroup parent,int position) {
		    final ImageView img = (ImageView) holderView.findViewById(R.id.image);
			 TextView tabname= (TextView) holderView.findViewById(R.id.text_tabName);
			 TextView tabdesc= (TextView) holderView.findViewById(R.id.text_tabDesc);
		    String url = (String) data.get("imageUrl");
			String name= (String) data.get("tabName");
			String desc= (String) data.get("tabDesc");
		    ActivityExOperUtil.getImg(mActivity, url, img);
			tabname.setText(name);
			tabdesc.setText(desc);
		    img.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				String tabId = (String) data.get("tabId");
				final HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("tabId", tabId);

				HttpTool.sendPost(mActivity, Constant.CTX_PATH + "cbg_tabList_right", params, new HttpPostProp() {
					@Override
					public void dealRecord(Map record) {
						boolean isSuccess = (boolean) record.get("isSuccess");
						if (isSuccess) {
							Intent intent = new Intent(mActivity, CombinMain.class);
							intent.putExtra("params", params);
							startActivity(intent);
						} else {
							String failDesc = (String) record.get("failDesc");
							new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage(failDesc).setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
//						Toast.makeText(mActivity,failDesc,Toast.LENGTH_LONG).show();
						}
					}
				});
			  }
		    });
		}
	  };
	  gridView.setAdapter(blva);

	  return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
    }

    public void refresh(){

	  Map<String, String> map = new HashMap<String, String>();
	  map.put("category", category);
	  HttpTool.sendPost(mActivity, Constant.CTX_PATH + "cbg_tabList", map, new HttpPostProp() {
		  @Override
		  public void dealRecord(Map record) {
			  Map result = (Map) record;
			  List<Map> tabList = (List) result.get("tabList");
			  blva.refreshData(tabList);
		  }
	  });
    }


    @Override
    public void onResume() {
	  super.onResume();

	  //切换用户刷新登陆名
	  TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
	  title.setText(CRApplication.getName(getActivity()));
    }


}
