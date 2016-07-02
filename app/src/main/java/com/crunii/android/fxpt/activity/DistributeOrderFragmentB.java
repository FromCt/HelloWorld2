package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.base.HttpTool;
import com.crunii.android.fxpt.util.Constant;
import com.crunii.android.fxpt.view.MyToast;
import com.crunii.android.fxpt.view.PullDownView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by speedingsnail on 16/1/15.
 */
public class DistributeOrderFragmentB extends Fragment implements PullDownView.OnPullDownListener, AdapterView.OnItemClickListener {

    List<Map<String, Object>> list;
    MyAdapter adapter;
    PullDownView mPullDownView;
    String name = "";
        private static final int pagesize = 5;
    private static final int WHAT_DID_LOAD_DATA = 0;
    private static final int WHAT_DID_REFRESH = 1;
    private static final int WHAT_DID_MORE = 2;
    private static final String TYPE = "2";//代表接单。
    private final Page page = new Page();

    private class Page {
        private int canbeHint = 1;            //为1时删除时 显示提示
        private int pageindex = 1;           //当前页码
        private int prePage = 1;            //前一页页码
        private int nextPage = 1;            //后一页页码
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View distributeOrederB = inflater.inflate(R.layout.activity_distribute_order_b, container, false);
        final TextView et_serach = (TextView) distributeOrederB.findViewById(R.id.et_serach);
        final Button btn_search = (Button) distributeOrederB.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_serach.getText().toString().trim();
                refresh(name, 1, WHAT_DID_LOAD_DATA);
            }
        });
        mPullDownView = (PullDownView) distributeOrederB.findViewById(R.id.distributeListView);
        mPullDownView.setOnPullDownListener(this);

        ListView mListView = mPullDownView.getListView();
        mListView.setDivider(null);
        mListView.setDividerHeight(80);
        mListView.setOnItemClickListener(this);
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        mPullDownView.enableAutoFetchMore(true, 1);

        refresh("", 1, WHAT_DID_LOAD_DATA);
        return distributeOrederB;
    }

    public void refresh(String name, int pageindex, final int categray) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("type", TYPE);
        map.put("pageindex", String.valueOf(pageindex));
        map.put("pagesize", String.valueOf(pagesize));
        HttpTool.sendPost(getActivity(), Constant.CTX_PATH + "receiveOrder", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                page.pageindex = (int) record.get("pageindex");           //当前页码
                page.prePage = (int) record.get("prePage");              //前一页页码
                page.nextPage = (int) record.get("nextPage");             //后一页页码
                page.canbeHint = (int) record.get("canbeHint");             //是否显示删除提示

                Log.i("ct_delete", "receiveOrder canbeHint===." + page.canbeHint);
                switch (categray) {
                    case WHAT_DID_LOAD_DATA: {
                        list = (List<Map<String, Object>>) record.get("list");
                        // 诉它数据加载完毕;
                        adapter.notifyDataSetChanged();
                        mPullDownView.notifyDidLoad();
                        break;
                    }
                    case WHAT_DID_REFRESH: {
                        // 告诉它更新完毕
                        list = (List<Map<String, Object>>) record.get("list");
                        adapter.notifyDataSetChanged();
                        mPullDownView.notifyDidRefresh();
                        break;
                    }

                    case WHAT_DID_MORE: {
                        // 告诉它获取更多完毕
                        list.addAll((List<Map<String, Object>>) record.get("list"));
                        adapter.notifyDataSetChanged();
                        mPullDownView.notifyDidMore();
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyAdapter.Holder holder = (MyAdapter.Holder) view.getTag();

    }

    @Override
    public void onRefresh() {
        refresh(name, 1, WHAT_DID_LOAD_DATA);
    }

    public void onRefresh1() {
        refresh(name,page.pageindex , WHAT_DID_REFRESH);
    }

    @Override
    public void onMore() {
        if (page.nextPage == page.pageindex) {
            MyToast.showToast(getActivity(), "客官，没有更多数据了", Toast.LENGTH_LONG);
            mPullDownView.notifyDidMore();
            return;
        }
        refresh(name, page.nextPage, WHAT_DID_MORE);
    }



    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
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
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(getActivity(), R.layout.activity_distribute_receive_order_list_item_b, null);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.intention = (TextView) convertView.findViewById(R.id.tv_intention);
                holder.state = (TextView) convertView.findViewById(R.id.tv_state);
                holder.zhongduanpay = (TextView) convertView.findViewById(R.id.tv_zhongduan_pay);
                holder.yewupay = (TextView) convertView.findViewById(R.id.tv_yewu_pay);
                // holder.pay = (TextView) convertView.findViewById(R.id.tv_pay);
                holder.deleteButton = (Button) convertView.findViewById(R.id.item_delete);
            } else {
                holder = (Holder) convertView.getTag();
            }
            int id = (int) list.get(position).get("id");
            holder.id = id;
            holder.name.setText((String) list.get(position).get("customerName"));
            holder.time.setText((String) list.get(position).get("distributeName"));
            if ( list.get(position).get("goods_money").toString().equals("null")) {
                holder.zhongduanpay.setText("¥ 0");
            }else {
                int goods=Integer.parseInt( list.get(position).get("goods_money").toString());
                String goods_money=String.valueOf(goods / 100);
                holder.zhongduanpay.setText("¥ "+ goods_money);

            }
            if ( list.get(position).get("combo_money").toString().equals("null")) {
                holder.yewupay.setText( "¥ 0");
            }else {
                int combo=Integer.parseInt(list.get(position).get("combo_money").toString());
                String combo_money=String.valueOf(combo/100);
                holder.yewupay.setText( "¥ "+combo_money);
            }
            String intention = (String) list.get(position).get("intention");
            int canbeDelete = (int) list.get(position).get("canbeDelete");
            Log.i("ct_delete", "dcanbeDelete===" + canbeDelete);
            if (canbeDelete == 1) {//为1时表示可以删除该条信息
                Log.i("ct_delete", "holder.button.setVisibility(View.VISIBLE)");
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ct_delete", "delete button onClick...");

                        //设置是否显示提示  1是显示提示。 0表示不在提示。
                        if (page.canbeHint == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("");
                            builder.setMessage("删除后，订单实际还存在，但在订单列表中不再显示，不能操作，不能查询，请谨慎操作。");
                            builder.setPositiveButton("不在提醒", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {//不在提醒 修改 canbeHint

                                    delete(holder.id + "", "0", position);
                                }
                            });

                            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {//直接确定 下次在次提醒
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(holder.id + "", "1", position);
                                }
                            });
                            builder.show();

                        } else {
                            delete(holder.id + "", "1", position);
                        }

                        // 诉它数据加载完毕;
                        // mPullDownView.notifyDidLoad();

                    }
                });
            } else {
                holder.deleteButton.setVisibility(View.GONE);
            }
            if (intention != null && intention.length() > 5) {
                holder.intention.setText(intention.substring(0, 5) + " ......");
            } else {
                holder.intention.setText(intention);
            }
            holder.state.setText((String) list.get(position).get("state"));
            //   holder.pay.setText("¥" + (String) list.get(position).get("pay"));
            convertView.setTag(holder);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ReceiverOrderDetailActivity.class);
                    intent.putExtra("id", holder.id);
                    getActivity().startActivityForResult(intent, 1);
                }
            });

            return convertView;
        }

        private class Holder {
            int id;
            Button deleteButton;
            TextView name;
            TextView time;
            TextView state;
            TextView intention;
            TextView zhongduanpay;
            TextView yewupay;
        }
    }

    public void delete(String id, String canbeHint, final int position) {//删除操作

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", TYPE);//代表派单
        map.put("id", id);//代表派单
        map.put("canbeHint", canbeHint);

        HttpTool.sendPost(getActivity(), Constant.CTX_PATH + "distributeOrderDelete", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                if ((boolean) record.get("isSuccess")) {
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}