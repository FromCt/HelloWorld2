package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by speedingsnail on 16/1/15.
 */
public class AddDistributeOrder extends Activity {

    private EditText nameV, phoneV, addressV, intentionV, receiverIdV;
    private TextView receiver_name, tv_county_name;
    private EditText shopDiscount;//到店优惠
    private String phone;//客户手机
    private Context mContext;
    private LinearLayout ll_county_name;
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add_distribute_add);


        ll_county_name = (LinearLayout) findViewById(R.id.ll_county_name);
        nameV = (EditText) findViewById(R.id.name);
        phoneV = (EditText) findViewById(R.id.phone);
        addressV = (EditText) findViewById(R.id.address);
        intentionV = (EditText) findViewById(R.id.intention);
        receiverIdV = (EditText) findViewById(R.id.receiverId);
        receiver_name = (TextView) findViewById(R.id.receiver_name);
        tv_county_name = (TextView) findViewById(R.id.tv_county_name);
        receiverIdV.addTextChangedListener(new MyTextWatcher());
        shopDiscount = (EditText) findViewById(R.id.shopDiscount);

        String id = getIntent().getExtras().getString("id");
        String phone = getIntent().getExtras().getString("phone");
        String name = getIntent().getExtras().getString("name");
        String county_name = getIntent().getExtras().getString("county_name");

        receiverIdV.setText(phone);
        receiver_name.setText(name);

        final Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("phone", phone);

        map.put("name", name);
        map.put("county_name", county_name);

        if (name.length() > 3) {
            receiver_name.setTextSize(11);
        } else {
            receiver_name.setTextSize(16);
        }
        if (county_name != null && !county_name.equals("")) {
            ll_county_name.setVisibility(View.VISIBLE);
            tv_county_name.setText(county_name);
        } else {
            ll_county_name.setVisibility(View.GONE);
        }

        receiverIdV.setTag(map);
    }


    public void doSearchReveiver(View view) {
        String phone = receiverIdV.getText().toString().trim();
        if (phone.length() == 0) {
            Toast.makeText(this, "请输入号码模糊查询", Toast.LENGTH_SHORT).show();
            return;
        }
        final Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        HttpTool.sendPost(this, Constant.CTX_PATH + "search_recevier", params, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                Boolean isSuccess = (Boolean) record.get("isSuccess");

                if (!isSuccess) {
                    String failDesc = (String) record.get("failDesc");
                    Toast.makeText(AddDistributeOrder.this, failDesc, Toast.LENGTH_SHORT).show();
                } else {
                    list = (List<Map<String, String>>) record.get("list");
                    new FXDialog(mContext, R.layout.activity_receiver_list_item, "请选择接单人", list) {

                        @Override
                        public void setItemData(int position, View convertView, ViewGroup parent) {
                            final TextView name = (TextView) convertView.findViewById(R.id.name);
                            final TextView phone = (TextView) convertView.findViewById(R.id.phone);
                            final TextView tv_county_name = (TextView) convertView.findViewById(R.id.tv_county_name);
                            name.setText((String) list.get(position).get("name"));
                            phone.setText((String) list.get(position).get("phone"));
                            tv_county_name.setText(list.get(position).get("county_name"));
                            if (name.length() > 3) {
                                receiver_name.setTextSize(12);
                            } else {
                                receiver_name.setTextSize(16);
                            }
                            convertView.setTag(list.get(position));
                        }

                        @Override
                        public void setDialogItemOnClickListener(View itemView) {
                            Map<String, String> map = (Map<String, String>) itemView.getTag();
                            receiverIdV.setText(map.get("phone"));
                            receiver_name.setText(map.get("name"));
                            tv_county_name.setText(map.get("county_name"));
                            receiverIdV.setTag(map);
                        }
                    }.show();
                }
            }
        });
    }

    public void doSubmit(final View view) {
        String customerName = nameV.getText().toString().trim();
        phone = phoneV.getText().toString().trim();
        //   String address = addressV.getText().toString().trim();
        String intention = intentionV.getText().toString().trim();

        if (customerName.length() == 0) {
            Toast.makeText(this, "请输入客户姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone != null && phone.length() != 0 && phone.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号=" + phone, Toast.LENGTH_SHORT).show();
            return;
        }
        /*if (address.length() == 0) {
            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (intention.length() == 0) {
            Toast.makeText(this, "请输入需求", Toast.LENGTH_SHORT).show();
            return;
        }
        if (receiverIdV.getTag() == null) {
            Toast.makeText(this, "请重新查询接单人", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shopDiscount.getText().toString().trim().length() > 20) {
            Toast.makeText(this, "到店优惠不能超过20个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        String receiverId = (String) ((Map<String, Object>) receiverIdV.getTag()).get("id");
        final Map<String, String> params = new HashMap<>();
        params.put("customerName", customerName);
        params.put("phone", phone);
        //  params.put("address", address);
        params.put("intention", intention);
        params.put("receiverId", receiverId);
        params.put("shopDiscount", shopDiscount.getText().toString().trim());
        HttpTool.sendPost(this, Constant.CTX_PATH + "distributeOrderSave", params, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                boolean isSuccess = (boolean) record.get("isSuccess");
                final String bc_id= (String) record.get("bc_id");
                if (!isSuccess) {
                    String fail = (String) record.get("fail");
                    MyToast.showToast(AddDistributeOrder.this, fail, Toast.LENGTH_SHORT);
                } else {
                    if (phone.equals("") || shopDiscount.getText().toString().equals("")) {
                        MyToast.showToast(AddDistributeOrder.this, "保存成功", Toast.LENGTH_SHORT);
                        onBackPressed();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("提示")
                                .setMessage("保存成功，是否发送优惠码短信给用户" + phone)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doSend(bc_id);
                                    }
                                }).show();
                    }

                }
            }
        });
    }

    //发送优惠码给用户
    public void doSend(String bc_id) {
        Map<String, String> params = new HashMap<>();
        params.put("bc_id", bc_id);
        HttpTool.sendPost(this, Constant.CTX_PATH + "sendCouponCode", params, new HttpPostProp(){
            @Override
            public void dealRecord(Map record) {
                super.dealRecord(record);
                boolean isSuccess = (boolean) record.get("isSuccess");
                if(isSuccess){
                    MyToast.showToast(AddDistributeOrder.this, "优惠码发送成功", Toast.LENGTH_SHORT);
                }else {
                    String fail = (String) record.get("fail");
                    MyToast.showToast(AddDistributeOrder.this, fail, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void doBack(View v) {
        onBackPressed();
    }

    private boolean confirm(String value, String nullDesc) {
        if (value.length() == 0) {
            Toast.makeText(this, nullDesc, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0 && s.length() < 11) {
                receiver_name.setText("");
                tv_county_name.setText("");
                receiverIdV.setTag(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
