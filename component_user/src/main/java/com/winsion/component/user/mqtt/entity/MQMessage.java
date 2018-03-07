package com.winsion.component.user.mqtt.entity;

/**
 * Created by w on 2017/7/19.
 * 用户状态：包括登录和离线
 */

public class MQMessage {
    private int messageType;
    private String senderId;
    private String senderIp;
    private String messageId;
    private String mqKeyId;
    private String desc;
    private String time;
    private String data;//需要此字段，该字段存储的是userId
    private String hash;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMqKeyId() {
        return mqKeyId;
    }

    public void setMqKeyId(String mqKeyId) {
        this.mqKeyId = mqKeyId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
