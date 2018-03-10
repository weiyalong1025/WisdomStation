package com.winsion.component.task.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/7/17
 */
public class TeamEntity implements Serializable {
    private String teamid;  // 班组ID
    private String postid;  // 职能组ID
    private String teamsName;   // 班组名称
    private String talkgroupid; // 会话组ID
    private int userCount;  // 用户数量

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTeamsName() {
        return teamsName;
    }

    public void setTeamsName(String teamsName) {
        this.teamsName = teamsName;
    }

    public String getTalkgroupid() {
        return talkgroupid;
    }

    public void setTalkgroupid(String talkgroupid) {
        this.talkgroupid = talkgroupid;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
