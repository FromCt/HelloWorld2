package com.crunii.android.fxpt.util;

import android.content.Context;
import android.util.Log;

/**
 * Created by ct on 2016/5/19.
 */
public class MyLog {
    public static String Tag="ct";

    public static void i(String  tag,String str){
        if (tag.equals(Tag)) {
            Log.i(tag, str);
        }
    }
}
