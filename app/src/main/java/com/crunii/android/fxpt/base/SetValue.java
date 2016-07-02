package com.crunii.android.fxpt.base;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by ct on 2015/11/30.
 */
public class SetValue {
    public static void  setText(TextView textView,String str){

        if(str==null) {
            Log.i("SetValue", "setText error  value=null");
        }else{
            textView.setText(str);
        }
    }

    public static void setBitmap(ImageView imageView,Bitmap bitmap){
        if(bitmap==null) {
            Log.i("SetValue", "setBitmap error  setBitmap=null");
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }

    public static void setPostparams(HashMap<String, String> postparams,String paramValue,String key){
        if(paramValue==null) {
            Log.i("SetValue", "setPostparams error  paramValue=null");
        }else{
            postparams.put(key,paramValue);
        }

    }

}
