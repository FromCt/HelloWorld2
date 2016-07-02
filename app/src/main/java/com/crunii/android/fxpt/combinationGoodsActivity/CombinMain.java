package com.crunii.android.fxpt.combinationGoodsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.activity.ProductDetailBatch;
import com.crunii.android.fxpt.activity.ProductDetailBroadband;
import com.crunii.android.fxpt.activity.ProductDetailContract;
import com.crunii.android.fxpt.activity.ProductDetailRechargeableCard;
import com.crunii.android.fxpt.activity.ProductDetailTerminal;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.BaseListViewAdapter;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.SetValue;
import com.crunii.android.fxpt.base.ViewEvent;
import com.crunii.android.fxpt.base.ViewEventType;
import com.crunii.android.fxpt.util.Constant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by speedingsnail on 15/11/18.
 */
public class CombinMain extends BaseActivity {

    private int i = 0;
    private String wang = "wangch";
    private ListView listView;


    private BaseListViewAdapter<Map> myAdapter = new BaseListViewAdapter<Map>(this, R.layout.activity_combin_goods_listview) {

        @Override
        public void getBaseView(final Map data, HolderView view, ViewGroup viewGroup,int position) {
            final ImageView imageView = (ImageView) view.findViewById(R.id.combinGoods_imageView);
            TextView goodsName = (TextView) view.findViewById(R.id.combinGoods_goodsName);
            TextView goodsPrice = (TextView) view.findViewById(R.id.combinGoods_goodsPrice);
            final ImageView imageButton = (ImageView) view.findViewById(R.id.combinGoods_buy);
            final LinearLayout listViewLayout = (LinearLayout) view.findViewById(R.id.combin_goods_listview_layout);

            String imageUrl=(String) data.get("imageUrl");
            if(imageUrl==null||imageUrl.equals("")){
                imageView.setImageResource(R.drawable.ct_test_picture);
            }else{
                getImg(imageUrl,imageView );
            }

            SetValue.setText(goodsName, (String) data.get("shellName"));
            SetValue.setText(goodsPrice, "￥:" + (String) data.get("price"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listViewLayout.getVisibility() == View.GONE) {
                        listViewLayout.setVisibility(View.VISIBLE);
                        //
                        HashMap<String, String> postparams = new HashMap<String, String>();
                        //postparams.put("shellCode", (String)data.get("shellCode"));
                        SetValue.setPostparams(postparams, (String) data.get("shellCode"), "shellCode");
                        sendPost(Constant.CTX_PATH + "cbg_providerList", postparams, new HttpPostProp() {
                            @Override
                            public void dealRecord(Map record) {
                                List list = (List) record.get("providerList");
                                for (int i = 0; i < list.size(); i++) {//初始化商城个数。设置购买
                                    View itemView = View.inflate(getApplicationContext(), R.layout.activity_combin_goods_content, null);
                                    Button button = (Button) itemView.findViewById(R.id.combin_goods_railbuy);
                                    TextView companyName = (TextView) itemView.findViewById(R.id.combin_goods_company);

                                    final Map map = (Map) list.get(i);

                                    companyName.setText((String) map.get("goodsName"));
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {//购买跳转
                                            final boolean isSupportThrow = (boolean) map.get("isSupportThrow");
                                            String goodsCode = (String) map.get("goodsCode");
                                            String templateId = (String) map.get("templateId");
                                            if(Constant.TEST_FLAG)
                                            Toast.makeText(CombinMain.this,"isSupportThrow = "+isSupportThrow+ ",templateId ="+ templateId,Toast.LENGTH_SHORT).show();
                                            if (isSupportThrow) {//支持甩单
                                                if (templateId.equals("1001") || templateId.equals("1008")) {
                                                    Intent intent = new Intent(CombinMain.this, AcceptanceActivity.class);
                                                    intent.putExtra("goodsCode", goodsCode);
                                                    intent.putExtra("templateId", templateId);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(CombinMain.this, "目前甩单商品只支持合约和固宽，其他模版暂不支持，当前模版："+ templateId , Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                            } else {
                                                Intent i = new Intent();
                                                i.putExtra("goodsCode", (String) map.get("goodsCode"));
                                                if (templateId.equals(ProductDetailBroadband.templateId)) { //固话/宽带售卖模板
                                                    i.setClass(CombinMain.this, ProductDetailBroadband.class);
                                                } else if (templateId.equals(ProductDetailTerminal.templateId)) { //终端销售模板
                                                    i.setClass(CombinMain.this, ProductDetailTerminal.class);
                                                } else if (templateId.equals(ProductDetailContract.templateId)) { //合约机模板
                                                    i.setClass(CombinMain.this, ProductDetailContract.class);
                                                } else if (templateId.equals(ProductDetailBatch.templateId)) { //批量售卖模板
                                                    i.setClass(CombinMain.this, ProductDetailBatch.class);
                                                } else if (templateId.equals(ProductDetailRechargeableCard.templateId)) { //批量售卖模板
                                                    i.setClass(CombinMain.this, ProductDetailRechargeableCard.class);
                                                } else {
                                                    Toast.makeText(CombinMain.this, "Internal Error: unsupported template", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                startActivity(i);
                                            }

                                        }
                                    });
                                    listViewLayout.addView(itemView);
                                }
                            }
                        });

                        imageButton.setImageResource(R.drawable.combin_buy2_background);
                        listViewLayout.removeAllViews();

                    } else {
                        listViewLayout.setVisibility(View.GONE);
                        imageButton.setImageResource(R.drawable.combin_buy1_background);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_goods);
        listView = (ListView) findViewById(R.id.combinGoods_listVIew);
        listView.setAdapter(myAdapter);
        searchClick(null);
    }


    @ViewEvent(id = R.id.combin_serachButton, eventType = {ViewEventType.CLICK})
    public void searchClick(View v) {
        HashMap params = getParams();
        String tabId = (String) params.get("tabId");
        TextView tv = (TextView) findViewById(R.id.combin_serachEdit);
        HashMap<String, String> postparams = new HashMap<String, String>();
        postparams.put("tabId", tabId.toString());
        postparams.put("keyWord", tv.getText().toString().trim());
        sendPost(Constant.CTX_PATH + "cbg_combinationList", postparams, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                List list = (List) record.get("combinationList");
                myAdapter.refreshData(list);
            }
        });

    }





}
