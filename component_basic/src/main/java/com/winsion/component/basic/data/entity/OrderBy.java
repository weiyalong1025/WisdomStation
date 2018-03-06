package com.winsion.component.basic.data.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wyl on 2017/6/13
 */
public class OrderBy {
    @JSONField(name = "Field")
    private String Field;
    @JSONField(name = "Mode")
    private int Mode;

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public int getMode() {
        return Mode;
    }

    public void setMode(int mode) {
        Mode = mode;
    }
}
