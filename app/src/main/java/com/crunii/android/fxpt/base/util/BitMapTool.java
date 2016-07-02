package com.crunii.android.fxpt.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ct on 2015/8/19.
 */
public class BitMapTool {

    public static void decodeFile(String filePath, ImageView iv, Activity activity) {

        File file = new File(filePath);
        try {
            FileInputStream fins = new FileInputStream(file);
            decodeStream(fins, iv, activity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap decodeStream(InputStream ins, Activity activity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        try {
            while ((n = ins.read(b)) != -1) {
                baos.write(b, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = baos.toByteArray();
        return decodeByteArray(data, activity);
    }

    public static Bitmap decodeByteArray(byte[] data, Activity activity) {
        BitmapFactory.Options optsOriginal = new BitmapFactory.Options();
        optsOriginal.inJustDecodeBounds = true;
       
        BitmapFactory.decodeByteArray(data, 0, data.length, optsOriginal);
        Integer originalWidth = optsOriginal.outWidth;
        Integer disWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        Integer inSampleSize = originalWidth / disWidth;
        if (inSampleSize > 1) {
            optsOriginal.inSampleSize = inSampleSize;
        }
        optsOriginal.inJustDecodeBounds = false;
        Bitmap disOriginal = BitmapFactory.decodeByteArray(data, 0, data.length, optsOriginal);
        return disOriginal;

    }

    public static Bitmap decodeByteArray(byte[] data, ImageView iv, Activity activity) {
        Bitmap bt = decodeByteArray(data, activity);
        iv.setImageBitmap(bt);
        return bt;
    }


    public static Bitmap decodeStream(InputStream ins, ImageView iv, Activity activity) {
        Bitmap bm = decodeStream(ins, activity);
        iv.setImageBitmap(bm);
        return bm;
    }

}
