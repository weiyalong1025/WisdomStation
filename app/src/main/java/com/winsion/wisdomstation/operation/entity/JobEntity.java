package com.winsion.wisdomstation.operation.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/6/13
 */
public class JobEntity implements Serializable {
    /**
     * 作业操作ID
     */
    private String joboperatorsid;

    /**
     * 作业ID
     */
    private String jobsid;

    /**
     * 操作组编号
     */
    private String teamsid;

    /**
     * 操作组名称
     */
    private String opteamname;

    /**
     * 计划结束时间
     */
    private String planendtime;

    /**
     * 计划开始时间
     */
    private String planstarttime;

    /**
     * 作业状态
     */
    private int workstatus;

    /**
     * 任务id
     */
    private String tasksid;

    /**
     * 任务名称
     */
    private String taskname;

    /**
     * 任务类型
     */
    private int taktype;

    /**
     * 任务区域ID
     */
    private String taskareaid;

    /**
     * 任务区域名称
     */
    private String taskareaname;

    /**
     * 车辆运行编号
     */
    private String runsid;

    /**
     * 任务状态
     */
    private String taskstatus;

    /**
     * 列车计划到达时间
     */
    private String arrivetime;

    /**
     * 列车晚点状态
     */
    private int delaytime;

    /**
     * 列车计划出发时间
     */
    private String departtime;

    /**
     * 列车到达时间
     */
    private String realarrivetime;

    /**
     * 列车出发时间
     */
    private String realdeparttime;

    /**
     * 车次
     */
    private String trainnumber;

    /**
     * 列车状态(正在检票，完成检票，停止检票)
     */
    private int trainstatus;

    /**
     * 列车终点站名称
     */
    private String estname;

    /**
     * 列车终点站编号
     */
    private String endstationsid;

    /**
     * 列车始发站名称
     */
    private String sstname;

    /**
     * 列车始发站编号
     */
    private String startstationsid;

    /**
     * 任务规则编号
     */
    private String ruletasksid;

    /**
     * 列车任务类型
     */
    private String traintasktypesid;

    /**
     * 车辆关联区域名称("1站台,第3候车室,1道,3候第4检票口")
     */
    private String runareaname;

    /**
     * 车辆关联区域类型("站台,候车室,股道,检票口")
     */
    private String areatypename;

    /**
     * 车辆关联区域类型编号("120,121,119,108")
     */
    private String areatypeno;

    /**
     * 列车晚点状态(正点，晚点，晚点未定，停运)
     */
    private int trainlate;

    /**
     * 实际开始时间
     */
    private String realstarttime;

    /**
     * 实际结束时间
     */
    private String realendtime;

    /**
     * 备注
     *
     * @return
     */
    private String note;

    /**
     * 命令/协作内容
     */
    private String workcontent;

    /**
     * 监视组名
     */
    private String monitorteamname;

    /**
     * 监视组ID
     */
    private String monitorteamid;

    public String getJoboperatorsid() {
        return joboperatorsid;
    }

    public void setJoboperatorsid(String joboperatorsid) {
        this.joboperatorsid = joboperatorsid;
    }

    public String getJobsid() {
        return jobsid;
    }

    public void setJobsid(String jobsid) {
        this.jobsid = jobsid;
    }

    public String getTeamsid() {
        return teamsid;
    }

    public void setTeamsid(String teamsid) {
        this.teamsid = teamsid;
    }

    public String getOpteamname() {
        return opteamname;
    }

    public void setOpteamname(String opteamname) {
        this.opteamname = opteamname;
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

    public int getWorkstatus() {
        return workstatus;
    }

    public void setWorkstatus(int workstatus) {
        this.workstatus = workstatus;
    }

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

    public int getTaktype() {
        return taktype;
    }

    public void setTaktype(int taktype) {
        this.taktype = taktype;
    }

    public String getTaskareaid() {
        return taskareaid;
    }

    public void setTaskareaid(String taskareaid) {
        this.taskareaid = taskareaid;
    }

    public String getTaskareaname() {
        return taskareaname;
    }

    public void setTaskareaname(String taskareaname) {
        this.taskareaname = taskareaname;
    }

    public String getRunsid() {
        return runsid;
    }

    public void setRunsid(String runsid) {
        this.runsid = runsid;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }

    public String getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }

    public int getDelaytime() {
        return delaytime;
    }

    public void setDelaytime(int delaytime) {
        this.delaytime = delaytime;
    }

    public String getDeparttime() {
        return departtime;
    }

    public void setDeparttime(String departtime) {
        this.departtime = departtime;
    }

    public String getRealarrivetime() {
        return realarrivetime;
    }

    public void setRealarrivetime(String realarrivetime) {
        this.realarrivetime = realarrivetime;
    }

    public String getRealdeparttime() {
        return realdeparttime;
    }

    public void setRealdeparttime(String realdeparttime) {
        this.realdeparttime = realdeparttime;
    }

    public String getTrainnumber() {
        return trainnumber;
    }

    public void setTrainnumber(String trainnumber) {
        this.trainnumber = trainnumber;
    }

    public int getTrainstatus() {
        return trainstatus;
    }

    public void setTrainstatus(int trainstatus) {
        this.trainstatus = trainstatus;
    }

    public String getEstname() {
        return estname;
    }

    public void setEstname(String estname) {
        this.estname = estname;
    }

    public String getEndstationsid() {
        return endstationsid;
    }

    public void setEndstationsid(String endstationsid) {
        this.endstationsid = endstationsid;
    }

    public String getSstname() {
        return sstname;
    }

    public void setSstname(String sstname) {
        this.sstname = sstname;
    }

    public String getStartstationsid() {
        return startstationsid;
    }

    public void setStartstationsid(String startstationsid) {
        this.startstationsid = startstationsid;
    }

    public String getRuletasksid() {
        return ruletasksid;
    }

    public void setRuletasksid(String ruletasksid) {
        this.ruletasksid = ruletasksid;
    }

    public String getTraintasktypesid() {
        return traintasktypesid;
    }

    public void setTraintasktypesid(String traintasktypesid) {
        this.traintasktypesid = traintasktypesid;
    }

    public String getRunareaname() {
        return runareaname;
    }

    public void setRunareaname(String runareaname) {
        this.runareaname = runareaname;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWorkcontent() {
        return workcontent;
    }

    public void setWorkcontent(String workcontent) {
        this.workcontent = workcontent;
    }

    public String getMonitorteamname() {
        return monitorteamname;
    }

    public void setMonitorteamname(String monitorteamname) {
        this.monitorteamname = monitorteamname;
    }

    public String getMonitorteamid() {
        return monitorteamid;
    }

    public void setMonitorteamid(String monitorteamid) {
        this.monitorteamid = monitorteamid;
    }
}