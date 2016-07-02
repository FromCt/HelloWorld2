package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.PersonChoose;
import com.crunii.android.fxpt.util.MyLog;
import com.crunii.android.fxpt.util.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ct on 2016/5/19.
 * 201605 19 添加登录后选择权限功能
 */
public class LoginChooseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_choose);

        initComponent();

        Intent intent=getIntent();
        ArrayList<PersonChoose> list = intent.getParcelableArrayListExtra("list");
        if (list!=null){//结收到选择权限传递的值。
           // MyToast.show("list.size="+list.size(),LoginChooseActivity.this);
            initData(list);
        }else {
            MyLog.i("ct", "LoinChooseActivity list=null");
        }



    }

    private ArrayList<PersonChoose> list = new ArrayList<>();
    public void initData(ArrayList<PersonChoose> list){
        this.list=list;
        listView.setAdapter(new MyListViewAdapter() {
        });
    }


    private ListView listView;
    public void initComponent() {

        listView= (ListView) findViewById(R.id.lc_listView);
        View headView = View.inflate(this, R.layout.login_choose_headview,null);
        View footView = View.inflate(this, R.layout.login_choose_footview,null);

        listView.addHeaderView(headView);
        listView.addFooterView(footView);
        listView.setDividerHeight(0);


    }


    class MyListViewAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Holder holder;

            if(convertView==null) {
                holder=new Holder();
                convertView = View.inflate(LoginChooseActivity.this, R.layout.login_choose_listview_item, null);
                holder.name= (TextView) convertView.findViewById(R.id.lcli_name);
                holder.crm= (TextView) convertView.findViewById(R.id.lcli_crm);
                holder.attribute= (TextView) convertView.findViewById(R.id.lcli_attribute);
                holder.branch= (TextView) convertView.findViewById(R.id.lcli_branch);
                holder.category= (TextView) convertView.findViewById(R.id.lcli_category);
                holder.chooseButton= (Button) convertView.findViewById(R.id.lcli_choose);
                holder.point= (TextView) convertView.findViewById(R.id.lcli_sale_point);
                holder.master= (TextView) convertView.findViewById(R.id.lcli_master);
                convertView.setTag(holder);

            }else{
                holder= (Holder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getName());
            holder.crm.setText(list.get(position).getCrm());
            holder.attribute.setText(list.get(position).getAttribute());
            holder.branch.setText(list.get(position).getBranch());
            holder.point.setText(list.get(position).getSale_point());
            holder.master.setText(list.get(position).getMaster());
            holder.category.setText(list.get(position).getCategory());


            holder.chooseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new BaseTask<String, String, JSONObject>(LoginChooseActivity.this, "选择中...") {


                        @Override
                        protected JSONObject doInBack(String... arg0) throws HttpException, IOException, TaskResultException {
                            return CRApplication.getApp().loginChoose(getApplicationContext(), arg0[0]);
                        }

                        @Override
                        protected void onSuccess(JSONObject jsonObject) {

                            MyLog.i("ct", "LoginChooseActivity jsonObject=" + jsonObject.toString());
                            try {
                                boolean b=jsonObject.getBoolean("success");

                                if(b){
                                    JSONObject record = jsonObject.getJSONObject("record");
                                    CRApplication.setToken(getApplicationContext(), record.getString("token"));
                                    CRApplication.setId(getApplicationContext(), record.getString("userId"));
                                    CRApplication.setName(getApplicationContext(), record.getString("userName"));
                                    //CRApplication.setPhone(this, phone); phone 在选择时候记录

                                    finish();
                                }else {
                                    MyLog.i("ct", "LoginChooseActivity jsonObject  msg=" + jsonObject.getString("msg"));
                                    MyToast.show(jsonObject.getString("msg"), LoginChooseActivity.this);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute(list.get(position).getId());

                }
            });

            return convertView;
        }
    }

    class Holder{
        public TextView name;
        public TextView crm;
        public TextView point;
        public TextView master;
        public TextView branch;//
        public TextView attribute;
        public TextView category;//类别
        public Button chooseButton;

    }


}
