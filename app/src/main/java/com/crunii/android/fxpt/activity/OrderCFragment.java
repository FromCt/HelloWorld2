package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderCFragment extends Fragment {

    final static int BESTPAY_REQUEST = 1;
    final static int KEYWORD_REQUEST = 2;
    final static int CANCELORDER_REQUEST = 3;
    final static int REFUNDORDER_REQUEST = 4;
    final static int WEBPAY_REQUEST = 5;


    AppleListView listView;
    List<JSONObject> itemHolder = new ArrayList<JSONObject>();

    ListAdapter lvAdapter;

    int pageindex = 1;
    private MyAsynImageLoader imageLoader;
    private String keyword = "",category = "",queryType = "";

    private View noneView;
    private TextView title;
    Button choose_condation;
    ImageView iv_keyword,srh_keyword;
    EditText myorder_searchEdit,partnerOrder_searchEdit;
    RelativeLayout rl_myOrder,rl_myPartnerOrder;


    public enum Category{
        myselfOrder,partnerOrder;
    }

    public enum QueryType{
        goodsName,partnerName,
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserDetail();

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
                Integer userLevel = result.optInt("userLevel");
                if (userLevel == 2){
                    rb_myPartnerOrder.setVisibility(View.GONE);
                }
            }


        }.execute("");
    }

    RadioButton rb_myPartnerOrder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_c, container, false);

        rl_myOrder = (RelativeLayout) view.findViewById(R.id.rl_myOrder);
        rl_myPartnerOrder = (RelativeLayout) view.findViewById(R.id.rl_myPartnerOrder);
        title = (TextView) view.findViewById(R.id.title);
        //默认选中我的订单
        category = Category.myselfOrder.toString();
        ((RadioButton)view.findViewById(R.id.rb_myselfOrder)).setChecked(true);
        rl_myOrder.setVisibility(View.VISIBLE);
        rl_myPartnerOrder.setVisibility(View.GONE);
        myorder_searchEdit =(EditText)view.findViewById(R.id.myorder_searchEdit);
        partnerOrder_searchEdit =(EditText)view.findViewById(R.id.partnerOrder_searchEdit);

        ((RadioGroup) view.findViewById(R.id.rg_order_category)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_myselfOrder:
                        rl_myOrder.setVisibility(View.VISIBLE);
                        rl_myPartnerOrder.setVisibility(View.GONE);
                        category = Category.myselfOrder.toString();
                        queryType = QueryType.goodsName.toString();
                        keyword = myorder_searchEdit.getText().toString();
                        break;
                    case R.id.rb_partnerOrder:
                        rl_myOrder.setVisibility(View.GONE);
                        rl_myPartnerOrder.setVisibility(View.VISIBLE);
                        category = Category.partnerOrder.toString();
                        choose_condation.setText(condationList[0]);
                        queryType = QueryType.goodsName.toString();
                        keyword = partnerOrder_searchEdit.getText().toString();
                        break;
                }
                refresh(true);
            }
        });
        rb_myPartnerOrder = (RadioButton)view.findViewById(R.id.rb_partnerOrder);

        noneView = view.findViewById(R.id.noneView);

        choose_condation = (Button) view.findViewById(R.id.choose_condation);
        choose_condation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChooseCondation(v);
            }
        });
        iv_keyword = (ImageView)view.findViewById(R.id.iv_keyword);
        srh_keyword = (ImageView)view.findViewById(R.id.srh_keyword);

        iv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword1 = myorder_searchEdit.getText().toString();
                doRefresh(keyword1);
            }
        });

        srh_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword1 = partnerOrder_searchEdit.getText().toString();
                doRefresh(keyword1);
            }
        });



        imageLoader = new MyAsynImageLoader(CRApplication.getApp().getHttpClient(), getActivity());

        listView = (AppleListView) view.findViewById(R.id.listView);


        //第一次加载
        queryType = QueryType.goodsName.toString();
        return view;
    }

    static final String[] condationList = new String[]{"商品名称","协同伙伴"};
    public void doChooseCondation(View view){

        new AlertDialog.Builder(getActivity()).setItems(condationList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        queryType = QueryType.goodsName.toString();
                        choose_condation.setText(condationList[0]);
                        break;
                    case 1:
                        queryType = QueryType.partnerName.toString();
                        choose_condation.setText(condationList[1]);
                        break;
                }
                refresh(true);
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

        refresh(true);
    }


    protected void refresh(final boolean showProgressDialog) {
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
                HashMap<String,Object> params = new HashMap<String, Object>();

                params.put("pageindex",pageindex+"");
                params.put("keyword", keyword);
                params.put("category", category);
                params.put("queryType", queryType);
                return CRApplication.getApp().getNetStoreList(getActivity(),params);
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
                view = inflater.inflate(R.layout.activity_netstore_item, null);
                viewHolder = new ViewHolder();

                viewHolder.orderId = (TextView) view.findViewById(R.id.orderId);
                viewHolder.orderState = (TextView) view.findViewById(R.id.orderState);
                viewHolder.goods_name = (TextView) view.findViewById(R.id.goods_name);
                viewHolder.create_time = (TextView) view.findViewById(R.id.create_time);
                viewHolder.nickname = (TextView) view.findViewById(R.id.nickname);
                viewHolder.lb_patnerName = (TextView) view.findViewById(R.id.lb_patnerName);
                viewHolder.patnerName = (TextView) view.findViewById(R.id.patnerName);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.position = position;

            showListItem(itemHolder.get(position), viewHolder);

            return view;
        }
    }

    class ViewHolder {
        int position;
        String orderIdStr;

        TextView orderId;
        TextView orderState;
        TextView goods_name;
        TextView create_time;
        TextView nickname;
        TextView lb_patnerName;
        TextView patnerName;

    }

    private void showListItem(JSONObject json, ViewHolder viewHolder) {
        viewHolder.orderIdStr = json.optString("orderId");
        viewHolder.orderId.setText(json.optString("orderId"));
        viewHolder.orderState.setText(json.optString("orderState"));
        viewHolder.goods_name.setText(json.optString("goodsName"));
        viewHolder.create_time.setText(json.optString("createTime"));
        viewHolder.nickname.setText(json.optString("nickName"));
        viewHolder.patnerName.setText(json.optString("patnerName"));
        if(!json.optString("patnerName").isEmpty()){
            viewHolder.patnerName.setVisibility(View.VISIBLE);
            viewHolder.lb_patnerName.setVisibility(View.VISIBLE);
        }else{
            viewHolder.patnerName.setVisibility(View.GONE);
            viewHolder.lb_patnerName.setVisibility(View.GONE);
        }
    }

    public void onBackPressed() {
        if (NullUtils.isEmpty(keyword)) {
            //finish();
        } else {
            keyword = "";
            title.setText("我的订单");
            refresh(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KEYWORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                keyword = data.getExtras().getString("keyword");
                if (NullUtils.isEmpty(keyword)) {
                    keyword = "";
                    title.setText("电商分销");
                    refresh(true);
                } else {
                    title.setText(keyword);
                    refresh(true);
                }
            }
        } else if (requestCode == CANCELORDER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refresh(true);
            }
        } else if (requestCode == REFUNDORDER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refresh(true);
            }
        } else if (requestCode == BESTPAY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_LONG).show();
                refresh(true);
            } else {
                Toast.makeText(getActivity(), "支付不成功", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == WEBPAY_REQUEST) {
            //webpay无法判断是否成功，只能强制刷新
            refresh(true);
        }
    }

    public void doRefresh(String keyword1) {
        keyword = keyword1;
        refresh(true);
    }

}
