package com.winsion.component.contact.entity;

import com.winsion.component.contact.constants.ContactType;

import java.io.Serializable;

/**
 * Created by w on 2017/7/18.
 * 联系人实体
 */

public class ContactsEntity extends ContactEntity implements Serializable {
    private String talkgroupid;
    private String postname;
    private String areaname;
    private String postid;
    private String teamsName;
    private String siptelladdress;
    private String photourl;
    private String areaid;
    private String teamid;
    private String usersid;
    private String id;
    private boolean delflag;
    private String username;
    private String loginstatus;

    public String getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(String loginstatus) {
        this.loginstatus = loginstatus;
    }

    public String getTalkgroupid() {
        return talkgroupid;
    }

    public void setTalkgroupid(String talkgroupid) {
        this.talkgroupid = talkgroupid;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
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

    public String getSiptelladdress() {
        return siptelladdress;
    }

    public void setSiptelladdress(String siptelladdress) {
        this.siptelladdress = siptelladdress;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getUsersid() {
        return usersid;
    }

    public void setUsersid(String usersid) {
        this.usersid = usersid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDelflag() {
        return delflag;
    }

    public void setDelflag(boolean delflag) {
        this.delflag = delflag;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int getContactType() {
        return ContactType.TYPE_CONTACTS;
    }

    @Override
    public String getConName() {
        return username;
    }

    @Override
    public String getConId() {
        return usersid;
    }

    @Override
    public String getConPhotoUrl() {
        return photourl;
    }

    @Override
    public String getConLoginState() {
        return loginstatus;
    }
}
