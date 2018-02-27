package com.winsion.dispatch.data.entity;

/**
 * Created by yalong on 2016/6/22.
 * Meta
 */
public class Meta {
    private int code;

    private String error;

    private String info;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "code=" + code +
                ", error='" + error + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}