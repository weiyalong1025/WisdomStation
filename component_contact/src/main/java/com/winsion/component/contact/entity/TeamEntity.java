package com.winsion.component.contact.entity;

import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.constants.UserState;

/**
 * Created by w on 2017/7/18.
 */

public class TeamEntity extends ContactEntity {
    private String talkgroupid;
    private int userCount;
    private String teamid;
    private String postid;
    private boolean delflag;
    private String teamsName;

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

    public boolean isDelflag() {
        return delflag;
    }

    public void setDelflag(boolean delflag) {
        this.delflag = delflag;
    }

    public String getTeamsName() {
        return teamsName;
    }

    public void setTeamsName(String teamsName) {
        this.teamsName = teamsName;
    }

    @Override
    public int getConType() {
        return ContactType.TYPE_TEAM;
    }

    @Override
    public String getConName() {
        return teamsName;
    }

    @Override
    public String getConId() {
        return teamid;
    }

    @Override
    public String getConPhotoUrl() {
        return "";
    }

    @Override
    public String getConMmpId() {
        return talkgroupid;
    }

    @Override
    public String getConLoginState() {
        return UserState.OFF_LINE;
    }
}
