package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestpay.plugin.Plugin;
import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.base.util.NullUtils;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.util.MyAsynImageLoader;
import com.crunii.android.fxpt.view.AppleListView;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class OrderAFragment extends Fragment {

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
    EditText myorder_searchEdit,partnerOrder_searchEdit;
    Button choose_condation;
    ImageView iv_keyword,srh_keyword;
    RelativeLayout rl_myOrder,rl_myPartnerOrder;


    public enum Category{
        mySelfOrder,myPartnerOrder;
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
                if (result.optInt("userLevel") == 2){
                    rb_myPartnerOrder.setVisibility(View.GONE);
                }
            }


        }.execute("");
    }

    RadioButton rb_myPartnerOrder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_order_a, container, false);

        rl_myOrder = (RelativeLayout) view.findViewById(R.id.rl_myOrder);
        rl_myPartnerOrder = (RelativeLayout) view.findViewById(R.id.rl_myPartnerOrder);

        //默认选中我的订单
        category = Category.mySelfOrder.toString();
        ((RadioButton)view.findViewById(R.id.rb_mySelfOrder)).setChecked(true);
        rl_myOrder.setVisibility(View.VISIBLE);
        rl_myPartnerOrder.setVisibility(View.GONE);
        myorder_searchEdit =(EditText)view.findViewById(R.id.myorder_searchEdit);
        partnerOrder_searchEdit =(EditText)view.findViewById(R.id.partnerOrder_searchEdit);

        ((RadioGroup) view.findViewById(R.id.rg_order_category)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mySelfOrder:
                        rl_myOrder.setVisibility(View.VISIBLE);
                        rl_myPartnerOrder.setVisibility(View.GONE);
                        category = Category.mySelfOrder.toString();
                        choose_condation.setText(condationList[0]);
                        queryType = QueryType.goodsName.toString();
                        keyword=myorder_searchEdit.getText().toString();
                        break;
                    case R.id.rb_myPartnerOrder:
                        rl_myOrder.setVisibility(View.GONE);
                        rl_myPartnerOrder.setVisibility(View.VISIBLE);
                        category = Category.myPartnerOrder.toString();
                        keyword=partnerOrder_searchEdit.getText().toString();
                        break;
                }
                refresh(true);
            }
        });
        rb_myPartnerOrder = (RadioButton)view.findViewById(R.id.rb_myPartnerOrder);

        noneView = view.findViewById(R.id.noneView);

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
                String keyword1 = myorder_searchEdit.getText().toString();
                doRefresh(keyword1);
            }
        });
        srh_keyword = (ImageView)view.findViewById(R.id.srh_keyword);
        srh_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword1 = partnerOrder_searchEdit.getText().toString();
                doRefresh(keyword1);
            }
        });
        title = (TextView) view.findViewById(R.id.title);

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

    public void doKeyword(View view){
        Intent i = new Intent(getActivity(), KeywordActivity.class);
        i.putExtra("hint", "请输入商品名称、订单编号");
        getActivity().startActivityForResult(i, OrderActivity.ORDER_A_KEYWORD_REQUEST);
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
                Intent intent = new Intent(getActivity(), OrderADetial.class);
                intent.putExtra("orderId", viewHolder.orderIdStr);
                startActivity(intent);
            }
        });

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
                return CRApplication.getApp().orderlist(getActivity(), pageindex, keyword,category,queryType);
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
                view = inflater.inflate(R.layout.order_list_item, null);
                viewHolder = new ViewHolder();

                viewHolder.orderId = (TextView) view.findViewById(R.id.orderId);
                viewHolder.statusDesc = (TextView) view.findViewById(R.id.statusDesc);
                viewHolder.goods_name = (TextView) view.findViewById(R.id.goods_name);
                viewHolder.pay_amount = (TextView) view.findViewById(R.id.pay_amount);
                viewHolder.create_time = (TextView) view.findViewById(R.id.create_time);
                viewHolder.od_receiver = (TextView) view.findViewById(R.id.od_receiver);
                viewHolder.sellerName = (TextView) view.findViewById(R.id.sellerName);


                viewHolder.orderCancel = (ImageView) view.findViewById(R.id.orderCancel);
                viewHolder.orderPay = (Button) view.findViewById(R.id.orderPay);
                viewHolder.orderRefund = (Button) view.findViewById(R.id.orderRefund);
                viewHolder.orderReturn = (Button) view.findViewById(R.id.orderReturn);
                viewHolder.orderConfirm = (Button) view.findViewById(R.id.orderConfirm);

                view.setTag(viewHolder);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doItemClick(v);
                    }
                });
                viewHolder.orderCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOrderCancel(v);
                    }
                });
                viewHolder.orderPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOrderPay(v);
                    }
                });
                viewHolder.orderRefund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOrderRefund(v);
                    }
                });
                viewHolder.orderReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOrderReturn(v);
                    }
                });
                viewHolder.orderConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doOrderConfirm(v);
                    }
                });


            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.position = position;

            viewHolder.orderCancel.setTag(position);
            viewHolder.orderPay.setTag(position);
            viewHolder.orderRefund.setTag(position);
            viewHolder.orderConfirm.setTag(position);

            showListItem(itemHolder.get(position), viewHolder);

            return view;
        }
    }

    class ViewHolder {
        int position;
        String goodsTempType;//目的是为了根据模板点击展示充值卡信息
        String orderIdStr;
        String goodsId;
        String payType;

        TextView orderId;
        TextView goods_name;
        TextView create_time;
        TextView sellerName;
        TextView pay_amount;
        TextView statusDesc;
        TextView od_receiver;

        ImageView orderCancel;
        Button orderPay;
        Button orderRefund;
        Button orderReturn;
        Button orderConfirm;

    }

    private void showListItem(JSONObject json, ViewHolder viewHolder) {

        viewHolder.orderIdStr = json.optString("orderId");
        viewHolder.goods_name.setText(json.optString("goodsName"));
        viewHolder.goodsTempType = json.optString("goods_temp_type");
        viewHolder.goodsId = json.optString("goodsId");
        viewHolder.orderId.setText(json.optString("orderId"));
        viewHolder.payType = json.optString("payType");
        viewHolder.pay_amount.setText("¥" + json.optString("payAmount"));
        viewHolder.od_receiver.setText("收货人：" + json.optString("od_receiver"));
        viewHolder.sellerName.setText("分销商："+json.optString("sellerName"));
        viewHolder.statusDesc.setText(json.optString("statusDesc"));
        viewHolder.create_time.setText(json.optString("createTime"));

        if(json.optString("od_receiver").isEmpty()){
            viewHolder.od_receiver.setVisibility(View.GONE);
        }

        if (json.optString("status").equals("unpay")) { //待付款
            viewHolder.statusDesc.setText("待付款");
            viewHolder.orderCancel.setVisibility(View.VISIBLE);
            viewHolder.orderPay.setVisibility(View.VISIBLE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.GONE);

        } else if (json.optString("status").equals("unsent")) { //待发货
            viewHolder.orderCancel.setVisibility(View.VISIBLE);
            viewHolder.orderPay.setVisibility(View.GONE);
            //模板为1003的不允许退款，需隐藏退款按钮
            if(json.optString("payAmount").equals("0.00") && json.optString("goods_temp_type").equals(ProductDetailRechargeableCard.templateId))
            {
                viewHolder.orderRefund.setVisibility(View.GONE);
            }else if(viewHolder.payType.equals("offLine"))//线下支付的需要隐藏退款按钮
            {
                viewHolder.orderRefund.setVisibility(View.GONE);
            }else{
                viewHolder.orderRefund.setVisibility(View.VISIBLE);
            }
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.GONE);

        } else if (json.optString("status").equals("sent")) { //已发货
            viewHolder.orderCancel.setVisibility(View.GONE);
            viewHolder.orderPay.setVisibility(View.GONE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.VISIBLE);

        } else if (json.optString("status").equals("refund")) { //退款中
            viewHolder.orderCancel.setVisibility(View.GONE);
            viewHolder.orderPay.setVisibility(View.GONE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.GONE);

        } else if(json.optString("status").equals("received")){
            viewHolder.orderCancel.setVisibility(View.GONE);
            viewHolder.orderPay.setVisibility(View.GONE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.VISIBLE);
            viewHolder.orderConfirm.setVisibility(View.GONE);
        } else { //其他状态
            viewHolder.orderCancel.setVisibility(View.GONE);
            viewHolder.orderPay.setVisibility(View.GONE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.GONE);

        }

        if(!json.optString("sellerName").isEmpty()){
            viewHolder.od_receiver.setVisibility(View.GONE);
            viewHolder.sellerName.setVisibility(View.VISIBLE);
            viewHolder.orderCancel.setVisibility(View.GONE);
            viewHolder.orderPay.setVisibility(View.GONE);
            viewHolder.orderRefund.setVisibility(View.GONE);
            viewHolder.orderReturn.setVisibility(View.GONE);
            viewHolder.orderConfirm.setVisibility(View.GONE);
        }else{
            viewHolder.od_receiver.setVisibility(View.VISIBLE);
            viewHolder.sellerName.setVisibility(View.GONE);
        }

    }

    final static String REQUEST_RESOURCE = "orderList";


    public void doItemClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();

        if(Constant.TEST_FLAG){
            Toast.makeText(getActivity(),viewHolder.payType,Toast.LENGTH_SHORT).show();
        }
        String statusNum = itemHolder.get(viewHolder.position).optString("statusNum");
        if (viewHolder.goodsTempType.equals(ProductDetailRechargeableCard.templateId)) {
            if (statusNum.equals("5")) {//订单已完成才能让客户查看卡密具体资料
                Intent intent = new Intent(getActivity(), RechargeableCardShow.class);
                intent.putExtra("orderId", viewHolder.orderIdStr);
                intent.putExtra("goodsId",viewHolder.goodsId);
                intent.putExtra("requestRe", REQUEST_RESOURCE);
                startActivity(intent);
            }else{
                Toast.makeText(getActivity(), Html.fromHtml("此订单为电子卡密订单，只有订单状态为<font color=\"#f6f789\">已完成</font>才能查看详情"),Toast.LENGTH_LONG).show();
            }
        }else{
            Intent intent = new Intent(getActivity(),OrderADetial.class);
            intent.putExtra("orderId",viewHolder.orderIdStr);
            startActivity(intent);
        }

    }

    private void showData(Holder holder, JSONObject jsonObject) {
        String config = "";
        String cardPwd = jsonObject.optString("cardPwd");
        String need_deal = jsonObject.optString("need_deal");
        if (need_deal.equals("0")) {//-1直接展示 0：未处理 1：已处理
            config = "【数据配置中】";
            cardPwd = "**************";
        } else {
            config = "【该卡可使用】";
        }
        holder.orderNumber.setText(jsonObject.optString("cardNo"));
        holder.password.setText(cardPwd);
        holder.config.setText(config);
    }

    class Holder {
        TextView orderNumber;
        TextView password;
        TextView config;

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

    public void doOrderCancel(View v) {
        int position = (Integer) v.getTag();
        final String orderId = itemHolder.get(position).optString("id");

        Intent i = new Intent(getActivity(), CancelOrderActivity.class);
        i.putExtra("orderId", orderId);
        i.putExtra("isBL", false);
        startActivityForResult(i, CANCELORDER_REQUEST);
    }


    public void doOrderPay(View v) {
        int position = (Integer) v.getTag();
        String id = itemHolder.get(position).optString("id");
        String amount = itemHolder.get(position).optString("amount");
        String name = itemHolder.get(position).optString("name");

        //Toast.makeText(this, id, Toast.LENGTH_LONG).show();
        doWebPay(id);
        //doBestPay(id, amount, name);
    }

    private void doWebPay(String orderId) {
        Intent i = new Intent(getActivity(), WebPayActivity.class);
        i.putExtra("orderId", orderId);
        startActivityForResult(i, WEBPAY_REQUEST);
    }

    private void doBestPay(String orderSeq, String orderAmount, String productDesc) {
        Hashtable<String, String> paramsHashtable = new Hashtable<String, String>();
        // 商户ID，必填
        paramsHashtable.put(Plugin.MERCHANTID, "0018888888");
        // 子商户ID，选填
        paramsHashtable.put(Plugin.SUBMERCHANTID, "");
        // 交易key,必填
        paramsHashtable.put(Plugin.MERCHANTPWD, "321123");
        // 订单编号，必填
        paramsHashtable.put(Plugin.ORDERSEQ, orderSeq);
        // 订单金额，单位元，小数点后取两位 订单金额= 产品金额 + 附加金额， 必填
        paramsHashtable.put(Plugin.ORDERAMOUNT, orderAmount);
        // 产品金额，单位元，小数点后取两位， 选填
        paramsHashtable.put(Plugin.PRODUCTAMOUNT, orderAmount);
        // 附加金额，单位元，小数点后取两位，选填
        paramsHashtable.put(Plugin.ATTACHAMOUNT, "0.00");
        // 订单时间，格式yyyyMMddhhmmss，必填
        paramsHashtable.put(Plugin.ORDERTIME, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date(System.currentTimeMillis())));
        // 订单有效时间，格式yyyyMMddhhmmss,必填
        paramsHashtable.put(Plugin.ORDERVALIDITYTIME, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date(System.currentTimeMillis() + 60 * 1000 * 60 * 24)));
        // 产品名称，必填
        paramsHashtable.put(Plugin.PRODUCTDESC, productDesc);
        // 用户ID，选填
        paramsHashtable.put(Plugin.CUSTOMERID, "01");
        // 币种，固定填“RMB”，必填
        paramsHashtable.put(Plugin.CURTYPE, "RMB");
        // 后台通知地址，必填
        paramsHashtable.put(Plugin.BACKMERCHANTURL, "www.sina.com");
        // 附加信息，选填
        paramsHashtable.put(Plugin.ATTACH, "");
        // 产品ID，选填
        paramsHashtable.put(Plugin.PRODUCTID, "01");
        // 用户IP，必填
        paramsHashtable.put(Plugin.USERIP, "192.168.11.130");
        // 分账明细，选填
        paramsHashtable.put(Plugin.DIVDETAILS, "");

        Plugin.pay(getActivity(), paramsHashtable);
    }


    public void doOrderRefund(View v) {
        int position = (Integer) v.getTag();
        final String orderId = itemHolder.get(position).optString("id");

        Intent i = new Intent(getActivity(), RefundOrderActivity.class);
        i.putExtra("orderId", orderId);
        startActivityForResult(i, REFUNDORDER_REQUEST);
    }

    public void doOrderReturn(View v) {
        int position = (Integer) v.getTag();
        final String orderId = itemHolder.get(position).optString("id");

        Intent i = new Intent(getActivity(), ReturnOrderActivity.class);
        i.putExtra("orderId", orderId);
        startActivityForResult(i, REFUNDORDER_REQUEST);
    }


    public void doOrderConfirm(View v) {
        int position = (Integer) v.getTag();
        final String orderId = itemHolder.get(position).optString("id");

        Dialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("是否确认收货?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderConfirm(orderId);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
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

    public void doRefresh(String keyword1) {
        keyword = keyword1;
        refresh(true);
    }

}
