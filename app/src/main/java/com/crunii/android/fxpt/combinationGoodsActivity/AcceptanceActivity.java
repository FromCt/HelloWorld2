package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.BaseListViewAdapter;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.Result;
import com.crunii.android.fxpt.base.SetValue;
import com.crunii.android.fxpt.util.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ct on 2015/11/19.
 * 受理。
 */
public class AcceptanceActivity extends BaseActivity {
    private MyAdapter adapter;
    private EditText serachEdit;
    private ImageButton searchButton;
    String goodsCode = "",templateId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goodsCode = getIntent().getExtras().getString("goodsCode");
        templateId = getIntent().getExtras().getString("templateId");
        setContentView(R.layout.activity_combin_acceptance);
        ListView listVivew = (ListView) findViewById(R.id.combin_accept_listView);
        serachEdit = (EditText) findViewById(R.id.combin_serachEdit);
        searchButton = (ImageButton) findViewById(R.id.combin_serachButton);

        // listVivew.setAdapter(new MylistViewAdapter());

        adapter = new MyAdapter(this, R.layout.activity_combin_acceptance_item);

        listVivew.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = serachEdit.getText().toString().trim();
                if (str != null && str.length() > 1) {
                    HashMap<String, String> postparams = new HashMap<String, String>();
                    SetValue.setPostparams(postparams, str, "keyWord");
                    sendPost(Constant.CTX_PATH + "cbg_searchSellerList", postparams, new HttpPostProp() {
                        @Override
                        public void dealRecord(Map record) {
                            boolean isSuccess = (boolean) record.get("isSuccess");
                            if(!isSuccess){
                                String failDesc = (String)record.get("failDesc");
                                Toast.makeText(AcceptanceActivity.this,failDesc,Toast.LENGTH_SHORT).show();
                            }
                            List list = (List) record.get("sellerList");
                            adapter.refreshData(list);
                        }
                    });

                } else {
                    Toast.makeText(AcceptanceActivity.this, "请输入两个以上中文字符", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    class MyAdapter extends BaseListViewAdapter {

        public MyAdapter(Activity activity, Integer layoutId) {
            super(activity, layoutId);
        }

        @Override
        public void getBaseView(Object data, HolderView holderView, ViewGroup parent,int positon) {
            final Map map = (Map) data;

            TextView name = (TextView) holderView.findViewById(R.id.combin_accept_name);
            TextView salePlace = (TextView) holderView.findViewById(R.id.combin_accept_salePlace);
            TextView crmId = (TextView) holderView.findViewById(R.id.combin_accept_CRM);
            Button hanleButton = (Button) holderView.findViewById(R.id.combin_accept_handle);

            SetValue.setText(name, (String) map.get("sellerName"));
            SetValue.setText(salePlace, (String) map.get("belongSellPoint"));
            SetValue.setText(crmId, (String) map.get("CRMId"));

            hanleButton.setOnClickListener(new View.OnClickListener() {//受理
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("CRMId", (String) map.get("CRMId"));
                    intent.putExtra("goodsCode", goodsCode);
                    if(templateId.equals(CombinGoodsBroadbandActivity.templateId)){
                        intent.setClass(AcceptanceActivity.this, CombinGoodsBroadbandActivity.class);
                        startActivity(intent);
                    }else if(templateId.equals(CombinGoodsContractActivity.templateId)){
                        intent.setClass(AcceptanceActivity.this, CombinGoodsContractActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AcceptanceActivity.this,"模版有误，请联系商品发布人员",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }


}
