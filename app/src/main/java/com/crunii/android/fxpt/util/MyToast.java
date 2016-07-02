package com.crunii.android.fxpt.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ct on 2016/3/21.
 */
public class MyToast {

    private static Toast toast;

    public static void show(String str,Context context){
        if (context==null){
            return;
        }else{
            if (toast == null) {
                toast = Toast.makeText(context,str, Toast.LENGTH_SHORT);
            }else{
                toast.setText(str);
            }
            toast.show();
        }
    }
}
