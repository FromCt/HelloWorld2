package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.view.AppleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/25.
 */
public class DistributionChannelActivity extends Activity {

    private static final String TAG = "DistributionChannelActivity";

    //type=first第一次请求,company分公司，substation支局，member支局成员，distributor分销商下属
    private static enum QueryType {
        first,company,substation,member,distributor,someone,search
    }
    //queryKey查询字段：姓名、手机、类别queryKey(name,phone,category)
    private static enum QueryKey {
        name,phone,category;
    }
    private static final String[] QUERYKEY = {"姓名", "手机", "类别"};

    private Context mContext;
    private Button bt_select, bt_item_name;
    private EditText word;
    private TextView tv_list_title,tv_number;
    private AppleListView listView;
    private LinearLayout ll_persion_detail;
    //queryType=first第一次请求,company分公司，substation支局，member支局成员，distributor分销商下属，someone具体某人,,search查询
    //queryKey 查询字段：姓名、手机、类别queryKey(name,phone,category)
    private String queryId = "", queryType = "", queryKey = "", queryWord = "", titleName = "", titleTotal = "";
    private String permission = "";//第一次请求后，保存用户权限
    private int pageindex = 1;
     MyAdapter myAdapter;
    private DistributionHolder holder;

    List<JSONObject> temporaryList = new ArrayList<JSONObject>();//临时数据
     List<JSONObject> categoryList = new ArrayList<JSONObject>();//渠道类别
     List<JSONObject> companyList = new ArrayList<JSONObject>();  //company分公司
     List<JSONObject> substationList = new ArrayList<JSONObject>(); //substation支局
     List<JSONObject> memberList = new ArrayList<JSONObject>();     //member支局成员
     List<JSONObject> distributorList = new ArrayList<JSONObject>();//distributor分销商下属

    String temporaryTitle = "",temporaryNumber = "",categoryTitle = "",companyTitle = "",substationTitle = "",memberTitle = "",distributorTitle = "",
            companyNumber ="",substationNumber = "",memberNumber = "",distributorNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_distribution_channel);
        bt_select = (Button) findViewById(R.id.bt_select);
        bt_item_name = (Button) findViewById(R.id.bt_item_name);
        word = (EditText) findViewById(R.id.word);
        tv_list_title = (TextView) findViewById(R.id.tv_list_title);
        tv_number = (TextView) findViewById(R.id.tv_number);
        listView = (AppleListView) findViewById(R.id.listView);
        ll_persion_detail = (LinearLayout)findViewById(R.id.ll_persion_detail);
        //第一次进来设置请求参数
        queryType = QueryType.first.toString();
        queryKey = QueryKey.name.toString();
        listView.setVisibility(View.VISIBLE);
        ll_persion_detail.setVisibility(View.GONE);
        requestListData();
        initListener();
    }

    private void initListener() {
        /** --------listView------- **/
        myAdapter = new MyAdapter(mContext);
        listView.setAdapter(myAdapter);
        listView.setonRefreshListener(new AppleListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageindex = 1;
                requestListData();
            }
        });
        listView.setPrevButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageindex > 1) {
                    queryType = QueryType.search.toString();
                    pageindex--;
                    requestListData();
                }
            }
        });
        listView.setNextButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryType = QueryType.search.toString();
                pageindex++;
                requestListData();
            }
        });
        listView.hidePrevButton();
        listView.hideNextButton();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                holder = (DistributionHolder) view.getTag();
                queryId = holder.id;
                queryType = holder.queryType;
                currentPage = queryType;//每次选择赋值当前页
                if (queryType.equals(QueryType.someone.toString())) {//分销商页面，请求具体个人信息
                    tv_list_title.setText(holder.name);
                    tv_number.setText("");
                    requestDistributorDetail();
                    ll_persion_detail.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {//其他请求list
                    requestListData();
                    myAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                    ll_persion_detail.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestDistributorDetail(){
        new BaseTask<String, String, JSONObject>(this,"正在加载...") {

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().distributorDetail(mContext, queryId);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                initDistributorDetail(jsonObject);
            }
        }.execute("");
    }

    private void initDistributorDetail(JSONObject jsonObject){
        String loginPhone, companyName, substationName, distributorName,categoryName,lastOrderTime,distributorPhone;
        loginPhone = jsonObject.optString("loginPhone");
        companyName = jsonObject.optString("companyName");
        substationName = jsonObject.optString("substationName");
        distributorName = jsonObject.optString("distributorName");
        categoryName = jsonObject.optString("categoryName");
        lastOrderTime = jsonObject.optString("lastOrderTime");
        distributorPhone = jsonObject.optString("distributorPhone");
        ((TextView)findViewById(R.id.loginPhone)).setText(loginPhone);
        ((TextView)findViewById(R.id.companyName)).setText(companyName);
        ((TextView)findViewById(R.id.substationName)).setText(substationName);
        ((TextView)findViewById(R.id.distributorName)).setText(distributorName+"("+distributorPhone+")");
        ((TextView)findViewById(R.id.categoryName)).setText(categoryName);
        ((TextView)findViewById(R.id.lastOrderTime)).setText(lastOrderTime);
    }

    private void requestListData() {

        new BaseTask<String, String, JSONObject>(this,"正在加载...") {

            @Override
            protected JSONObject doInBack(String... strings) throws HttpException, IOException, TaskResultException {
                return CRApplication.getApp().distributionSearch(mContext, queryId, queryType, queryKey, queryWord, pageindex);
            }

            @Override
            protected void onSuccess(JSONObject jsonObject) {
                initData(jsonObject);
            }

        }.execute("");
    }

    private void initData(JSONObject jsonObject) {
        List<JSONObject> lingshiList = new ArrayList<JSONObject>();
        titleName = jsonObject.optString("titleName");
        titleTotal = jsonObject.optString("titleTotal");

        JSONArray array = null, categoryArray = null;
        int prePage = 1, nextPage = 1;
        try {
            categoryArray = jsonObject.getJSONArray("categoryList");
            array = jsonObject.getJSONArray("list");
            prePage = jsonObject.getInt("prePage");
            nextPage = jsonObject.getInt("nextPage");
            for (int i = 0; i < array.length(); i++) {
                lingshiList.add(array.getJSONObject(i));
            }
            for (int i = 0; i < categoryArray.length(); i++) {
                categoryList.add(categoryArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.e("分销渠道：", "Json解析数据失败");
        }
        String responseQueryType = "";
        if(array.length() > 0){
            responseQueryType = array.optJSONObject(0).optString("queryType");
        }
        if (queryType.equals(QueryType.first.toString())) {//第一次请求，保存用户权限
            permission = switchPermission(responseQueryType);
            currentPage = permission ;
        }
        if(queryType.equals(QueryType.someone.toString())){

            temporaryTitle = titleName;
            temporaryNumber = "";
        }else{

            temporaryTitle = titleName;
            temporaryNumber = "("+titleTotal+")";

        }
        tv_list_title.setText(temporaryTitle);
        tv_number.setText(temporaryNumber);

        copyList(responseQueryType, lingshiList, temporaryTitle,temporaryNumber);
        JsonPage jsonPage = new JsonPage(lingshiList, pageindex, prePage, nextPage);
        showView(jsonPage);
    }

    /**
     * 根据权限赋值List
     */
    private void copyList(String  permission,List<JSONObject> list,String temporaryTitle,String temporaryNumber){
        String linshiPermission = switchPermission(permission);
        if(linshiPermission.equals(QueryType.company.toString())){
            companyList.clear();
            companyList.addAll(list);
            companyTitle = temporaryTitle;
            companyNumber = temporaryNumber;
        }else if(linshiPermission.equals(QueryType.substation.toString())){
            substationList.clear();
            substationList.addAll(list);
            substationTitle = temporaryTitle;
            substationNumber = temporaryNumber;
        }else if(linshiPermission.equals(QueryType.member.toString())){
            memberList.clear();
            memberList.addAll(list);
            memberTitle = temporaryTitle;
            memberNumber = temporaryNumber;
        }else if(linshiPermission.equals(QueryType.distributor.toString())){
            distributorList.clear();
            distributorList.addAll(list);
            distributorTitle = temporaryTitle;
            distributorNumber = temporaryNumber;
        }
    }

    /**
     * 当用户第一次请求，根据下一级决定用户权限
     *
     * @param subPermission
     * @return
     */
    private String switchPermission(String subPermission) {
        if (subPermission.equals(QueryType.substation.toString())) {
            return QueryType.company.toString();
        } else if (subPermission.equals(QueryType.member.toString())) {
            return QueryType.substation.toString();
        } else if (subPermission.equals(QueryType.distributor.toString())) {
            return QueryType.member.toString();
        } else if (subPermission.equals(QueryType.someone.toString())) {
            return QueryType.distributor.toString();
        } else {
            return QueryType.someone.toString();
        }
    }

    private void showView(JsonPage jsonPage) {
        List<JSONObject> list = jsonPage.getJsonList();
        if (jsonPage.isHasPrevPage() || jsonPage.isHasNextPage()) {
            if (jsonPage.isHasPrevPage()) {
                listView.enablePrevButton();
            } else {
                listView.disablePrevButton();
            }
            if (jsonPage.isHasNextPage()) {
                listView.enableNextButton();
            } else {
                listView.disableNextButton();
            }
        } else {
            listView.hidePrevButton();
            listView.hideNextButton();
        }
        temporaryList.clear();
        for (int i = 0; i < list.size(); i++) {
            temporaryList.add(list.get(i));
        }
        myAdapter.notifyDataSetChanged();
    }


    class MyAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public MyAdapter(Context mContext) {
            super();
            this.inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return temporaryList.size();
        }

        @Override
        public Object getItem(int position) {
            return temporaryList.get(position);
        }

         @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DistributionHolder holder;
            if (convertView == null) {
                holder = new DistributionHolder();
                convertView = inflater.inflate(R.layout.distribution_list_item, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(holder);
            } else {
                holder = (DistributionHolder) convertView.getTag();
            }
            holder.id = temporaryList.get(position).optString("id");
            holder.name = temporaryList.get(position).optString("name");
            holder.phone = temporaryList.get(position).optString("phone");
            holder.total = temporaryList.get(position).optString("total");
            holder.queryType = temporaryList.get(position).optString("queryType");
            if (holder.queryType.equals(QueryType.someone.toString())) {//选择某个分销商后，不展示图标，显示电话号码
                holder.iv_icon.setVisibility(View.GONE);
                holder.tv_phone.setVisibility(View.VISIBLE);
            } else {//其他情况显示图标，不显示号码
                holder.iv_icon.setVisibility(View.VISIBLE);
                holder.tv_phone.setVisibility(View.GONE);
            }
            if(holder.queryType.equals(QueryType.someone.toString())){
                holder.tv_name.setText(holder.name);
            }else{
                String red = "(" + holder.total + ")";
                holder.tv_name.setText(Html.fromHtml(holder.name +"<font color=\"#ff0000\">"+red+"</font>"));
            }

            holder.tv_phone.setText(holder.phone);

            queryType = holder.queryType;
            return convertView;
        }


    }

    private class DistributionHolder {
        TextView tv_name, tv_phone;
        ImageView iv_icon;
        String id, name, phone, total, queryType;
    }

    private String currentPage = "";//当前页

    private static boolean isReutrn(String permission){
        return false;
    }

    /**
     * type=first第一次请求,company分公司，substation支局，member支局成员，distributor分销商下属，someone具体某人
     * 重写返回事件，执行顺序：分公司->支局->分销商->分销商下属
     */
    @Override
    public void onBackPressed() {
        temporaryList.clear();
        if (currentPage.equals(QueryType.company.toString()) || currentPage.equals(permission)) {
            this.finish();
        } else if (currentPage.equals(QueryType.substation.toString())) {
            temporaryList.addAll(companyList);
            currentPage = QueryType.company.toString();
            tv_list_title.setText(companyTitle);
            tv_number.setText(companyNumber);
        }else if (currentPage.equals(QueryType.member.toString())) {
            temporaryList.addAll(substationList);
            currentPage = QueryType.substation.toString();
            tv_list_title.setText(substationTitle);
            tv_number.setText(substationNumber);
        }else if (currentPage.equals(QueryType.distributor.toString())) {
            temporaryList.addAll(memberList);
            currentPage = QueryType.member.toString();
            tv_list_title.setText(memberTitle);
            tv_number.setText(memberNumber);
        }else if(currentPage.equals(QueryType.someone.toString())){
            temporaryList.addAll(distributorList);
            currentPage = QueryType.distributor.toString();
            tv_list_title.setText(distributorTitle);
            tv_number.setText(distributorNumber);
            listView.setVisibility(View.VISIBLE);
            ll_persion_detail.setVisibility(View.GONE);
        }
        myAdapter.notifyDataSetChanged();
    }

    public void doBack(View v) {
        onBackPressed();
    }

    /**
     * 选择查询类型
     *
     * @param view
     */
    public void showTypeList(View view) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, QUERYKEY);
        new AlertDialog.Builder(mContext).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        queryKey = QueryKey.name.toString();
                        word.setVisibility(View.VISIBLE);
                        word.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        word.setHint("请输入姓名");
                        bt_select.setVisibility(View.GONE);
                        break;
                    case 1:
                        queryKey = QueryKey.phone.toString();
                        word.setVisibility(View.VISIBLE);
                        word.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                        word.setHint("请输入电话");
                        bt_select.setVisibility(View.GONE);
                        break;
                    case 2:
                        queryKey = QueryKey.category.toString();
                        word.setVisibility(View.GONE);
                        bt_select.setVisibility(View.VISIBLE);
                        break;
                }
                bt_item_name.setText(QUERYKEY[which]);
                dialog.dismiss();
            }
        }).show();
    }

    private static enum QueryTypeEnum  {
        aa,BB,CC;
    }

    /**
     * 点击搜索
     *
     * @param view
     */
    public void doSearch(View view) {
        pageindex = 1;
        queryType = QueryType.search.toString();//查询
        int len = word.getText().toString().trim().length();
        if (queryKey.equals(QueryKey.name.toString())) {
            if(len == 0){
                Toast.makeText(mContext, "请输入姓名", Toast.LENGTH_SHORT).show();
                return;
            }else{
                queryWord = word.getText().toString();
            }

        } else if (queryKey.equals(QueryKey.phone.toString()) ) {
            if(len == 0){
                Toast.makeText(mContext, "请输入手机号码", Toast.LENGTH_SHORT).show();
                return;
            }else{
                queryWord = word.getText().toString();
            }

        } else if (queryKey.equals(QueryKey.category.toString())) {
            if(categoryList.size() == 0){
                Toast.makeText(mContext, "无渠道类别，不能查询", Toast.LENGTH_SHORT).show();
                return;
            }
            if(queryWord.length() == 0){
                Toast.makeText(mContext, "请选择渠道类别", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        requestListData();
    }

    /**
     * 选择渠道
     *
     * @param view
     */
    public void doDistri(View view) {
        List<String> list = new ArrayList<String>();
        for (JSONObject json : categoryList) {
            list.add(json.optString("name"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        new AlertDialog.Builder(mContext).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                queryWord = categoryList.get(which).optString("id");
                bt_select.setText(categoryList.get(which).optString("name"));
                dialog.dismiss();
            }
        }).show();
    }

}
