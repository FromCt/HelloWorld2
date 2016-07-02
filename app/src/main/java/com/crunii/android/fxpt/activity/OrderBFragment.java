package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.util.MyAsynImageLoader;
import com.crunii.android.fxpt.view.AppleListView;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBFragment extends Fragment {

    final static int CANCELORDER_REQUEST = 3;
    final static int KEYWORD_REQUEST = 2;
    final static int PAY_ORDER = 1;
    AppleListView listView;
    List<JSONObject> itemHolder = new ArrayList<JSONObject>();
    List<JSONObject> partnerListHolder = new ArrayList<JSONObject>();
    ListAdapter lvAdapter;
    private int yearInt = 2015,mouthInt = 1;
    int pageindex = 1;
    private View noneView;
    private MyAsynImageLoader imageLoader;
    private TextView title;

    RelativeLayout rl_myOrder,rl_myPartnerOrder;



    private String keyword = "",category = "",queryType = "",queryTime = "",queryPartnerId = "";
    Button choose_condation,bt_year,bt_mouth,bt_choose_partner;
    ImageView iv_keyword;
    RadioButton rb_myPartnerOrder;

    public enum Category{
        mySelfOrder,myPartnerOrder;
    }
    public enum QueryType{
        recentlyThreeMonth,allYear,all2014,
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserDetail();

        Map<String,String> map0 = new HashMap<String, String>();
        map0.put("id","all");
        map0.put("name", "全部");
        partnerListHolder.add(0, new JSONObject(map0));
        queryPartnerId = "all";
    }

    public void getUserDetail() {
        new BaseTask<String, String, JSONObject>(getActivity(), "请稍后...") {

            @Override
            protected JSONObject doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().userDetail(getActivity());
            }

            @Override
            protected void onSuccess(JSONObject result) {
                if (result.optInt("userLevel") == 2){
                    rb_myPartnerOrder.setVisibility(View.GONE);
                }else{
                    getPartnerList();
                }
            }


        }.execute("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_order_b, container, false);
        title = (TextView) view.findViewById(R.id.title);
        noneView = view.findViewById(R.id.noneView);

        rl_myOrder = (RelativeLayout) view.findViewById(R.id.rl_myOrder);
        rl_myPartnerOrder = (RelativeLayout) view.findViewById(R.id.rl_myPartnerOrder);

        rb_myPartnerOrder = (RadioButton)view.findViewById(R.id.rb_myPartnerOrder);
        //默认选中我的订单
        category = Category.mySelfOrder.toString();
        ((RadioButton)view.findViewById(R.id.rb_mySelfOrder)).setChecked(true);
        rl_myOrder.setVisibility(View.VISIBLE);
        rl_myPartnerOrder.setVisibility(View.GONE);
        ((RadioGroup) view.findViewById(R.id.rg_order_category)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mySelfOrder:
                        rl_myOrder.setVisibility(View.VISIBLE);
                        rl_myPartnerOrder.setVisibility(View.GONE);
                        category = Category.mySelfOrder.toString();
                        choose_condation.setText(condationList[0]);
                        queryType = QueryType.recentlyThreeMonth.toString();
                        break;
                    case R.id.rb_myPartnerOrder:
                        rl_myOrder.setVisibility(View.GONE);
                        rl_myPartnerOrder.setVisibility(View.VISIBLE);
                        category = Category.myPartnerOrder.toString();
                        break;
                }
                refresh(true);
            }
        });
        choose_condation = (Button) view.findViewById(R.id.choose_condation);
        choose_condation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChooseCondation(v);
            }
        });
        iv_keyword = (ImageView)view.findViewById(R.id.iv_keyword);
        iv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doKeyword(v);
            }
        });
        bt_year = (Button) view.findViewById(R.id.bt_year);
        bt_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doYear(v);
            }
        });
        bt_mouth = (Button) view.findViewById(R.id.bt_mouth);
        bt_mouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMouth(v);
            }
        });
        bt_choose_partner = (Button) view.findViewById(R.id.bt_choose_partner);
        bt_choose_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePartnerId(v);
            }
        });

        imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), getActivity());

        listView = (AppleListView) view.findViewById(R.id.listView);

        //第一次加载
        queryType = QueryType.recentlyThreeMonth.toString();
        //默认设置当前年月
        Calendar calendar = Calendar.getInstance();
        yearInt = calendar.get(Calendar.YEAR);
        mouthInt = calendar.get(Calendar.MONTH)+1;
        bt_year.setText(String.valueOf(yearInt));
        bt_mouth.setText(String.valueOf(mouthInt));

        bt_choose_partner.setText("全部");

        return view;
    }


    void getPartnerList() {

        new BaseTask<String, String, JsonPage>(getActivity(), "请稍后...") {

            @Override
            protected JsonPage doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().partnerlist(getActivity(), pageindex, keyword);
            }

            @Override
            protected void onSuccess(JsonPage result) {
                List<JSONObject> orderList = result.getJsonList();
                partnerListHolder.clear();
                for (int i = 0; i < orderList.size(); i++) {
                    partnerListHolder.add(orderList.get(i));
                }
                Map<String,String> map0 = new HashMap<String, String>();
                map0.put("id","all");
                map0.put("name","全部");
                partnerListHolder.add(0,new JSONObject(map0));
            }

            @Override
            protected void onPostExecute(JsonPage result) {
                super.onPostExecute(result);

                listView.onRefreshComplete();

            }
        }.execute("");
    }

    static final String[] condationList = new String[]{"最近三个月订单","全年订单","2014年订单"};
    public void doChooseCondation(View view){

        new AlertDialog.Builder(getActivity()).setItems(condationList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        queryType = QueryType.recentlyThreeMonth.toString();
                        choose_condation.setText(condationList[0]);
                        break;
                    case 1:
                        queryType = QueryType.allYear.toString();
                        choose_condation.setText(condationList[1]);
                        break;
                    case 2:
                        queryType = QueryType.all2014.toString();
                        choose_condation.setText(condationList[2]);
                        break;
                }
                refresh(true);
            }
        }).show();

    }
    private static final String[] yearList = {"2014","2015"};//,"2016","2017","2018","2019","2020"
    private static final String[] mouthList = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    public void doYear(View view){
        new AlertDialog.Builder(getActivity()).setItems(yearList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yearInt = Integer.valueOf(yearList[which]);
                bt_year.setText(yearList[which]);

                refresh(true);
            }
        }).show();
    }



    public void doMouth(View view){

        new AlertDialog.Builder(getActivity()).setItems(mouthList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mouthInt = which+1;
                bt_mouth.setText(mouthList[which]);
                refresh(true);
            }
        }).show();
    }

    public void doKeyword(View view){
        Intent i = new Intent(getActivity(), KeywordActivity.class);
        i.putExtra("hint", "请输入订单编号");
        startActivityForResult(i, OrderActivity.ORDER_B_KEYWORD_REQUEST);
    }

    public void choosePartnerId(View view){
        int length = partnerListHolder.size();
        String[] partnerArray = new String[length];
        for(int i=0;i<length;i++){
            partnerArray[i] = partnerListHolder.get(i).optString("name");
        }
        new AlertDialog.Builder(getActivity()).setItems(partnerArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                queryPartnerId = partnerListHolder.get(which).optString("id");
                bt_choose_partner.setText(partnerListHolder.get(which).optString("name"));
                refresh(true);
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        lvAdapter = new ListAdapter(getActivity().getBaseContext());
        listView.setAdapter(lvAdapter);
        listView.setonRefreshListener(new AppleListView.OnRefreshListener() {
            public void onRefresh() {
                pageindex = 1;
                refresh(true);
            }
        });
        listView.setPrevButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageindex > 1) {
                    pageindex--;
                }
                refresh(true);
            }
        });
        listView.setNextButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex++;
                refresh(true);
            }
        });
        listView.hidePrevButton();
        listView.hideNextButton();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                Intent intent = new Intent(getActivity(), ExposeDetial.class);
                intent.putExtra("orderId", viewHolder.orderIdStr);
                startActivity(intent);
            }
        });

        refresh(true);
    }


    private void refresh(final boolean showProgressDialog) {

        new BaseTask<String, String, JsonPage>(getActivity()) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                if (showProgressDialog) {
                    this.loadMask = ProgressDialog.show(context, null, "请稍候...");
                }
            }

            @Override
            protected JsonPage doInBack(String... arg0) throws HttpException,
                    IOException, TaskResultException {
                return CRApplication.getApp().transcribeOrderList(getActivity(), pageindex, keyword,category,queryType,queryTime,queryPartnerId);
            }

            @Override
            protected void onSuccess(JsonPage result) {
                if (result.getJsonList().size() == 0) {
                    noneView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    return;
                } else {
                    noneView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }

                List<JSONObject> orderList = result.getJsonList();
                if (result.isHasPrevPage() || result.isHasNextPage()) {
                    if (result.isHasPrevPage()) {
                        listView.enablePrevButton();
                    } else {
                        listView.disablePrevButton();
                    }
                    if (result.isHasNextPage()) {
                        listView.enableNextButton();
                    } else {
                        listView.disableNextButton();
                    }
                } else {
                    listView.hidePrevButton();
                    listView.hideNextButton();
                }

                itemHolder.clear();
                for (int i = 0; i < orderList.size(); i++) {
                    itemHolder.add(orderList.get(i));
                }
                lvAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(JsonPage result) {
                super.onPostExecute(result);
                if (showProgressDialog) {
                    this.loadMask.dismiss();
                }
                listView.onRefreshComplete();
            }

        }.execute("");
    }


    class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ListAdapter(Context mContext) {
            super();
            this.inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return itemHolder.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.activity_transcribe_item, null);
                viewHolder = new ViewHolder();

                viewHolder.orderId = (TextView) view.findViewById(R.id.orderId);
                viewHolder.subCompany = (TextView) view.findViewById(R.id.subCompany);
                viewHolder.netName = (TextView) view.findViewById(R.id.netName);
                viewHolder.time = (TextView) view.findViewById(R.id.time);
                viewHolder.stateDesc = (TextView) view.findViewById(R.id.stateDesc);
                viewHolder.image = (ImageView) view.findViewById(R.id.image);
                viewHolder.orderCancel = (ImageView) view.findViewById(R.id.orderCancel);
                viewHolder.payAmount = (TextView) view.findViewById(R.id.payAmount);
                viewHolder.phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);

                viewHolder.doing = (Button) view.findViewById(R.id.doing);
//              viewHolder.undo = (Button) view.findViewById(R.id.undo);
                viewHolder.finish = (Button) view.findViewById(R.id.finish);
                viewHolder.fail = (Button) view.findViewById(R.id.fail);
                viewHolder.reason = (Button) view.findViewById(R.id.reason);
                viewHolder.pay = (Button) view.findViewById(R.id.pay);

                view.setTag(viewHolder);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doItemClick(v);
                    }
                });
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.position = position;
            viewHolder.orderCancel.setTag(position);
            viewHolder.reason.setTag(position);
            viewHolder.pay.setTag(position);
            viewHolder.image.setTag(position);
            viewHolder.orderCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    final String orderId = itemHolder.get(position).optString("orderId");
                    Intent i = new Intent(getActivity(), CancelOrderActivity.class);
                    i.putExtra("orderId", orderId);
                    i.putExtra("isBL", true);
                    startActivityForResult(i, CANCELORDER_REQUEST);
                }
            });
            viewHolder.reason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    String failDesc = itemHolder.get(position).optString("failDesc");
                    new AlertDialog.Builder(getActivity())
                            .setTitle("受理失败原因")
                            .setMessage(failDesc)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                }
                            }).show();
                }
            });
            viewHolder.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    Intent i = new Intent(getActivity(), WebPayActivity.class);
                    i.putExtra("orderId", itemHolder.get(position).optString("orderId"));
                    startActivity(i);
                }
            });
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    String imgUrl = itemHolder.get(position).optString("picpre");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(imgUrl), "image/*");
                    startActivity(intent);
                }
            });


            showListItem(itemHolder.get(position), viewHolder);


            return view;
        }
    }

    class ViewHolder {
        int position;
        String orderIdStr;

        TextView orderId;
        TextView time;
        TextView subCompany;
        TextView netName;
        ImageView image;
        TextView stateDesc;
        TextView payAmount;
        TextView phoneNumber;

        ImageView orderCancel;
        Button undo;
        Button doing;
        Button finish;
        Button fail;
        Button reason;
        Button pay;

    }

    private void showListItem(JSONObject json, ViewHolder viewHolder) {
        System.out.println("json=" + json.toString());
        imageLoader.showImageAsyn(new SoftReference<ImageView>(viewHolder.image),json.optString("picpre") , R.drawable.phone);
        String time = json.optString("time");
        String rq = "", sj = "";
        if (time.length() > 13) {
            rq = time.substring(0, 10);
            sj = time.substring(11, time.length() - 2);
        }
//        viewHolder.time.setText(time);
        //viewHolder.hours.setText(sj);

        String orderId = json.optString("orderId");
        String subCompany = json.optString("subCompany");
        String netName = json.optString("netName");
        String state = json.optString("state");
        String stateDesc = json.optString("stateDesc");
        boolean isCancle = json.optBoolean("isCancle");
        String payAmount = json.optString("payAmount");
        String phoneNumber = json.optString("phoneNumber");


        viewHolder.orderIdStr = orderId;
        viewHolder.orderId.setText(orderId);
        viewHolder.time.setText(rq+"\n"+sj);
        viewHolder.subCompany.setText(subCompany);
        viewHolder.netName.setText(netName);
        viewHolder.stateDesc.setText(stateDesc);
        viewHolder.payAmount.setText("¥"+payAmount);
        viewHolder.phoneNumber.setText(phoneNumber);

        if(isCancle){
            viewHolder.orderCancel.setVisibility(View.VISIBLE);
        }else{
            viewHolder.orderCancel.setVisibility(View.GONE);

        }
        //“未支付  0”、未处理 1 “待处理 5 ”、“处理中 2”、“已完成 3”、“受理失败  4”、“已取消  is_cancel  1”
        if(state.equals("0")){
            viewHolder.pay.setVisibility(View.VISIBLE);
        }else{
            viewHolder.pay.setVisibility(View.GONE);
        }
        if(state.equals("4")){
            viewHolder.reason.setVisibility(View.VISIBLE);
        }else{
            viewHolder.reason.setVisibility(View.GONE);
        }

    }

    public void doItemClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        Intent intent = new Intent(getActivity(), ExposeDetial.class);
        intent.putExtra("orderId", viewHolder.orderIdStr);
        startActivity(intent);
        //

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case KEYWORD_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    keyword = data.getExtras().getString("keyword");
                    if (!NullUtils.isEmpty(keyword)) {
                        title.setText(keyword);
                        refresh(true);
                    } else {
                        keyword = "";
                        title.setText("电商分销");
                        refresh(true);
                    }
                }
                break;
            case CANCELORDER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    refresh(true);
                }
                break;
            case PAY_ORDER:
                if (resultCode == Activity.RESULT_OK) {
                    refresh(true);
                }
                break;
        }
    }


    private void orderConfirm(String orderId) {

        new BaseTask<String, String, Boolean>(getActivity()) {
            private ProgressDialog loadMask;

            @Override
            protected void onPreExecute() {
                this.loadMask = ProgressDialog.show(context, null, "请稍候...");
            }

            @Override
            protected void onSuccess(Boolean result) {

                //Toast.makeText(getApplicationContext(), "已确认收货。", Toast.LENGTH_SHORT).show();

                refresh(true);
            }

            @Override
            protected void onError() {
                super.onError();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                this.loadMask.dismiss();
            }

            @Override
            protected Boolean doInBack(String... params) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().confirmOrder(getActivity(), params[0]);
            }

        }.execute(orderId);

    }
}
