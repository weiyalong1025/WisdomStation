package com.winsion.component.contact.entity;

import cn.jiguang.imui.commons.models.IUser;

public class MyUser implements IUser {
    private String id;
    private String displayName;
    private String avatar;

    public MyUser(String id, String displayName, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getAvatarFilePath() {
        return avatar;
    }
}
