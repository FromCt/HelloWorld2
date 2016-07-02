package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class TabFFragment extends Fragment {
    boolean needRefresh = true;
    private JSONArray list;
    int modeId=14;
    String modeName;
    LayoutInflater inflater;
    LinearLayout content_view;
    String category = "card";



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_speedpackage1, container, false);
       return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        TextView modeNameTv = (TextView) getActivity().findViewById(R.id.modeName);
        modeNameTv.setText("号卡批发");
        title.setText(CRApplication.getName(getActivity()));

    }

  /*  public void refresh() {
        new BaseTask<String, String, JSONArray>(getActivity(), "请稍后...") {

            @Override
            protected JSONArray doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().mode(getActivity(), category);
            }

            @Override
            protected void onSuccess(JSONArray result) {
                needRefresh = false;
                list = result;
                Log.d("33333333", list.toString());
                showData();

            }

        }.execute("");

    }

    private void showData() {
        JSONObject json = list.optJSONObject(2);
        try {
            modeId = json.getString("id");
            Log.i("modeid", modeId);
            modeName = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView modeNameTv = (TextView) getActivity().findViewById(R.id.modeName);
        modeNameTv.setText(modeName);

        content_view = (LinearLayout) getActivity().findViewById(R.id.content_view);
        refreshData();
    }*/

    private void refreshData() {
        new BaseTask<String, String, JSONArray>(getActivity(), "请稍后...") {

            @Override
            protected JSONArray doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().speedpackage(getActivity(), String.valueOf(modeId));
            }
            @Override
            protected void onSuccess(JSONArray result) {
                needRefresh=false;
                list = result;
                Log.i("6666666666666",list.toString());
                showcontentData();
            }
        }.execute("");
    }

    private void showcontentData() {
        for (int i = 0; i < list.length(); i++) {
            int iconResId = getSpeedPackageIconId(i);
            int colorBackground;  //背景颜色
            int colorText;  //文字颜色
            colorBackground = 0xFFFFFFFF;
            colorText = 0xFF000000;
            JSONObject json = list.optJSONObject(i);
            String speedId = json.optString("speedId");
            String speedName = json.optString("speedName");
            View view = inflater.inflate(R.layout.speed_line, null);
            TextView speedtitle = (TextView) view.findViewById(R.id.speedtitle);
            speedtitle.setText(speedName);
            showPackage(speedId, speedName, (LinearLayout) view.findViewById(R.id.packageview), json.optJSONArray("package"), colorBackground, colorText, iconResId);
            content_view.addView(view);
        }
    }

    private void showPackage(String speedId, String speedName, LinearLayout packageview, JSONArray packagelist,
                             int colorBackground, int colorText,int iconResId) {
        int length = packagelist.length();
        int lines = length / 2 + length % 2;
        for (int i = 0; i < lines; i++) {
            View view = inflater.inflate(R.layout.speedpackage_line, null);

            ((ImageView) view.findViewById(R.id.speedpackage_icon1)).setImageResource(iconResId);
            ((ImageView) view.findViewById(R.id.speedpackage_icon2)).setImageResource(iconResId);

            View package1 = view.findViewById(R.id.package1);
            View package2 = view.findViewById(R.id.package2);
            package1.setBackgroundColor(colorBackground);
            package2.setBackgroundColor(colorBackground);
            TextView package1name = (TextView) view.findViewById(R.id.package1name);
            TextView package2name = (TextView) view.findViewById(R.id.package2name);
            package1name.setTextColor(colorText);
            package2name.setTextColor(colorText);

            int position1 = i * 2;
            JSONObject json1 = packagelist.optJSONObject(position1);
            package1.setTag(new PackageInfo(speedId, speedName, json1.optString("id"), json1.optString("name")));
            package1.setOnClickListener(new PackageOnClickListener());
            package1name.setText(json1.optString("name"));

            if (length % 2 != 0 && (i + 1 == lines)) { //最后一行，单数
                package2.setVisibility(View.INVISIBLE);
            } else {
                int position2 = i * 2 + 1;
                JSONObject json2 = packagelist.optJSONObject(position2);
                package2.setTag(new PackageInfo(speedId, speedName, json2.optString("id"), json2.optString("name")));
                package2.setOnClickListener(new PackageOnClickListener());
                package2name.setText(json2.optString("name"));
            }

            packageview.addView(view);
        }
    }
    private class PackageOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            PackageInfo info = (PackageInfo) v.getTag();
            Intent i = new Intent(getActivity(), ProductListActivity.class);
            i.putExtra("category", category);
            i.putExtra("mode", String.valueOf(modeId));
            i.putExtra("speed", info.getSpeedId());
            i.putExtra("packageId", info.getPackageId());
            i.putExtra("packageName", info.getPackageName());
            startActivity(i);
        }
    }
    private int getSpeedPackageIconId(int line) {
        int iconResId =0;
        switch (line) {
            case 0:
                iconResId = R.drawable.speedpackage_zd01;
                break;
            case 1:
                iconResId = R.drawable.speedpackage_zd02;
                break;
            case 2:
                iconResId = R.drawable.speedpackage_zd03;
                break;
            case 3:
                iconResId = R.drawable.speedpackage_zd04;
                break;
            case 4:
                iconResId = R.drawable.speedpackage_zd05;
                break;
            case 5:
                iconResId = R.drawable.speedpackage_zd06;
                break;
            default:
                break;
        }
    return iconResId;
}
    public class PackageInfo {
        String speedId;
        String speedName;
        String packageId;
        String packageName;

        public PackageInfo(String speedId, String speedName, String packageId, String packageName) {
            this.speedId = speedId;
            this.speedName = speedName;
            this.packageId = packageId;
            this.packageName = packageName;
        }


        public String getSpeedId() {
            return speedId;
        }


        public void setSpeedId(String speedId) {
            this.speedId = speedId;
        }


        public String getSpeedName() {
            return speedName;
        }


        public void setSpeedName(String speedName) {
            this.speedName = speedName;
        }


        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
    }

  public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            if (needRefresh) {
                //refresh();
                content_view = (LinearLayout) getActivity().findViewById(R.id.content_view);
                refreshData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //切换用户刷新登陆名
        TextView title = (TextView) this.getView().findViewById(R.id.head_tv_user);
        title.setText(CRApplication.getName(getActivity()));
    }


}