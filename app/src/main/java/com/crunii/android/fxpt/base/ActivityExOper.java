package com.crunii.android.fxpt.base;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.Map;

/**
 * Created by 王春晖 on 2015/11/19.
 */
public interface ActivityExOper {

    public void afterSetContentView();

    public void sendPost(final String url, final Map<String, String> params, final HttpPostProp httpPostResult);

    public void sendPost(final String url, final Object params, final HttpPostProp httpPostResult);

    public void getImg(final String url, final HttpPostProp httpPostResult);


}
