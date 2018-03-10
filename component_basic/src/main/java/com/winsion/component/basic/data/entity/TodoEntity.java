package com.winsion.component.basic.data.entity;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by wyl on 2017/6/2
 */
@Entity
public class TodoEntity implements Serializable {
    @Id
    private Long id;
    private String content; // 提醒内容
    private long planDate;  // 提醒时间
    private boolean finished;   // 是否已完成
    private String belongUserId;    // 是哪个用户添加的

    public TodoEntity(Long id, String content, long planDate, boolean finished, String belongUserId) {
        this.id = id;
        this.content = content;
        this.planDate = planDate;
        this.finished = finished;
        this.belongUserId = belongUserId;
    }

    public TodoEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPlanDate() {
        return this.planDate;
    }

    public void setPlanDate(long planDate) {
        this.planDate = planDate;
    }

    public boolean getFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getBelongUserId() {
        return this.belongUserId;
    }

    public void setBelongUserId(String belongUserId) {
        this.belongUserId = belongUserId;
    }
}
