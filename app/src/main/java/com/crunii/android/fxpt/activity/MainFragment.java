package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.util.AsynImageLoaderWithSDCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

abstract class MainFragment extends Fragment {
    boolean needRefresh = true;

    //TODO
    private AsynImageLoaderWithSDCache imageLoader;
     private List list;

    abstract String getCategory();

    abstract String getTitle();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO
        imageLoader = new AsynImageLoaderWithSDCache(CRApplication.getApp().getHttpClient(), getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_main, container, false);
        ((TextView) view.findViewById(R.id.packagetitle)).setText(getTitle());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
        View kdbl = getView().findViewById(R.id.kdbl);
        kdbl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "宽带报录", Toast.LENGTH_SHORT).show();
			}
		});
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //切换用户刷新登陆名
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refresh() {

        new BaseTask<String, String, JSONArray>(getActivity(), "请稍后...") {

            @Override
            protected JSONArray doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().mode(getActivity(), getCategory());
            }

            @Override
            protected void onSuccess(JSONArray result) {
				list=new ArrayList();
                for (int i = 0; i < result.length(); i++) {
						JSONObject json=result.optJSONObject(i);
						int id=json.optInt("id");
						if (id==14){
							continue;
						}else {
							list.add(json);
						}

                }
				showData();
            }

        }.execute("");



    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            if (needRefresh) {
                refresh();
            }
        }
    }
	
	private void showData() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout content_view = (LinearLayout) this.getView().findViewById(R.id.content_view);
		content_view.removeAllViews();
		
		int length = list.size();
		int lines=0;
		if(length%3!=0){
			 lines = length/3 + 1;
		}else{
			 lines = length/3;
		}

		for(int i=0; i<lines; i++) {
			View view = inflater.inflate(R.layout.mode_line, null);
			ImageView image1 = (ImageView) view.findViewById(R.id.image1);
			ImageView image2 = (ImageView) view.findViewById(R.id.image2);
			ImageView image3 = (ImageView) view.findViewById(R.id.image3);
			TextView  tabname1= (TextView) view.findViewById(R.id.text_tabName);
			TextView  tabdesc1= (TextView) view.findViewById(R.id.text_tabDesc);
			TextView  tabname2= (TextView) view.findViewById(R.id.text_tabName2);
			TextView  tabdesc2= (TextView) view.findViewById(R.id.text_tabDesc2);
			TextView  tabname3= (TextView) view.findViewById(R.id.text_tabName3);
			TextView  tabdesc3= (TextView) view.findViewById(R.id.text_tabDesc3);
			LinearLayout layout= (LinearLayout) view.findViewById(R.id.layout_tab2);
			LinearLayout layout2= (LinearLayout) view.findViewById(R.id.layout_tab3);
			int position1 = i*3;
			JSONObject json1 = (JSONObject) list.get(position1);
			image1.setTag(position1);
			image1.setOnClickListener(new ImageOnClickListener());
			imageLoader.showImageAsyn(new SoftReference<ImageView>(image1), json1.optString("image"), R.drawable.mode_loading);
			tabname1.setText(json1.optString("name").toString());
            tabdesc1.setText(json1.optString("desc").toString());

			if(i!=lines-1){
				int position2 = i*3+1;
				JSONObject json2 = (JSONObject) list.get(position2);
				image2.setTag(position2);
				image2.setOnClickListener(new ImageOnClickListener());
				imageLoader.showImageAsyn(new SoftReference<ImageView>(image2), json2.optString("image"), R.drawable.mode_loading);
				tabname2.setText(json2.optString("name").toString());
				tabdesc2.setText(json2.optString("desc").toString());
				int position3 = i*3+2;
				JSONObject json3 = (JSONObject) list.get(position3);
				image3.setTag(position3);
				image3.setOnClickListener(new ImageOnClickListener());
				tabname3.setText(json3.optString("name").toString());
				tabdesc3.setText(json3.optString("desc").toString());
				imageLoader.showImageAsyn(new SoftReference<ImageView>(image3), json3.optString("image"), R.drawable.mode_loading);
			}else{//最后一行

				int size=length%3;
				if (size==1){
					layout.setVisibility(View.INVISIBLE);
					layout2.setVisibility(View.INVISIBLE);
				}
				if(size==2){
					int position=i*3+1;
					JSONObject json2 = (JSONObject) list.get(position);
					image2.setTag(position);
					image2.setOnClickListener(new ImageOnClickListener());
					imageLoader.showImageAsyn(new SoftReference<ImageView>(image2), json2.optString("image"), R.drawable.mode_loading);
					tabname2.setText(json2.optString("name").toString());
					tabdesc2.setText(json2.optString("desc").toString());

					layout2.setVisibility(View.INVISIBLE);

				}

				if(size==0){

					int position=i*3+1;
					JSONObject json2 = (JSONObject) list.get(position);
					image2.setTag(position);
					image2.setOnClickListener(new ImageOnClickListener());
					imageLoader.showImageAsyn(new SoftReference<ImageView>(image2), json2.optString("image"), R.drawable.mode_loading);
					tabname2.setText(json2.optString("name").toString());
					tabdesc2.setText(json2.optString("desc").toString());

					int position3 = i*3+2;
					JSONObject json3 = (JSONObject) list.get(position3);
					image3.setTag(position3);
					image3.setOnClickListener(new ImageOnClickListener());
					tabname3.setText(json3.optString("name").toString());
					tabdesc3.setText(json3.optString("desc").toString());
					imageLoader.showImageAsyn(new SoftReference<ImageView>(image3), json3.optString("image"), R.drawable.mode_loading);

				}

			}
			content_view.addView(view);
		}
		
	}
	
	private class ImageOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			JSONObject json = (JSONObject) list.get(position);
			Intent i = new Intent(getActivity(), SpeedPackageActivity.class);
			i.putExtra("category", getCategory());
			i.putExtra("modeId", json.optString("id"));
			i.putExtra("modeName", json.optString("name"));
			startActivity(i);
		}
	}
}
