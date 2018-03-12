package com.winsion.component.basic.entity;

/**
 * Created by wyl on 2017/6/10
 */
public class ResponseForQueryData<T> {
    private int pagecount;
    private int totalcount;
    private T dataList;

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public T getDataList() {
        return dataList;
    }

    public void setDataList(T dataList) {
        this.dataList = dataList;
    }
}
