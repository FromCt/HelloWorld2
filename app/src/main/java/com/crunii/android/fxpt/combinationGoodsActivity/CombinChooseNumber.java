package com.crunii.android.fxpt.combinationGoodsActivity;






import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseListViewAdapter;

import java.util.ArrayList;

/**
 * Created by ct on 2015/11/23.
 */
public class CombinChooseNumber extends Activity {
    private GridView gridView;
    private Button submit;
    private ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_choose_number);

        gridView = (GridView) findViewById(R.id.combin_choose_number_gridView);
        submit = (Button) findViewById(R.id.combin_choose_number_button);

        for(int i=0;i<10;i++) {
            list.add("123456789" + i);
        }

        MyAdapter adapter = new MyAdapter(this, R.layout.activity_combin_choose_number_item);
        gridView.setAdapter(adapter);
        adapter.refreshData(list);

    }

    class MyAdapter extends BaseListViewAdapter {

        public MyAdapter(Activity activity, Integer layoutId) {
            super(activity, layoutId);
        }

        @Override
        public void getBaseView(Object data, HolderView holderView, ViewGroup parent,int position) {
            TextView number= (TextView) holderView.findViewById(R.id.combin_cm_number);
            number.setText((String)data);

        }
    }
}


