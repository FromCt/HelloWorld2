package com.crunii.android.fxpt.combinationGoodsActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import com.crunii.android.fxpt.R;
import com.crunii.android.fxpt.base.BaseActivity;
import com.crunii.android.fxpt.base.BaseListViewAdapter;
import com.crunii.android.fxpt.base.HttpPostProp;
import com.crunii.android.fxpt.util.Constant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ct on 2015/11/24.
 */
public class CombinTabList extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_tablist);

        GridView gridView = (GridView) findViewById(R.id.combin_gridview);
        final BaseListViewAdapter blva = new BaseListViewAdapter<Map>(this, R.layout.activity_combin_grid_picture_item) {
            @Override
            public void getBaseView(final Map data, HolderView holderView, ViewGroup parent,int position) {
                final ImageView img = (ImageView) holderView.findViewById(R.id.image);
                String url = (String) data.get("imageUrl");
                getImg(url, img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tabId = (String) data.get("tabId");
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("tabId", tabId);
                        gotoActivity(params, CombinMain.class);
                    }
                });
            }
        };
        gridView.setAdapter(blva);
        Map<String, String> map = new HashMap<String, String>();
        map.put("category", "phone");
        sendPost(Constant.CTX_PATH + "cbg_tabList", map, new HttpPostProp() {
            @Override
            public void dealRecord(Map record) {
                Map result = (Map) record;
                List<Map> tabList = (List) result.get("tabList");
                blva.refreshData(tabList);
            }
        });


    }
}







