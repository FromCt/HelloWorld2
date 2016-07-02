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

public class ProductDetailBroadband extends ProductDetailActivity {
	public final static String templateId = "1008";//固话宽带模版

	@Override
	void showOrHideViews() {
		productsview.setVisibility(View.VISIBLE);
		serialnumberview.setVisibility(View.GONE);
		fixedlineview.setVisibility(View.VISIBLE);
		broadbandview.setVisibility(View.VISIBLE);
		phoneview.setVisibility(View.VISIBLE);
		contractmodeview.setVisibility(View.GONE);
		
		if(needChooseNumber) {
			choose_number_view.setVisibility(View.VISIBLE);
		} else {
			choose_number_view.setVisibility(View.GONE);
		}
		contract_view.setVisibility(View.GONE);
		contract_package_view.setVisibility(View.GONE);
	//	salemodeview.setVisibility(View.VISIBLE);
		amountview.setVisibility(View.GONE);
		guaranteeview.setVisibility(View.GONE);
		
		//20141011APK修改内容.docx 4.4:固宽模板为支付金额通栏展示，不展示库存
		findViewById(R.id.stockview).setVisibility(View.GONE);

		ITV_view.setVisibility(View.VISIBLE);

	}

	@Override
	String getTemplateId() {
		return templateId;
	}

	@Override
	void updateProductPrice(String productCode, String salemode, String contractId) {

        //固宽模板：主产品、销售模式 决定价格
		if(productCode.equals("") || salemode.equals("")) {
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
