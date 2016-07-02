package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crunii.android.base.exception.HttpException;
import com.crunii.android.base.exception.TaskResultException;
import com.crunii.android.base.task.BaseTask;
import com.crunii.android.fxpt.CRApplication;
import com.crunii.android.fxpt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyProfitActivity extends Activity {

    //收益详情类型  分销伙伴收益、自己收益
    public static enum ProfitDetailType{
        partner,myself;
    }
    String currentMonth;
    String currentMonthProfit;
    String yesterday;
    String yesterdayProfit;
	String preTwelveTotal;
	boolean isPartner = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myprofit);

		refresh();
	}

	public void doBack(View v) {
		onBackPressed();
	}

	public void doYesterday(View v) {
		Intent i = new Intent(this, ProfitDetailActivity.class);
        i.putExtra("type","yesterday");
		i.putExtra("date", yesterday);
		startActivity(i);
	}

	public void doCurrentMonth(View v) {
		Intent i = new Intent(this, ProfitDetailActivity.class);
        i.putExtra("type", ProfitDetailType.myself.toString());
		i.putExtra("date", currentMonth);
		startActivity(i);
	}

	public void doPreTwelveTotal(View v) {
		Intent i = new Intent(this, ProfitListActivity.class);
		startActivity(i);
	}

	public void doPartner(View view) {
		if (!isPartner) {
			Intent i = new Intent(this, PartnerProfit.class);
			startActivity(i);
		}else{
			Toast.makeText(this,"对不起，您没有分销伙伴",Toast.LENGTH_SHORT).show();
		}

	}
	
	private void refresh() {

		new BaseTask<String, String, JSONObject>(this, "请稍后...") {

			@Override
			protected JSONObject doInBack(String... arg0) throws HttpException,
					IOException, TaskResultException {
				return CRApplication.getApp().myprofit(getApplicationContext());
			}

			@Override
			protected void onSuccess(JSONObject result) {
				try {
					yesterday = result.getString("yesterday");
					yesterdayProfit = result.getString("yesterdayProfit");
					currentMonth = result.getString("currentMonth");
					currentMonthProfit = result.getString("currentMonthProfit");
					preTwelveTotal = result.getString("preTwelveTotal");
					isPartner = result.getBoolean("isPartner");

					((TextView)findViewById(R.id.yesterdayProfit)).setText(yesterdayProfit);
					((TextView)findViewById(R.id.currentMonthProfit)).setText(currentMonthProfit);
					((TextView)findViewById(R.id.prevTwelveProfit)).setText(preTwelveTotal);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.execute("");
	}
}
