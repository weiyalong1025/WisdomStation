package com.winsion.dispatch.modules.reminder.entity;

import java.util.List;

/**
 * Created by wyl on 2017/9/27
 */
public class MessageHandling {
    private List<String> id;
    private int handle;

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * 1/2  已读/删除
     */
    public int getHandle() {
        return handle;
    }

    /**
     * 1/2  已读/删除
     */
    public void setHandle(int handle) {
        this.handle = handle;
    }
}
