package com.crunii.android.fxpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ct on 15/11/18.
 * 网店管理页签二
 */
public class shopManagementFragment2 extends Fragment {

    private static   final String TYPE1="0";//固定不可修改类型

    public ListView listView;
    public ArrayList<Map> listData = new ArrayList<>();
    public ArrayList<Map> layoutData ;
    public MyListAdapter adapter;

    //获取页签list的数据
    public void initLayoutData() {
        listData.clear();
        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("id", CRApplication.getId(getActivity()));
        postparams.put("phone", CRApplication.getPhone(getActivity()));
        HttpTool.sendPost(getActivity(), Constant.URL.SHOPMANAGEMENTYIXUAN, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                ArrayList<Map> list = (ArrayList<Map>) record.get("menuList");
                listData.addAll(list);
                if (listData != null) {
                    Log.i("smf2", " listData.size========" + listData.size());
                } else {
                    Log.i("smf2", " listData.size========null");
                }

                adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("smf2","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("smf2","onCreateView");
        View view = View.inflate(getActivity(), R.layout.shop_management_fragment2,null);
        listView = (ListView) view.findViewById(R.id.smv2_listView);

        // initLayoutData();
        adapter=new MyListAdapter(listData);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("smf2", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("smf2", "onStart");
        TabBFragment.listView=listView;
        //initLayoutData();
    }

    @Override
    public void onResume() {
        Log.i("smf2", "onResume");
        if (myMap.size()>0){//当有item layout可见时 刷新。设置金额及时刷新
            initLayoutData();
        }
        super.onResume();
    }

    class MyListAdapter extends BaseAdapter {

        public ArrayList<Map> list=new ArrayList<Map>();
        MyListAdapter(ArrayList<Map> list){
            this.list=list;
        }
        @Override
        public int getCount() {
           // Log.i("smf2", "adapter getCount=="+list.size());
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
         //   Log.i("smf2", "adapter getView==" + position);
            final Holder holder;
            if (convertView==null){
              //  Log.i("smf2", "adapter getView  convertView==null");
                holder=new Holder();
                convertView = View.inflate(getActivity(), R.layout.shop_management_view2_listitem, null);
                holder.position=position;
                holder.imageView= (ImageView) convertView.findViewById(R.id.smv_image);
                holder.button1 = (Button) convertView.findViewById(R.id.smv2_packageName);
                holder.textView = (TextView) convertView.findViewById(R.id.smv2_packageNumber);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.smv2_layout);
                convertView.setTag(holder);
            }else{
                holder= (Holder) convertView.getTag();
            }
            holder.button1.setText((String)list.get(position).get("menuName"));
            String chooseValue2 = (String) list.get(position).get("chooseValue2");
            ArrayList<Map> layout_data = (ArrayList<Map>) list.get(position).get("resultList");
            //Log.i("smf2", "shopManagementFragment  layout_data.size()=" + layout_data.size());
            holder.textView.setText("已选数量：" + layout_data.size());

            if(myMap.get(position+"")!=null&&myMap.get(position+"").equals(position+"")){
                holder.layout.setVisibility(View.VISIBLE);
                Log.i("smf2", "setVisibility(View.VISIBLE)"+position);
            }else{
                holder.layout.setVisibility(View.GONE);
            }

            if (holder.layout.getVisibility() == View.VISIBLE) {
                if (holder.viewSize != layout_data.size()) {//判断holder保存的个数是否和子view个数相同。相同则不用子再次创建
                    holder.layout.removeAllViews();
                    for (int i = 0; i < layout_data.size(); i++) {
                        holder.viewSize = layout_data.size();
                        Log.i("smf2","View.inflate(getActivity(), R.layout.shop_management_view2_layoutitem");
                        View view = View.inflate(getActivity(), R.layout.shop_management_view2_layoutitem, null);
                        if(i==0) {
                            LinearLayout line = (LinearLayout) view.findViewById(R.id.smv2_line);
                            line.setVisibility(View.GONE);
                        }
                        setViewData(view, layout_data.get(i),chooseValue2);
                        setItemClick(view, holder, layout_data, i,chooseValue2);
                        holder.layout.addView(view);
                    }
                }else{
                    if(holder.position==position){//查看当前item是否是之前item 是则不做改变

                    }else{
                        Log.i("smf1","从用item出错，重新view");
                        holder.layout.removeAllViews();
                        for (int i = 0; i < layout_data.size(); i++) {
                            holder.viewSize = layout_data.size();
                            View view = View.inflate(getActivity(), R.layout.shop_management_view2_layoutitem, null);
                            if(i==0) {
                                LinearLayout line = (LinearLayout) view.findViewById(R.id.smv2_line);
                                line.setVisibility(View.GONE);
                            }
                            setViewData(view, layout_data.get(i),chooseValue2);
                            setItemClick(view, holder, layout_data, i,chooseValue2);
                            holder.layout.addView(view);
                        }
                    }

                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("smf2", "shopManagementFragment  button onclick");
                    if (holder.layout.getVisibility() == View.VISIBLE) {
                        holder.layout.setVisibility(View.GONE);
                        myMap.remove(position + "");
                        holder.imageView.setImageResource(R.drawable.sm_right);
                    } else {
                        holder.layout.setVisibility(View.VISIBLE);
                        myMap.put(position + "", position + "");
                        holder.imageView.setImageResource(R.drawable.sm_down);
                    }

                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }


    public Map<String ,String> myMap = new HashMap<String ,String>();//记录点击开了的item

    /**
     * //1001  套餐推广最低返利
     */
    private   final  String PACKAGEMONEY="1001";//1001  终端推广返利 套餐推广最低返利
    private final String GUKUAN="1002";         //1002   坐扣返利  套餐推广最低返利
    private final String GPACKGE="1003";        // 1003     终端推广返利
    private final String COMMONLY="1004";       //  1004  套餐推广最低返利
    //设置listView item 中子view的值
    private void setViewData(View view,Map<String,String> layout_data,String chooseValue2) {
        TextView smvi_packageName= (TextView) view.findViewById(R.id.smvi_packageName);
        TextView smvi_packagePrice= (TextView) view.findViewById(R.id.smvi_packagePrice);
        TextView smvi_packageCompany= (TextView) view.findViewById(R.id.smvi_packageCompany);
        smvi_packageName.setText(layout_data.get("packageName"));
        smvi_packagePrice.setText("￥"+layout_data.get("packagePrice"));
        smvi_packageCompany.setText(layout_data.get("supplierName"));

        TextView smvi_price1= (TextView) view.findViewById(R.id.smvi_price1);
        TextView priceName1= (TextView) view.findViewById(R.id.smvi_price1_name);
        TextView smvi_price2= (TextView) view.findViewById(R.id.smvi_price2);
        TextView priceName2= (TextView) view.findViewById(R.id.smvi_price2_name);
        TextView smvi_price3= (TextView) view.findViewById(R.id.smvi_price3);
        TextView priceName3= (TextView) view.findViewById(R.id.smvi_price3_name);
        TextView smvi_price4= (TextView) view.findViewById(R.id.smvi_price4);
        TextView priceName4= (TextView) view.findViewById(R.id.smvi_price4_name);

        //smvi_price3.setText("￥"+layout_data.get("groupReturn"));
        smvi_price3.setVisibility(View.GONE);
        priceName3.setVisibility(View.GONE);


        if(chooseValue2.equals(PACKAGEMONEY)){//
            priceName1.setVisibility(View.VISIBLE);
            priceName2.setVisibility(View.VISIBLE);
            priceName2.setVisibility(View.VISIBLE);
            smvi_price1.setVisibility(View.VISIBLE);
            smvi_price4.setVisibility(View.GONE);
            priceName4.setVisibility(View.GONE);
            //MyLog.i("ct","");
            smvi_price1.setText("￥" + layout_data.get("terminalReturn"));
            smvi_price2.setText(layout_data.get("businessReturn"));

        }

        if(chooseValue2.equals(GUKUAN)){//
            priceName1.setVisibility(View.GONE);
            smvi_price1.setVisibility(View.GONE);
            priceName2.setVisibility(View.VISIBLE);
            smvi_price2.setVisibility(View.VISIBLE);
            smvi_price4.setVisibility(View.VISIBLE);
            priceName4.setVisibility(View.VISIBLE);
            smvi_price4.setText("￥" +layout_data.get("promotionReturn"));
            smvi_price2.setText(layout_data.get("businessReturn"));

        }

        if(chooseValue2.equals(GPACKGE)){//把终端推广返利
            priceName1.setVisibility(View.VISIBLE);
            smvi_price1.setVisibility(View.VISIBLE);
            priceName2.setVisibility(View.GONE);
            smvi_price1.setText("￥" + layout_data.get("terminalReturn"));
            smvi_price2.setVisibility(View.GONE);
            smvi_price4.setVisibility(View.GONE);
            priceName4.setVisibility(View.GONE);
        }



        if(chooseValue2.equals(COMMONLY)){//把终端推广返利
            priceName1.setVisibility(View.GONE);
            smvi_price1.setVisibility(View.GONE);
            priceName2.setVisibility(View.VISIBLE);
            smvi_price2.setVisibility(View.VISIBLE);
            smvi_price4.setVisibility(View.GONE);
            priceName4.setVisibility(View.GONE);
            smvi_price2.setText(layout_data.get("businessReturn"));
        }

    }

    public void setItemClick(View view, final Holder holder, final ArrayList<Map> listdata, final int position,final String chooseValue2) {


        Button setMoney= (Button) view.findViewById(R.id.smv2_setMoney);
        boolean canBeDiscount= (boolean)listdata.get(position).get("canBeDiscount");
        if(canBeDiscount){//可以设置优惠金额
            TextView amount= (TextView) view.findViewById(R.id.smvi_amount);
            amount.setVisibility(View.VISIBLE);
            amount.setText("优惠金额￥:"+(String)listdata.get(position).get("amount"));

            setMoney.setVisibility(View.VISIBLE);
            setMoney.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String maxMoney=(String) listdata.get(position).get("amountMax");

                        Log.i("smf2", "setMoney.setOnClickListener maxMoney"+maxMoney);
                        if(maxMoney==null||maxMoney.equals("")) {

                            MyToast.show("未配置优惠额度", getActivity());

                        }else{
                            Intent intent = new Intent(getActivity(), shopManagementSetMoneyActivity.class);
                            String packageID = (String) listdata.get(position).get("packageId");
                            String id=(String) listdata.get(position).get("id");

                            Log.i("smf3", "packageId=" + packageID+",,,maxMoney=="+maxMoney);
                            intent.putExtra("packageId", packageID);
                            intent.putExtra("id", id);
                            intent.putExtra("maxMoney", maxMoney);
                            intent.putExtra("chooseValue2", chooseValue2);
                            startActivity(intent);
                        }
                    }
            });

        } else {
            setMoney.setVisibility(View.GONE);
        }


        Button withdraw= (Button) view.findViewById(R.id.smv2_withdraw);//撤回待选区
        String type= (String) listdata.get(position).get("type");
        if(type.equals(TYPE1)){
            //withdraw.setVisibility(View.GONE);
            withdraw.setText("撤回待选区");
            withdraw.setBackgroundColor(getResources().getColor(R.color.gray));
        }else{
            withdraw.setVisibility(View.VISIBLE);
            withdraw.setText("撤回待选区");
            withdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("smf2", "withdraw.setOnClickListener");
                    String packageID = (String) listdata.get(position).get("packageId");
                    HashMap<String, String> postparams = new HashMap<String, String>();
                    postparams.put("id", CRApplication.getId(getActivity()));
                    postparams.put("packageId", packageID);
                    HttpTool.sendPost(getActivity(), Constant.URL.SHOPMANAGEMENTRETURN, postparams, new HttpPostProp() {
                        @Override
                        public void dealRecord(Map record) {
                            boolean success = (boolean) record.get("success");
                            String des = (String) record.get("des");
                            MyToast.show(des, getActivity());

                            if (success) {
                                loadData();
                            }
                        }
                    });
                }
            });
        }


    }

    class Holder{
        public ImageView imageView;
        public int position;
        public int viewSize=0;
        public LinearLayout layout;
        public Button button1;
        public TextView textView;
    }


    public void loadData(){
        initLayoutData();
    }
}
