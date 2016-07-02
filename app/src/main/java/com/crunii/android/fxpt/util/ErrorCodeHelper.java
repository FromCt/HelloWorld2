package com.crunii.android.fxpt.util;

import com.crunii.android.fxpt.activity.LoginActivity;

import android.content.Context;
import android.content.Intent;

public class ErrorCodeHelper {

	public static String getErrorCode(String msg, Context context) {
		String code = msg;
		if (msg.equals("401")) {
			code = "请重新登录";
			Intent i = new Intent(context, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} 
		return code;
	}
}
