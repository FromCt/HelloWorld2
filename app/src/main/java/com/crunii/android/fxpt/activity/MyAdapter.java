package com.crunii.android.fxpt.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by speedingsnail on 16/1/19.
 */
public abstract class MyAdapter extends BaseAdapter {

    public MyAdapter(Context context,List list,int itemResourceId) {
        this.context = context;
        this.list = list;
        this.resourceId = itemResourceId;
    }

    private Context context;
    private List list;
    private int resourceId;

    public void setList(List list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, resourceId,null);
        }
        initView(position,convertView,parent);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemOnClickListener(v);
            }
        });
        return convertView;
    }

    public abstract void initView(int position, View convertView, ViewGroup parent);

    public abstract void setItemOnClickListener(View itemView);
}
