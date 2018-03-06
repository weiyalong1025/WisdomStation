package com.winsion.component.basic.data.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wyl on 2017/6/9
 * 查询参数
 */
public class QueryParameter {
    @JSONField(name = "OrderBy")
    private String OrderBy;
    @JSONField(name = "PageSize")
    private int PageSize;
    @JSONField(name = "PageStart")
    private int PageStart;
    @JSONField(name = "ViewName")
    private String ViewName;
    @JSONField(name = "WhereClause")
    private String WhereClause;

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String OrderBy) {
        this.OrderBy = OrderBy;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int PageSize) {
        this.PageSize = PageSize;
    }

    public int getPageStart() {
        return PageStart;
    }

    public void setPageStart(int PageStart) {
        this.PageStart = PageStart;
    }

    public String getViewName() {
        return ViewName;
    }

    public void setViewName(String ViewName) {
        this.ViewName = ViewName;
    }

    public String getWhereClause() {
        return WhereClause;
    }

    public void setWhereClause(String WhereClause) {
        this.WhereClause = WhereClause;
    }
}
