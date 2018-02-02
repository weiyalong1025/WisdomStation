package com.winsion.dispatch.modules.grid.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/6/26
 * 巡视计划一级界面数据
 */
public class PatrolPlanEntity implements Serializable {
    /**
     * ID
     */
    private String id;

    /**
     * ID 巡视点编号
     */
    private String pointsid;

    /**
     * 计划开始时间
     */
    private String planstarttime;

    /**
     * 计划结束时间
     */
    private String planendtime;

    /**
     * 实际开始时间
     */
    private String realstarttime;

    /**
     * 实际结束时间
     */
    private String realendtime;

    /**
     * 状态
     */
    private String patrolsstate;

    /**
     * 创建时间
     */
    private String createdate;

    /**
     * 职能组编号
     */
    private String postid;

    /**
     * 巡视任务总量
     */
    private int itemscount;

    /**
     * 完成总量
     */
    private int finishcount;

    /**
     * 问题总量
     */
    private int problemscount;

    /**
     * 职能组名称
     */
    private String postname;

    /**
     * 巡视点名称
     */
    private String pointname;

    /**
     * 蓝牙信标ID
     */
    private String bluetoothid;

    /**
     * 是否到达蓝牙标签附近
     */
    private boolean isArrive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPointsid() {
        return pointsid;
    }

    public void setPointsid(String pointsid) {
        this.pointsid = pointsid;
    }

    public String getPlanstarttime() {
        return planstarttime;
    }

    public void setPlanstarttime(String planstarttime) {
        this.planstarttime = planstarttime;
    }

    public String getPlanendtime() {
        return planendtime;
    }

    public void setPlanendtime(String planendtime) {
        this.planendtime = planendtime;
    }

    public String getRealstarttime() {
        return realstarttime;
    }

    public void setRealstarttime(String realstarttime) {
        this.realstarttime = realstarttime;
    }

    public String getRealendtime() {
        return realendtime;
    }

    public void setRealendtime(String realendtime) {
        this.realendtime = realendtime;
    }

    public String getPatrolsstate() {
        return patrolsstate;
    }

    public void setPatrolsstate(String patrolsstate) {
        this.patrolsstate = patrolsstate;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public int getItemscount() {
        return itemscount;
    }

    public void setItemscount(int itemscount) {
        this.itemscount = itemscount;
    }

    public int getFinishcount() {
        return finishcount;
    }

    public void setFinishcount(int finishcount) {
        this.finishcount = finishcount;
    }

    public int getProblemscount() {
        return problemscount;
    }

    public void setProblemscount(int problemscount) {
        this.problemscount = problemscount;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public String getBluetoothid() {
        return bluetoothid;
    }

    public void setBluetoothid(String bluetoothid) {
        this.bluetoothid = bluetoothid;
    }

    public boolean isArrive() {
        return isArrive;
    }

    public void setArrive(boolean arrive) {
        isArrive = arrive;
    }
}
