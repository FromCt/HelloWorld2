package com.crunii.android.fxpt.base;

import android.content.DialogInterface;

/**
 * Created by Administrator on 2015/12/2.
 */
public abstract class ConfirmDialogProperty {
    private String title = "系统提示";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getConcelBtLabel() {
        return concelBtLabel;
    }

    public void setConcelBtLabel(String concelBtLabel) {
        this.concelBtLabel = concelBtLabel;
    }

    public String getConfirmBtLabel() {
        return confirmBtLabel;
    }

    public void setConfirmBtLabel(String confirmBtLabel) {
        this.confirmBtLabel = confirmBtLabel;
    }

    private String confirmBtLabel = "确定";
    private String concelBtLabel = "取消";

    /**
     * 确定按钮回调
     *
     * @param dialogInterface
     * @param i
     */
    public abstract void confirm(DialogInterface dialogInterface, int i);

    /**
     * 取消按钮回调
     *
     * @param dialogInterface
     * @param i
     */
    public abstract void cancel(DialogInterface dialogInterface, int i);


}
