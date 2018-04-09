package com.winsion.component.contact.entity;

/**
 * Created by wyl on 2017/5/26
 */
public class UserMessage {
    private Long id;
    // 时间，消息类型，发送者(id,mmpId,name)，接收者，组，消息内容(图片和语音消息为文件ID+后缀)，描述为文件存储父目录(不带文件名)
    // 时间戳
    private long time;
    // 消息类型(MessageType)
    private int type;
    // 发送者(id,mmpId,name)
    private String senderId;
    private String senderMmpId;
    private String senderName;
    // 接收者(id,mmpId,name)
    // 组消息时该组字段值为组信息,单人消息时为用户信息
    private String receiverId;
    private String receiverMmpId;
    private String receiverName;
    // 消息内容(图片和语音消息为文件下载时所需ID)
    private String content;
    // 描述信息(本地文件存储路径,文字消息时该字段为空)
    private String description;
    // 消息所属用户ID(用来区分是哪个用户下的消息,存储到本地数据库之前才设置该值)
    private String belongUserId;
    // 消息状态(成功1,进行中2,失败3)
    private int status;
    // 是否是组消息
    private boolean isGroup;

    public UserMessage(Long id, long time, int type, String senderId,
                       String senderMmpId, String senderName, String receiverId,
                       String receiverMmpId, String receiverName, String content,
                       String description, String belongUserId, int status, boolean isGroup) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.senderId = senderId;
        this.senderMmpId = senderMmpId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverMmpId = receiverMmpId;
        this.receiverName = receiverName;
        this.content = content;
        this.description = description;
        this.belongUserId = belongUserId;
        this.status = status;
        this.isGroup = isGroup;
    }

    public UserMessage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderMmpId() {
        return this.senderMmpId;
    }

    public void setSenderMmpId(String senderMmpId) {
        this.senderMmpId = senderMmpId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverMmpId() {
        return this.receiverMmpId;
    }

    public void setReceiverMmpId(String receiverMmpId) {
        this.receiverMmpId = receiverMmpId;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBelongUserId() {
        return this.belongUserId;
    }

    public void setBelongUserId(String belongUserId) {
        this.belongUserId = belongUserId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean getIsGroup() {
        return this.isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}
