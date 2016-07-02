package com.crunii.android.fxpt.combinationGoodsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crunii.android.fxpt.R;

/**
 * Created by speedingsnail on 15/11/18.
 */
public class CombinGoods extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_goods);


        listView = (ListView) findViewById(R.id.combinGoods_listVIew);

        listView.setAdapter(new MylistViewAdapter());

    }


    class MylistViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final View listview=View.inflate(getApplicationContext(),R.layout.activity_combin_goods_listview,null);

            ImageView imageView= (ImageView) listview.findViewById(R.id.combinGoods_imageView);
            TextView goodsName = (TextView) listview.findViewById(R.id.combinGoods_goodsName);
            TextView goodsPrice = (TextView) listview.findViewById(R.id.combinGoods_goodsPrice);
            ImageButton imageButton = (ImageButton) listview.findViewById(R.id.combinGoods_buy);

            imageView.setImageResource(R.drawable.ct_test_picture);
            goodsName.setText("小米手机");
            goodsPrice.setText("￥:5000");

            imageButton.setVisibility(View.GONE);




            return listview;
        }
    }
}
