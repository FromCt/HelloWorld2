package com.crunii.android.fxpt.activity;

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
 * 网店管理页签1 待选
 */
public class shopManagementFragment1 extends Fragment {
    private  View view;

    public ListView listView;
    public ArrayList<Map> listData = new ArrayList<>();
    public MyListAdapter adapter=new MyListAdapter(listData);

    //获取页签list的数据
    public void initLayoutData() {

        listData.clear();
        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("id", CRApplication.getId(getActivity()));
        postparams.put("phone", CRApplication.getPhone(getActivity()));
        HttpTool.sendPost(getActivity(), Constant.URL.SHOPMANAGEMENTDAIXUAN, postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                ArrayList<Map> list = (ArrayList<Map>) record.get("menuList");
                listData.addAll(list);
                if (listData != null) {
                    Log.i("smf1", " listData.size========" + listData.size());
                } else {
                    Log.i("smf1", " listData.size========null");
                }

                adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(getActivity(), R.layout.shop_management_fragment2,null);
        listView = (ListView) view.findViewById(R.id.smv2_listView);
        Log.i("smf1", "onCreate listView init ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("smf1", "onCreateView ");
        // initLayoutData();
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("smf1", "onActivityCreated ");
    }


    class MyListAdapter extends BaseAdapter {

        public ArrayList<Map> list=new ArrayList<Map>();
        MyListAdapter(ArrayList<Map> list){
            this.list=list;
        }
        @Override
        public int getCount() {
           // Log.i("smf1", "adapter getCount==" + list.size());
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
           // Log.i("smf1", "adapter getView==" + position);
            final Holder holder;
            if (convertView==null){
                holder=new Holder();
                convertView = View.inflate(getActivity(), R.layout.shop_management_view2_listitem, null);
                holder.button1 = (Button) convertView.findViewById(R.id.smv2_packageName);
                holder.textView = (TextView) convertView.findViewById(R.id.smv2_packageNumber);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.smv2_layout);
                holder.imageView= (ImageView) convertView.findViewById(R.id.smv_image);
                holder.positon=position;
                convertView.setTag(holder);

            }else{
                holder= (Holder) convertView.getTag();
            }

            holder.button1.setText((String)list.get(position).get("menuName"));
            ArrayList<Map> layout_data = (ArrayList) list.get(position).get("resultList");//每次重新获取数据大小
            String leftChooseValue2 = (String) list.get(position).get("leftChooseValue2");
            //待选商品 只针对合约机页签，将“全新装返佣” 名称显示为 “全新装返佣至少 ”

            holder.textView.setText("待选数量：" + layout_data.size());
            if(myMap.get(position+"")!=null&&myMap.get(position+"").equals(position+"")){
                holder.layout.setVisibility(View.VISIBLE);
                Log.i("smf1", "setVisibility(View.VISIBLE)"+position);
            }else{
                holder.layout.setVisibility(View.GONE);
            }
            if (holder.layout.getVisibility() == View.VISIBLE) {
                if (holder.viewSize != layout_data.size()) {//判断holder保存的个数是否和子view个数相同。相同则不用子再次创建
                    holder.layout.removeAllViews();
                    for (int i = 0; i < layout_data.size(); i++) {
                        Log.i("smf1","View.inflate(getActivity(), R.layout.shop_management_view2_layoutitem");
                        holder.viewSize = layout_data.size();
                        View view = View.inflate(getActivity(), R.layout.shop_management_view2_layoutitem, null);
                        if(i==0) {
                            LinearLayout line = (LinearLayout) view.findViewById(R.id.smv2_line);
                            line.setVisibility(View.GONE);
                        }
                        setViewData(view, layout_data.get(i),leftChooseValue2);
                        setItemClick(view, holder, layout_data, i);

                        holder.layout.addView(view);
                        Log.i("smf1", "shopManagementFragment   holder.layout.addView(view)");
                    }
                }else{
                    if(holder.positon==position){//查看当前item是否是之前item 是则不做改变

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

                            setViewData(view, layout_data.get(i),leftChooseValue2);

                            setItemClick(view, holder, layout_data, i);

                            holder.layout.addView(view);
                            Log.i("smf1", "shopManagementFragment   holder.layout.addView(view)");
                        }
                    }
                }
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("smf1", "shopManagementFragment  button onclick");
                    if (holder.layout.getVisibility() == View.VISIBLE) {
                        holder.layout.setVisibility(View.GONE);
                        holder.imageView.setImageResource(R.drawable.sm_right);
                        myMap.remove(position+"");
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


    @Override
    public void onStart() {
        super.onStart();
        Log.i("smf1", "onStart");
        TabBFragment.listView=listView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("smf1", "onResume");
    }

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
            smvi_price4.setText("￥" + layout_data.get("promotionReturn"));
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

    public void setItemClick(View view, final Holder holder, final ArrayList<Map> listdata, final int position) {
               Button setMoney= (Button) view.findViewById(R.id.smv2_setMoney);
        setMoney.setVisibility(View.GONE);

        Button withdraw= (Button) view.findViewById(R.id.smv2_withdraw);//加入选区待选区
        withdraw.setText("加入已选区");
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String packageId = (String) listdata.get(position).get("packageId");
                HashMap<String, String> postparams = new HashMap<String, String>();
                postparams.put("id", CRApplication.getId(getActivity()));
                postparams.put("packageId", packageId);
                HttpTool.sendPost(getActivity(), Constant.URL.SHOPMANAGEMENTADD, postparams, new HttpPostProp() {
                    @Override
                    public void dealRecord(Map record) {
                        boolean success = (boolean) record.get("success");
                        if (success) {
                            MyToast.show("加入成功", getActivity());
                            Log.i("smf1", "withdraw.setOnClickListener");

                            loadData();

                        }
                    }
                });

            }
        });

    }

    class Holder{
        public ImageView imageView;
        public int positon;//记录当前的item 防止重用数据出错
        public int viewSize=0;
        public LinearLayout layout;
        public Button button1;
        public TextView textView;
    }


    public void loadData(){
        initLayoutData();
       // adapter.notifyDataSetChanged();
    }
}
