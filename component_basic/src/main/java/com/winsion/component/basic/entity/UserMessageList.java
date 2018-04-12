package com.winsion.component.basic.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by wyl on 2017/5/31
 */
@Entity
public class UserMessageList {
    @Id
    private Long id;
    private long time;
    private String chatToId;
    private String chatToMmpId;
    private String chatToName;
    private String content;
    private int unreadCount;
    private int contactType;
    private String belongUserId;

    public UserMessageList(Long id, long time, String chatToId, String chatToMmpId, String chatToName,
                           String content, int unreadCount, int contactType, String belongUserId) {
        this.id = id;
        this.time = time;
        this.chatToId = chatToId;
        this.chatToMmpId = chatToMmpId;
        this.chatToName = chatToName;
        this.content = content;
        this.unreadCount = unreadCount;
        this.contactType = contactType;
        this.belongUserId = belongUserId;
    }

    public UserMessageList() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getChatToId() {
        return chatToId;
    }

    public void setChatToId(String chatToId) {
        this.chatToId = chatToId;
    }

    public String getChatToMmpId() {
        return chatToMmpId;
    }

    public void setChatToMmpId(String chatToMmpId) {
        this.chatToMmpId = chatToMmpId;
    }

    public String getChatToName() {
        return chatToName;
    }

    public void setChatToName(String chatToName) {
        this.chatToName = chatToName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public String getBelongUserId() {
        return belongUserId;
    }

    public void setBelongUserId(String belongUserId) {
        this.belongUserId = belongUserId;
    }
}
