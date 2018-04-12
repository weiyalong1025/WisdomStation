package com.winsion.component.contact.entity;

import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.contact.constants.UserState;

/**
 * Created by w on 2017/7/25.
 * 联系人组实体
 */

public class ContactsGroupEntity extends ContactEntity {

//    \"talkgroupid\":\"G000000000000054\"," +
//            "\"ugid\":\"859E4B1B-38A7-4AEA-9D04-A7075B029B9B\"," +
//            "\"groupname\":\"3候检票组\"}

    private String talkgroupid;
    private String ugid;
    private String groupname;

    public String getTalkgroupid() {
        return talkgroupid;
    }

    public void setTalkgroupid(String talkgroupid) {
        this.talkgroupid = talkgroupid;
    }

    public String getUgid() {
        return ugid;
    }

    public void setUgid(String ugid) {
        this.ugid = ugid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override
    public int getConType() {
        return ContactType.TYPE_CONTACTS_GROUP;
    }

    @Override
    public String getConName() {
        return groupname;
    }

    @Override
    public String getConId() {
        return ugid;
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
