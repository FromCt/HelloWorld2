package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.crunii.android.fxpt.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王春晖 on 2015/11/19.
 */
public class ActivityExOperUtil {
    private static List<ActivityListener> activityListeners = new ArrayList<ActivityListener>();

    private static void addListener(ActivityListener al) {
        activityListeners.add(al);
    }

    public static void bindEvent(Activity activity, View view, String methodName, ViewEventType... eventTypes) {
        Method method = null;
        Class<? extends Activity> clazz = activity.getClass();
        try {
            method = clazz.getMethod(methodName, View.class);
            new ViewEventListener(activity, method, eventTypes, view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void initViewEvent(final Activity activity) {
        Method[] ms = activity.getClass().getMethods();
        for (final Method m : ms) {
            ViewEvent ans = m.getAnnotation(ViewEvent.class);
            if (ans == null) {
                continue;
            }
            Integer viewId = ans.id();
            View v = activity.findViewById(viewId);
            if (v == null) {
                continue;
            }
            ViewEventType[] eventTypes = ans.eventType();
            new ViewEventListener(activity, m, eventTypes, v);

        }
    }

    public static void getImg(Activity activity,String url, final ImageView imageView) {
        HttpTool.getImg(activity,url, new HttpPostProp() {
            @Override
            public void successBitmap(Bitmap bitmap) {
                if (bitmap == null) {
                    imageView.setImageResource(R.drawable.ct_test_picture);
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void fail(Exception e) {
                //super.fail(e);
                imageView.setImageResource(R.drawable.ct_test_picture);
            }
        });
    }
}
