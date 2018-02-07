package com.winsion.dispatch.modules.operation.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/6/17
 */
public class TaskEntity implements Serializable {
    private String tasksid; // 任务编号
    private String taskname;    // 任务名称
    private int job;    // 作业数量
    private String notes;   // 任务备注
    private String planendtime; // 计划结束时间
    private String planstarttime;   // 计划开始时间
    private String realendtime; // 实际结束时间
    private String realstarttime;   // 实际开始时间
    private int taktype;    // 任务类型
    private int taskstatus; // 任务状态
    private String trainnumber; // 车次
    private String sstname; // 起始车站名
    private String estname; // 终点车站名
    private String areatypename;    // 车辆关联区域类型("站台,候车室,股道,检票口")
    private String areatypeno;  // 车辆关联区域类型编号("120,121,119,108")
    private int trainlate;  // 列车晚点状态(正点，晚点，晚点未定，停运)
    private String areaname;    // 区域名称
    private String memo;    // 等级

    public String getTasksid() {
        return tasksid;
    }

    public void setTasksid(String tasksid) {
        this.tasksid = tasksid;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPlanendtime() {
        return planendtime;
    }

    public void setPlanendtime(String planendtime) {
        this.planendtime = planendtime;
    }

    public String getPlanstarttime() {
        return planstarttime;
    }

    public void setPlanstarttime(String planstarttime) {
        this.planstarttime = planstarttime;
    }

    public String getRealendtime() {
        return realendtime;
    }

    public void setRealendtime(String realendtime) {
        this.realendtime = realendtime;
    }

    public String getRealstarttime() {
        return realstarttime;
    }

    public void setRealstarttime(String realstarttime) {
        this.realstarttime = realstarttime;
    }

    public int getTaktype() {
        return taktype;
    }

    public void setTaktype(int taktype) {
        this.taktype = taktype;
    }

    public int getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(int taskstatus) {
        this.taskstatus = taskstatus;
    }

    public String getTrainnumber() {
        return trainnumber;
    }

    public void setTrainnumber(String trainnumber) {
        this.trainnumber = trainnumber;
    }

    public String getSstname() {
        return sstname;
    }

    public void setSstname(String sstname) {
        this.sstname = sstname;
    }

    public String getEstname() {
        return estname;
    }

    public void setEstname(String estname) {
        this.estname = estname;
    }

    public String getAreatypename() {
        return areatypename;
    }

    public void setAreatypename(String areatypename) {
        this.areatypename = areatypename;
    }

    public String getAreatypeno() {
        return areatypeno;
    }

    public void setAreatypeno(String areatypeno) {
        this.areatypeno = areatypeno;
    }

    public int getTrainlate() {
        return trainlate;
    }

    public void setTrainlate(int trainlate) {
        this.trainlate = trainlate;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
