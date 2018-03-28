package com.winsion.component.contact.entity;

/**
 * Created by w on 2017/7/18.
 */

public class TeamEntity {

//            {\"talkgroupid\":\"G000000000000067\"," +
//            "\"userCount\":\"4\"," +
//            "\"teamid\":\"3A9530A7-A144-44E7-9E2D-5D9E78ECDCCF\"," +
//            "\"postid\":\"D83B9257-D2B9-4555-8C63-0597BFC3B04E\"," +
//            "\"delflag\":\"false\"," +
//            "\"teamsName\":\"3候客运丁班\"}

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
}
