package com.crunii.android.fxpt.base;

/**
 * Created by 王春晖 on 2015/11/19.
 */
public enum ViewEventType {

    /**
     * 单击事件
     * void onClick(View v);
     */
    CLICK("onClick"),
    /**
     * 长按事件
     * boolean onLongClick(View v);
     */
    LONGCLICK("onLongClick"),

    /**
     * 拖动事件
     * boolean onDrag(View v, DragEvent event);
     */
    DRAG("onDrag");


    private String methodName;

    private ViewEventType(String methodName) {
        this.methodName = methodName;
    }

}
