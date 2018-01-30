package com.winsion.dispatch.modules.operation.entity;

import java.util.ArrayList;

/**
 * Created by wyl on 2017/7/17
 * 发布命令/协作参数
 */
public class PublishParameter {
    private String areaId;
    private String runsId;
    private String operatorTeamId;
    private String monitorTeamId;
    private String planStartTime;
    private String planEndTime;
    /**
     * 标题
     */
    private String taskName;
    /**
     * 类型(命令/协作)
     */
    private int taskType;
    private String usersId;
    /**
     * 内容
     */
    private String workContent;
    private String ssId;
    private String note;
    private ArrayList<FileEntity> fileList;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getRunsId() {
        return runsId;
    }

    public void setRunsId(String runsId) {
        this.runsId = runsId;
    }

    public String getOperatorTeamId() {
        return operatorTeamId;
    }

    public void setOperatorTeamId(String operatorTeamId) {
        this.operatorTeamId = operatorTeamId;
    }

    public String getMonitorTeamId() {
        return monitorTeamId;
    }

    public void setMonitorTeamId(String monitorTeamId) {
        this.monitorTeamId = monitorTeamId;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<FileEntity> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<FileEntity> fileList) {
        this.fileList = fileList;
    }
}
