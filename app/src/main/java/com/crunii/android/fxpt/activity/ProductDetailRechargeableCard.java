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

public class ProductDetailRechargeableCard extends ProductDetailActivity {
    public final static String templateId = "1003";//充值卡模板,电子卡

	@Override
	void showOrHideViews() {
		productsview.setVisibility(View.VISIBLE);
        numLimitView.setVisibility(View.GONE);
        preExplainView.setVisibility(View.GONE);
		serialnumberview.setVisibility(View.GONE);
		fixedlineview.setVisibility(View.GONE);
		broadbandview.setVisibility(View.GONE);
		phoneview.setVisibility(View.GONE);
		contractmodeview.setVisibility(View.GONE);
		choose_number_view.setVisibility(View.GONE);
		contract_view.setVisibility(View.GONE);
		contract_package_view.setVisibility(View.GONE);
	//	salemodeview.setVisibility(View.GONE);
		amountview.setVisibility(View.VISIBLE);
		guaranteeview.setVisibility(View.GONE);

	}
	
	@Override
	String getTemplateId() {
		return templateId;
	}

	@Override
	void updateProductPrice(final String productCode, String salemode, String contractId) {

        //终端模板：主产品 决定价格
		if(productCode.equals("")) {
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
                //终端模板，可能存在折扣
                updateDiscount(productCode, getCountNumber());
			}
		}.execute(productCode, "", goodsCode, "");
	}

}