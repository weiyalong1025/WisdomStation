package com.winsion.component.task.entity.message;

/**
 * Created by wyl on 2017/8/25
 */
public class TaskMessage {
    private String taskid;
    private String monitorteamid;
    private String operatorteamid;
    private String taskName;
    private String taskStatus;

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getMonitorteamid() {
        return monitorteamid;
    }

    public void setMonitorteamid(String monitorteamid) {
        this.monitorteamid = monitorteamid;
    }

    public String getOperatorteamid() {
        return operatorteamid;
    }

    public void setOperatorteamid(String operatorteamid) {
        this.operatorteamid = operatorteamid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
