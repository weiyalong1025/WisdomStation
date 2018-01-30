package com.winsion.dispatch.data.entity;

import java.io.Serializable;

/**
 * Created by Mr.ZCM on 2016/7/12.
 * QQ:656025633
 * Company:winsion
 * Version:1.0
 * explain:
 */
public class BaseModel implements Serializable {
    private Meta meta;
    private String data;

    public Meta getMeta() {
        return meta;
    }

    public String getData() {
        return data;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void setData(String data) {
        this.data = data;
    }
}
