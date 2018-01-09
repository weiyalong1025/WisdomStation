package com.winsion.wisdomstation.modules.operation.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/7/17
 */
public class TeamEntity implements Serializable {
    /**
     * 班组ID
     */
    private String teamid;

    /**
     * 职能组ID
     */
    private String postid;

    /**
     * 班组名称
     */
    private String teamsName;

    /**
     * 会话组ID
     */
    private String talkgroupid;

    /**
     * 用户数量
     */
    private int userCount;

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
