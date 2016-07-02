package com.crunii.android.fxpt.base;

/**
 * Created by 王春晖 on 2015/11/17.
 */
public class Result {
    public Object getRecord() {
        return record;
    }

    public void setRecord(Object record) {
        this.record = record;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    private Boolean success = false;
    private String msg = "";
    private Object record;
}
