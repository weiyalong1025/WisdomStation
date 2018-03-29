package com.winsion.component.contact.entity;

import java.util.HashMap;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.commons.models.IUser;

public class MyMessage implements IMessage {
    private long id;
    private IUser user;
    private String timeString;
    private int type;
    private MessageStatus messageStatus;
    private String text;
    private String mediaFilePath;
    private long duration;
    private String progress;

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMediaFilePath(String mediaFilePath) {
        this.mediaFilePath = mediaFilePath;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getMsgId() {
        return String.valueOf(id);
    }

    @Override
    public IUser getFromUser() {
        return user;
    }

    @Override
    public String getTimeString() {
        return timeString;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getMediaFilePath() {
        return mediaFilePath;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    @Override
    public HashMap<String, String> getExtras() {
        return null;
    }
}
