package com.crunii.android.fxpt.activity;

import android.view.View;
import android.widget.TextView;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONObject;

import java.io.IOException;

public class ProductDetailContract extends ProductDetailActivity {
    public final static String templateId = "1001";//合约机模版

	@Override
	void showOrHideViews() {
		productsview.setVisibility(View.VISIBLE);
		serialnumberview.setVisibility(View.GONE);
		fixedlineview.setVisibility(View.GONE);
		broadbandview.setVisibility(View.GONE);
		phoneview.setVisibility(View.GONE);
		contractmodeview.setVisibility(View.VISIBLE);
		choose_number_view.setVisibility(View.VISIBLE);
		contract_view.setVisibility(View.VISIBLE);
		contract_package_view.setVisibility(View.VISIBLE);
		//salemodeview.setVisibility(View.VISIBLE);
		amountview.setVisibility(View.GONE);
		guaranteeview.setVisibility(View.GONE);
        ITV_view.setVisibility(View.VISIBLE);
	}

	@Override
	String getTemplateId() {
		return templateId;
	}

	@Override
	void updateProductPrice(String productCode, String salemode, String contractId) {

        //合约模板：主产品、销售模式、合约 决定价格
		if(productCode.equals("") || salemode.equals("") || contractId.equals("")) {
			return;
		}

		new BaseTask<String, String, JSONObject>(this, "") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().goodsprice(getApplicationContext(), arg0[0], arg0[1], arg0[2], arg0[3]);
			}

			@Override
			protected void onSuccess(JSONObject result) {
				originalProductPrice = result.optString("price");
				payPrice = result.optString("payPrice");
				((TextView)findViewById(R.id.price)).setText("商品零售价:" + originalProductPrice + "元");
			}
		}.execute(productCode, salemode, goodsCode, contractId);
	}

}
