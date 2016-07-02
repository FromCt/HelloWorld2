package com.crunii.android.fxpt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.fxpt.R;

import java.util.List;

/**
 * Created by speedingsnail on 16/1/19.
 */
public abstract class FXDialog extends AlertDialog{


    public FXDialog(Context context) {
        super(context);
    }

    public FXDialog(Context context,int itemResourceId,String title,List list) {
        super(context);
        init(context, itemResourceId, title, list);
    }

    private void init(Context context,int itemResourceId,String title,List list){
        View view = View.inflate(context,R.layout.activity_receiver_list,null);
        TextView titleView  = (TextView)view.findViewById(R.id.title);
        titleView.setText(title);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        MyAdapter myAdapter = new MyAdapter(context,list,itemResourceId) {
            @Override
            public void initView(int position, View convertView, ViewGroup parent) {
                setItemData(position,convertView,parent);
            }

            @Override
            public void setItemOnClickListener(View itemView) {
                setDialogItemOnClickListener(itemView);
                dismiss();
            }
        };
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        setView(view,0,0,0,0);
    }

    public abstract void setItemData(int position, View convertView, ViewGroup parent);

    public abstract void setDialogItemOnClickListener(View itemView);


}
