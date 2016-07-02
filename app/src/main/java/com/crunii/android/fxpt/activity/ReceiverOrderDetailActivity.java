package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.view.MyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by speedingsnail on 16/1/20.
 */
public class ReceiverOrderDetailActivity extends Activity {


    private List<Map<String, String>> stateList;
    private TextView name, phone, intention, time, receiver_name, shopDiscount,prove,deal_seller;
    private LinearLayout ll_accept_type, ll_terminal_type, ll_goods_money, ll_crm_accept_id, ll_combo_money, ll_submit,ll_yhjcontent;
    private Button state,btnsubmit,btncancle;
    private EditText et_terminaltype, et_goodsmoney, et_crmacceptid, et_combomoney,et_yhj;
    private Spinner sp_accept_type;
    private ArrayAdapter<String> typeadapter;
    private String[] typelist = {"裸机配件", "业务办理"};
    private Context mContext;
    private int id;
    private String  audit_state; //1表示审核状态
    private String localyhj; //本地优惠码
    private static final int LUOJIPEIJIAN = 1;   //裸机配件
    private static final int YEWUBANLI = 2;       //业务办理
    private boolean isProve=false;      //验证码是否正确

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        stateList = new ArrayList<Map<String, String>>();
        setContentView(R.layout.activity_distribute_order_detail_b);
        id = getIntent().getExtras().getInt("id");
        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
        intention = (TextView) findViewById(R.id.intention);
        time = (TextView) findViewById(R.id.time);
        receiver_name = (TextView) findViewById(R.id.receiver_name);
        state = (Button) findViewById(R.id.state);
        prove= (TextView) findViewById(R.id.tv_prove);                            //验证
        deal_seller= (TextView) findViewById(R.id.deal_seller);                  //处理人
        et_yhj= (EditText) findViewById(R.id.et_yhj);                            //优惠码
        ll_yhjcontent= (LinearLayout) findViewById(R.id.ll_yhjcontent);
        ll_accept_type = (LinearLayout) findViewById(R.id.ll_accept_type);     //订单类型
        ll_combo_money = (LinearLayout) findViewById(R.id.combo_moeny);         //业务酬金
        et_combomoney = (EditText) findViewById(R.id.et_combo_moeny);
        ll_crm_accept_id = (LinearLayout) findViewById(R.id.crm_accept_id);     //crm订单
        et_crmacceptid = (EditText) findViewById(R.id.et_crm_accept_id);
        ll_goods_money = (LinearLayout) findViewById(R.id.goods_moeny);         // 终端返利
        et_goodsmoney = (EditText) findViewById(R.id.et_goods_moeny);
        ll_terminal_type = (LinearLayout) findViewById(R.id.terminal_type);    //终端类型
        et_terminaltype = (EditText) findViewById(R.id.et_terminal_type);
        ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
        sp_accept_type = (Spinner) findViewById(R.id.sp_accept_type);
        shopDiscount = (TextView) findViewById(R.id.shopDiscount);
        btnsubmit= (Button) findViewById(R.id.bt_submit);
        btncancle= (Button) findViewById(R.id.bt_cancle);

        Map<String, Object> params = new HashMap<>();
        params.put("id", String.valueOf(id));
        HttpTool.sendPost(this, Constant.CTX_PATH + "receiveOrderDetail", params, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                audit_state = record.get("audit_state").toString();
                stateList = (List<Map<String, String>>) record.get("stateList");
                boolean isEdit= (boolean) record.get("isEdit");
                 Map<String, Object> stateMap = (Map<String, Object>) record.get("state");
                String desc = (String) stateMap.get("desc");
                localyhj=  record.get("coupon_code").toString();
                if (localyhj.equals("")||localyhj.equals("null")){
                    ll_yhjcontent.setVisibility(View.GONE);
                }
                deal_seller.setText((String) record.get(" deal_seller_name"));
                if (desc != null) {
                    boolean isShow = (boolean) stateMap.get("isShow");
                    if (isShow) {
                        if (!localyhj.equals("null")) {
                            et_yhj.setText(localyhj);
                        }
                        ll_accept_type.setVisibility(View.VISIBLE);
                        int accepttype = (int) record.get("accept_type");   //1表示裸机配件，2表示业务办理
                        if (accepttype == LUOJIPEIJIAN) {
                            ll_terminal_type.setVisibility(View.VISIBLE);
                            ll_goods_money.setVisibility(View.VISIBLE);
                            int goods = Integer.parseInt(record.get("goods_money").toString());
                            String goods_money = String.valueOf(goods / 100);
                            et_terminaltype.setText((String) record.get("terminal_type"));
                            et_goodsmoney.setText(goods_money);
                            spinnertype();
                            sp_accept_type.setSelection(0);
                        } else {
                            ll_crm_accept_id.setVisibility(View.VISIBLE);
                            ll_combo_money.setVisibility(View.VISIBLE);
                            ll_goods_money.setVisibility(View.VISIBLE);
                            ll_terminal_type.setVisibility(View.VISIBLE);
                            et_terminaltype.setText((String) record.get("terminal_type"));
                            et_crmacceptid.setText(record.get("crm_accept_id").toString());
                            int goods = Integer.parseInt(record.get("goods_money").toString());
                            int combo = Integer.parseInt(record.get("combo_money").toString());
                            String goods_money = String.valueOf(goods / 100);
                            String combo_money = String.valueOf(combo / 100);
                            et_goodsmoney.setText(goods_money);
                            et_combomoney.setText(combo_money);
                            spinnertype();
                            sp_accept_type.setSelection(1);

                        }

                    } else {
                        ll_accept_type.setVisibility(View.GONE);
                    }
                    state.setText((String) desc);
                    state.setTag(stateMap);
                }
                name.setText((String) record.get("customerName"));
                phone.setText((String) record.get("phone"));
                intention.setText((String) record.get("intention"));
                time.setText((String) record.get("time"));
                receiver_name.setText((String) record.get("distributeName"));

                if (record.get("discount") != null) {
                    shopDiscount.setText((String) record.get("discount"));
                }
                //1表示表示审核状态 不可编辑
                if (audit_state.equals("1")&&isEdit==false) {
                    state.setEnabled(false);
                    sp_accept_type.setEnabled(false);
                    et_terminaltype.setEnabled(false);
                    et_goodsmoney.setEnabled(false);
                    et_crmacceptid.setEnabled(false);
                    et_combomoney.setEnabled(false);
                    et_yhj.setEnabled(false);
                    prove.setEnabled(false);
                    prove.setTextColor(getResources().getColor(R.color.gray));
                    btnsubmit.setVisibility(View.GONE);
                }
            }
        });
    }

    public void doBack(View v) {
        onBackPressed();
    }
    // 验证优惠码
    public void  yhjProve(View v){
        String yhj=et_yhj.getText().toString();
       if (yhj.equals("")){
           Toast.makeText(this,"请输入优惠码",Toast.LENGTH_LONG).show();
       }else {
            if (yhj.equals(localyhj)){
                Toast.makeText(this,"优惠码正确",Toast.LENGTH_LONG).show();
                isProve=true;
            }else {
                Toast.makeText(this,"优惠码不正确，请确认后重新输入",Toast.LENGTH_LONG).show();
                et_yhj.setText("");
            }
       }

    }

    public void doSelect(final View v) {
        new FXDialog(ReceiverOrderDetailActivity.this, R.layout.activity_distribute_order_detail_b_item, "请选择状态", stateList) {
            @Override
            public void setItemData(int position, View convertView, ViewGroup parent) {
                final TextView desc = (TextView) convertView.findViewById(R.id.text);
                desc.setText(stateList.get(position).get("desc"));
                convertView.setTag(stateList.get(position));
            }

            @Override
            public void setDialogItemOnClickListener(View itemView) {
                Map<String, Object> map = (Map<String, Object>) itemView.getTag();
                String desc = (String) map.get("desc");
                boolean isShow = (Boolean) map.get("isShow");
                if (isShow) {
                    ll_accept_type.setVisibility(View.VISIBLE);
                    spinnertype();

                } else {
                    ll_accept_type.setVisibility(View.GONE);
                }
                state.setText(desc);
                state.setTag(map);
            }
        }.show();

    }

    public void doCancle(View v) {
        onBackPressed();
    }

    public void spinnertype() {
        typeadapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_layout, typelist);
        sp_accept_type.setAdapter(typeadapter);
        //注册事件
        sp_accept_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (typelist[position].equals("裸机配件")) {
                    ll_crm_accept_id.setVisibility(View.GONE);
                    ll_combo_money.setVisibility(View.GONE);
                    ll_goods_money.setVisibility(View.VISIBLE);
                    ll_terminal_type.setVisibility(View.VISIBLE);
                    findViewById(R.id.xinghao3).setVisibility(View.VISIBLE);
                    findViewById(R.id.xinghao4).setVisibility(View.VISIBLE);

                } else {
                    ll_crm_accept_id.setVisibility(View.VISIBLE);
                    ll_combo_money.setVisibility(View.VISIBLE);
                    ll_goods_money.setVisibility(View.VISIBLE);
                    ll_terminal_type.setVisibility(View.VISIBLE);
                    findViewById(R.id.xinghao3).setVisibility(View.GONE);
                    findViewById(R.id.xinghao4).setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(mContext, "没有改变的处理", Toast.LENGTH_LONG).show();
            }

        });
    }
   private void doSure(){
       Map<String, Object> param = new HashMap<>();
       param.put("id", String.valueOf(id));
       HttpTool.sendPost(this, Constant.CTX_PATH + "receiveOrderDetail", param, new HttpPostProp() {
           @Override
           public void dealRecord(Map record) {
               String tibmstate = record.get("tibm_state").toString();
               if (tibmstate.equals("1")) {
                   MyToast.showToast(mContext, "修改失败", Toast.LENGTH_LONG);
               } else {
                   final Map<String, Object> params = new HashMap<>();
                   if (ll_accept_type.getVisibility() == View.VISIBLE) {
                       String accepttype = sp_accept_type.getSelectedItem().toString();
                       String terminaltype = et_terminaltype.getText().toString().trim();
                       String goodsmoney = et_goodsmoney.getText().toString().trim();
                       String crmacceptid = et_crmacceptid.getText().toString().trim();
                       String combomoney = et_combomoney.getText().toString().trim();

                       if (accepttype.equals("裸机配件")) {
                           if (ll_terminal_type.getVisibility() == View.VISIBLE && terminaltype.length() == 0) {
                               Toast.makeText(mContext, "请输入终端类型", Toast.LENGTH_LONG).show();
                               return;
                           }
                           if (ll_goods_money.getVisibility() == View.VISIBLE && goodsmoney.length() == 0) {
                               Toast.makeText(mContext, "请输入终端返利", Toast.LENGTH_LONG).show();
                               return;
                           }
                           params.put("accept_type", String.valueOf(LUOJIPEIJIAN));
                           params.put("terminal_type", terminaltype);
                           params.put("goods_money", String.valueOf(goodsmoney));
                       } else {
                           if (ll_crm_accept_id.getVisibility() == View.VISIBLE && crmacceptid.length() == 0) {
                               Toast.makeText(mContext, "请输入CRM订单号", Toast.LENGTH_LONG).show();
                               return;
                           } else {
                               if (!crmacceptid.substring(0, 3).equals("823") || crmacceptid.length() != 15) {
                                   Toast.makeText(mContext, "请填写以823开头的15位CRM订单号", Toast.LENGTH_LONG).show();
                                   return;
                               }
                           }
                           if (ll_combo_money.getVisibility() == View.VISIBLE && combomoney.length() == 0) {
                               Toast.makeText(mContext, "请输入业务酬金", Toast.LENGTH_LONG).show();
                               return;
                           }

                           params.put("accept_type", String.valueOf(YEWUBANLI));
                           params.put("crm_accept_id", String.valueOf(crmacceptid));
                           params.put("combo_money", String.valueOf(combomoney));
                           params.put("terminal_type", terminaltype);
                           params.put("goods_money", String.valueOf(goodsmoney));
                       }
                   }

                   if (state.getTag() != null) {
                       Map<String, Object> map = (Map<String, Object>) state.getTag();
                       params.put("stateId", map.get("value"));
                   }
                   params.put("id", String.valueOf(id));

                   //  params.put("pay", payTe);
                   HttpTool.sendPost(mContext, Constant.CTX_PATH + "saveReceiverOrederPay", params, new HttpPostProp() {
                       @Override
                       public void dealRecord(Map record) {
                           boolean isSuccess = (boolean) record.get("isSuccess");
                           if (!isSuccess) {
                               String fail = (String) record.get("fail");
                               MyToast.showToast(ReceiverOrderDetailActivity.this, fail, Toast.LENGTH_SHORT);
                           } else {
                               setResult(Activity.RESULT_OK);
                               finish();
                           }
                       }
                   });
               }
           }
       });
   }
    public void doSubmit(View v) {
        if (!et_yhj.getText().toString().equals("")) {
            yhjProve(v);
            if (isProve) {
                doSure();
            }
        }else {
            doSure();
        }


    }
}
