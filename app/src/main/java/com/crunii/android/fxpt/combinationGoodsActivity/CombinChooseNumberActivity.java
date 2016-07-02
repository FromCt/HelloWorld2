package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.business.JsonPage;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter;
import com.crunii.android.fxpt.view.AppleListViewReverseFooter.OnRefreshListener;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombinChooseNumberActivity extends BaseActivity {
    ;
    String areaId = "";
    String section = "";
    String query = "";
    String databaseId = "";

    AppleListViewReverseFooter listView;
    List<JSONObject> ItemCommonHolder = new ArrayList<JSONObject>();
    ListAdapter lvAdapter;

    int pageindex = 1;

    ArrayList<ItemCommon> areaList = new ArrayList<ItemCommon>();
    ItemCommon areaItemCommon = new ItemCommon("", "null");
    ArrayList<String> titleList = new ArrayList<String>();

    ArrayList<ItemCommon> sectionList = new ArrayList<ItemCommon>();
    ItemCommon sectionItemCommon = new ItemCommon("", "null");
    ArrayList<String> sectionTitle = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number_combin);
        Bundle bundle = getIntent().getExtras();
        ChoooseNumberDemo choooseNumberDemo =
                (ChoooseNumberDemo) bundle.getSerializable("choooseNumberDemo");
        databaseId = choooseNumberDemo.databaseId;

        if(choooseNumberDemo.areaList != null && choooseNumberDemo.areaList.size() > 0){
            areaList = choooseNumberDemo.areaList;

            for(int i=0;i<areaList.size();i++) {
                titleList.add(areaList.get(i).name);
            }

            //区域选择第一个默认为区域列表的第一个
            areaItemCommon = new ItemCommon(choooseNumberDemo.areaList.get(0).id,choooseNumberDemo.areaList.get(0).name);
        }
        ArrayList<String> list = choooseNumberDemo.sectionList;
        if(list != null && list.size() > 0){
            //号段选择第一个默认不选，也即"全部"
            sectionItemCommon = new ItemCommon("", "全部");
            sectionList.add(sectionItemCommon);
            for (int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                sectionList.add(new ItemCommon(string, string));
            }

            for(int i=0;i<sectionList.size();i++) {
                sectionTitle.add(sectionList.get(i).name);
            }
        }

        listView = (AppleListViewReverseFooter) findViewById(R.id.listView);
        lvAdapter = new ListAdapter(getBaseContext());
        listView.setAdapter(lvAdapter);
        listView.setonRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                pageindex = 1;
                refresh(true);
            }
        });
        listView.setPrevButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageindex > 1) {
                    pageindex--;
                }
                refresh(true);
            }
        });
        listView.setNextButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pageindex++;
                refresh(true);
            }
        });
        listView.hidePrevButton();
        listView.hideNextButton();

        doSearch(null);
    }

    public void doBack(View v) {
        onBackPressed();
    }

    public void showAreaList(View v) {
        ArrayAdapter<String> ItemCommonAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, titleList);

        new AlertDialog.Builder(this).setSingleChoiceItems(ItemCommonAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int position) {
                areaItemCommon = areaList.get(position);
                ((Button) findViewById(R.id.btn_area)).setText(areaItemCommon.name);
                dlg.dismiss();
            }
        }).show();
    }

    public void showSectionList(View v) {
        ArrayAdapter<String> ItemCommonAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, sectionTitle);

        new AlertDialog.Builder(this).setSingleChoiceItems(ItemCommonAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int position) {
                sectionItemCommon = sectionList.get(position);
                ((Button) findViewById(R.id.btn_section)).setText(sectionItemCommon.name);
                dlg.dismiss();
            }
        }).show();
    }

    public void doSearch(View v) {
        areaId = areaItemCommon.id;
        section = sectionItemCommon.id;
        query = ((EditText) findViewById(R.id.query)).getText().toString();

        pageindex = 1;
        refresh(true);
    }


    private void refresh(final boolean showProgressDialog) {

        new BaseTask<String, String, JsonPage>(this) {
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
                return CRApplication.getApp().choosenumber(getApplicationContext(), pageindex, areaId, section, query, databaseId);
            }

            @Override
            protected void onSuccess(JsonPage result) {

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

                ItemCommonHolder.clear();
                for (int i = 0; i < orderList.size(); i++) {
                    ItemCommonHolder.add(orderList.get(i));
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
            return ItemCommonHolder.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder;

            if (view == null) {
                view = inflater.inflate(R.layout.choose_number_list_item, null);
                viewHolder = new ViewHolder();

                viewHolder.text_number = (TextView) view.findViewById(R.id.number);
                viewHolder.text_prepaid = (TextView) view.findViewById(R.id.prepaid);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.position = position;

            showListItemCommon(ItemCommonHolder.get(position), viewHolder);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder viewHolder = (ViewHolder) v.getTag();
                    int position = viewHolder.position;

                    String number = ItemCommonHolder.get(position).optString("number");
                    String tele_use = ItemCommonHolder.get(position).optString("tele_use");
                    String prepaid = ItemCommonHolder.get(position).optString("prepaid");

                    Intent i = new Intent();
                    i.putExtra("number", number);
                    i.putExtra("tele_use", tele_use);
                    i.putExtra("prepaid", prepaid);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });

            return view;
        }
    }

    class ViewHolder {
        int position;

        TextView text_number, text_prepaid;
    }

    private void showListItemCommon(JSONObject json, ViewHolder viewHolder) {

        String number = json.optString("number");
        String prepaid = json.optString("prepaid");

        viewHolder.text_number.setText(number);
        viewHolder.text_prepaid.setText(prepaid);

    }

    public void doItemCommonClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        int position = viewHolder.position;

        String number = ItemCommonHolder.get(position).optString("number");
        String tele_use = ItemCommonHolder.get(position).optString("tele_use");
        String prepaid = ItemCommonHolder.get(position).optString("prepaid");

        Intent i = new Intent();
        i.putExtra("number", number);
        i.putExtra("tele_use", tele_use);
        i.putExtra("prepaid", prepaid);
        setResult(RESULT_OK, i);
        finish();
    }

}
