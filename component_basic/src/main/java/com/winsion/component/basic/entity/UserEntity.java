package com.winsion.component.basic.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by yalong on 2016/6/28.
 * 数据库存储用户信息
 */
@Entity
public class UserEntity {
    @Id
    private Long id;
    private String userId;
    private String username;
    private String password;
    private String headerUrl;
    private String pocUsername;
    private String pocPassword;
    private long lastLoginTime;
    private String loginIp;
    private String loginPort;
    private boolean isAutoLogin;

    public UserEntity(Long id, String userId, String username, String password,
                      String headerUrl, String pocUsername, String pocPassword,
                      long lastLoginTime, String loginIp, String loginPort,
                      boolean isAutoLogin) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.headerUrl = headerUrl;
        this.pocUsername = pocUsername;
        this.pocPassword = pocPassword;
        this.lastLoginTime = lastLoginTime;
        this.loginIp = loginIp;
        this.loginPort = loginPort;
        this.isAutoLogin = isAutoLogin;
    }

    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getPocUsername() {
        return pocUsername;
    }

    public void setPocUsername(String pocUsername) {
        this.pocUsername = pocUsername;
    }

    public String getPocPassword() {
        return pocPassword;
    }

    public void setPocPassword(String pocPassword) {
        this.pocPassword = pocPassword;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginPort() {
        return this.loginPort;
    }

    public void setLoginPort(String loginPort) {
        this.loginPort = loginPort;
    }

    public boolean getIsAutoLogin() {
        return this.isAutoLogin;
    }

    public void setIsAutoLogin(boolean isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
    }
}
