package com.winsion.dispatch.modules.grid.entity;

import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.constants.Formatter;

import java.io.Serializable;

/**
 * Created by wyl on 2017/6/26
 * 巡视计划一级界面数据
 */
public class PatrolPlanEntity implements Serializable, CommonBiz.HalfSearchCondition {
    private String id;  // ID
    private String pointsid;    // ID 巡视点编号
    private String planstarttime;   // 计划开始时间
    private String planendtime; // 计划结束时间
    private String realstarttime;   // 实际开始时间
    private String realendtime; // 实际结束时间
    private String patrolsstate;    // 状态
    private String createdate;  // 创建时间
    private String postid; // 职能组编号
    private int itemscount; // 巡视任务总量
    private int finishcount;    // 完成总量
    private int problemscount;  // 问题总量
    private String postname;    // 职能组名称
    private String pointname;   // 巡视点名称
    private String bluetoothid; // 蓝牙信标ID
    private boolean isArrive;   // 是否到达蓝牙标签附近

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

    @Override
    public String equalFieldValue() {
        return id;
    }

    @Override
    public long compareFieldValue() {
        return ConvertUtils.parseDate(planstarttime, Formatter.DATE_FORMAT1);
    }
}
