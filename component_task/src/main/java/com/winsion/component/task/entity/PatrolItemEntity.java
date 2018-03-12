package com.winsion.component.task.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/6/27
 */
public class PatrolItemEntity implements Serializable {
    private String patrolitemsid;   // 单条巡视任务ID
    private String patrolsid;   // 巡视任务总ID
    private String patroltime;  // 操作设备时间
    private String commituser;  // 操作用户名
    private String devicestate; // 设备状态
    private String commituserid;    // 操作用户ID
    private String committeamid;    // 操作用户所属班组ID
    private String committeamname;  // 操作用户所属班组名
    private String itemdescribe;    // 巡视项目描述
    private String pointname;   // 地点
    private String id;  // detailId

    public String getPatrolitemsid() {
        return patrolitemsid;
    }

    public void setPatrolitemsid(String patrolitemsid) {
        this.patrolitemsid = patrolitemsid;
    }

    public String getPatrolsid() {
        return patrolsid;
    }

    public void setPatrolsid(String patrolsid) {
        this.patrolsid = patrolsid;
    }

    public String getPatroltime() {
        return patroltime;
    }

    public void setPatroltime(String patroltime) {
        this.patroltime = patroltime;
    }

    public String getCommituser() {
        return commituser;
    }

    public void setCommituser(String commituser) {
        this.commituser = commituser;
    }

    public String getDevicestate() {
        return devicestate;
    }

    public void setDevicestate(String devicestate) {
        this.devicestate = devicestate;
    }

    public String getCommituserid() {
        return commituserid;
    }

    public void setCommituserid(String commituserid) {
        this.commituserid = commituserid;
    }

    public String getCommitteamid() {
        return committeamid;
    }

    public void setCommitteamid(String committeamid) {
        this.committeamid = committeamid;
    }

    public String getCommitteamname() {
        return committeamname;
    }

    public void setCommitteamname(String committeamname) {
        this.committeamname = committeamname;
    }

    public String getItemdescribe() {
        return itemdescribe;
    }

    public void setItemdescribe(String itemdescribe) {
        this.itemdescribe = itemdescribe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
