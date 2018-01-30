package com.winsion.dispatch.modules.reminder.entity;

import java.io.Serializable;

/**
 * Created by admin on 2017/1/3.
 */

public class RemindEntity implements Serializable {
    private String id;
    private String operatorteamid;
    private String monitorteamid;
    private String voicecontent;
    private String taskid;
    private int readed;
    private String sendtime;
    private int taskType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatorteamid() {
        return operatorteamid;
    }

    public void setOperatorteamid(String operatorteamid) {
        this.operatorteamid = operatorteamid;
    }

    public String getMonitorteamid() {
        return monitorteamid;
    }

    public void setMonitorteamid(String monitorteamid) {
        this.monitorteamid = monitorteamid;
    }

    public String getVoicecontent() {
        return voicecontent;
    }

    public void setVoicecontent(String voicecontent) {
        this.voicecontent = voicecontent;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}
