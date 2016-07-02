package com.crunii.android.fxpt.base;

import android.app.Activity;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 王春晖 on 2015/11/12.
 */
public class ViewEventListener implements OnClickListener, OnLongClickListener, OnDragListener {
    private Activity viewActivity;
    private Method excuteMethod;
    private List<ActivityListener> activityListeners;


    public ViewEventListener(Activity activity, Method method, ViewEventType[] eventTypes, View v) {
        this.viewActivity = activity;
        this.excuteMethod = method;
        Class clazz = excuteMethod.getReturnType();
        for (ViewEventType eventType : eventTypes) {
            //添加事件的实现到具体的View 监听器
            if (eventType == ViewEventType.CLICK) {
                v.setOnClickListener(this);
            } else if (eventType == ViewEventType.LONGCLICK) {
                v.setOnLongClickListener(this);
            } else if (eventType == ViewEventType.DRAG) {
                v.setOnDragListener(this);
            }
        }

    }

    private void eventListener(View v, Activity activity, ViewEventType eventType) {
        if (activityListeners != null) {
            for (ActivityListener al : activityListeners) {
                al.operListener(activity, v, eventType);
            }
        }
    }

    /**
     * 不需要返回的事件实现，例如onClick
     *
     * @param objects
     */
    private void voidMethod(Object... objects) {
        try {
            excuteMethod.invoke(viewActivity, objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回boolean类型的事件实现，例如onLongClick
     *
     * @param objects
     */
    private boolean booleanMethod(Object... objects) {
        boolean flag = false;
        try {
            flag = (Boolean) excuteMethod.invoke(viewActivity, objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void onClick(View v) {
        eventListener(v, viewActivity, ViewEventType.CLICK);
        voidMethod(v);
    }

    @Override
    public boolean onLongClick(View v) {
        eventListener(v, viewActivity, ViewEventType.LONGCLICK);
        return booleanMethod(v);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        eventListener(v, viewActivity, ViewEventType.LONGCLICK);
        return booleanMethod(v, event);
    }
}
