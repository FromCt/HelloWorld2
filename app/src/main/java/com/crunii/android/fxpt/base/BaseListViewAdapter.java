package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/23.
 */
public abstract class BaseListViewAdapter<DataType> extends BaseAdapter {
    private final List<DataType> datas = new ArrayList<DataType>();
    private Activity activity;
    private Integer layoutId;
    public int selectedPosition = -1;
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public BaseListViewAdapter(Activity activity, Integer layoutId) {
        this.activity = activity;
        this.layoutId = layoutId;
    }

    public void addData(Collection<DataType> rdatas) {
        datas.addAll(rdatas);
        notifyDataSetChanged();
    }

    public void refreshData(Collection<DataType> rdatas) {
        datas.clear();
        addData(rdatas);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity, layoutId, null);
        }
        HolderView holderView = (HolderView) convertView.getTag();
        if (holderView == null) {
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        DataType data = datas.get(position);
        getBaseView(data, holderView, parent,position);
        return convertView;
    }


    public abstract void getBaseView(final DataType data, HolderView holderView, ViewGroup parent,int position);

    public class HolderView {
        private View view;
        private Map<Integer, View> map = new HashMap<Integer, View>();

        public HolderView(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public void setOnClickListener(View.OnClickListener onClickListener){
            this.view.setOnClickListener(onClickListener);
        }

        public View findViewById(Integer id) {
            View v = map.get(id);
            if (v == null) {
                v = view.findViewById(id);
                map.put(id, v);
            }
            return v;
        }

    }

}
