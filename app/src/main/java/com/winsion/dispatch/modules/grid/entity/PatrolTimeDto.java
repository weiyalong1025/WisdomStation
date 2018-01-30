package com.winsion.dispatch.modules.grid.entity;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by wyl on 2017/6/28
 * 记录巡视时间
 */
@Entity
public class PatrolTimeDto {
    @Id
    private Long id;
    /**
     * 巡视项目ID
     */
    private String patrolId;
    /**
     * 到位时间
     */
    private long arriveTime;
    /**
     * 结束时间
     */
    private long finishTime;

    public PatrolTimeDto(Long id, String patrolId, long arriveTime,
                         long finishTime) {
        this.id = id;
        this.patrolId = patrolId;
        this.arriveTime = arriveTime;
        this.finishTime = finishTime;
    }

    public PatrolTimeDto() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatrolId() {
        return this.patrolId;
    }

    public void setPatrolId(String patrolId) {
        this.patrolId = patrolId;
    }

    public long getArriveTime() {
        return this.arriveTime;
    }

    public void setArriveTime(long arriveTime) {
        this.arriveTime = arriveTime;
    }

    public long getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
