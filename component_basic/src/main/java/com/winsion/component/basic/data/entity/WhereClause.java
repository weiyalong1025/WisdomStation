package com.winsion.component.basic.data.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wyl on 2017/6/13
 */
public class WhereClause {
    @JSONField(name = "FieldKey")
    private int FieldKey;
    @JSONField(name = "JoinKey")
    private int JoinKey;
    @JSONField(name = "ValueKey")
    private String ValueKey;
    @JSONField(name = "Fields")
    private String Fields;

    public int getFieldKey() {
        return FieldKey;
    }

    public void setFieldKey(int fieldKey) {
        FieldKey = fieldKey;
    }

    public int getJoinKey() {
        return JoinKey;
    }

    public void setJoinKey(int joinKey) {
        JoinKey = joinKey;
    }

    public String getValueKey() {
        return ValueKey;
    }

    public void setValueKey(String valueKey) {
        ValueKey = valueKey;
    }

    public String getFields() {
        return Fields;
    }

    public void setFields(String fields) {
        Fields = fields;
    }
}
