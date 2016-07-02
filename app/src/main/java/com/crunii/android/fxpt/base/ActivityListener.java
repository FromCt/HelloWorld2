package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by 王春晖 on 2015/11/22.
 */
public interface ActivityListener {
    public void operListener(Activity activity, View view, ViewEventType eventType);
}
