package com.winsion.component.user.entity;

import java.util.List;

/**
 * Created by wyl on 2017/6/9
 */
public class AuthEntity {
    private String token;
    private String httpKey;
    private String mqKeyId;
    private String mqKey;
    private String roleId;
    private String teamId;
    private String powerLine;
    private UserDto user;
    private String userId;
    private List<SystemConfigDto> configDtoList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHttpKey() {
        return httpKey;
    }

    public void setHttpKey(String httpKey) {
        this.httpKey = httpKey;
    }

    public String getMqKeyId() {
        return mqKeyId;
    }

    public void setMqKeyId(String mqKeyId) {
        this.mqKeyId = mqKeyId;
    }

    public String getMqKey() {
        return mqKey;
    }

    public void setMqKey(String mqKey) {
        this.mqKey = mqKey;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPowerLine() {
        return powerLine;
    }

    public void setPowerLine(String powerLine) {
        this.powerLine = powerLine;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SystemConfigDto> getConfigDtoList() {
        return configDtoList;
    }

    public void setConfigDtoList(List<SystemConfigDto> configDtoList) {
        this.configDtoList = configDtoList;
    }

    public class UserDto {
        private String organizationid;
        private String areaid;
        private String username;
        private int login;
        private String loginip;
        private String loginname;
        private long logintime;
        private long starttime;
        private long endtime;
        private String password;
        private String passwordsec;
        private int status;
        private String operatetime;
        private String photo;
        private String siptelladdress;
        private String sippassword;
        private int usertype;
        private String mmpuser;
        private String mmpwd;
        private int userlevel;
        private int delflag;
        private String lastSsid;
        private int device;
        private long lastTimeStamp;

        public String getOrganizationid() {
            return organizationid;
        }

        public void setOrganizationid(String organizationid) {
            this.organizationid = organizationid;
        }

        public String getAreaid() {
            return areaid;
        }

        public void setAreaid(String areaid) {
            this.areaid = areaid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getLogin() {
            return login;
        }

        public void setLogin(int login) {
            this.login = login;
        }

        public String getLoginip() {
            return loginip;
        }

        public void setLoginip(String loginip) {
            this.loginip = loginip;
        }

        public String getLoginname() {
            return loginname;
        }

        public void setLoginname(String loginname) {
            this.loginname = loginname;
        }

        public long getLogintime() {
            return logintime;
        }

        public void setLogintime(long logintime) {
            this.logintime = logintime;
        }

        public long getStarttime() {
            return starttime;
        }

        public void setStarttime(long starttime) {
            this.starttime = starttime;
        }

        public long getEndtime() {
            return endtime;
        }

        public void setEndtime(long endtime) {
            this.endtime = endtime;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPasswordsec() {
            return passwordsec;
        }

        public void setPasswordsec(String passwordsec) {
            this.passwordsec = passwordsec;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOperatetime() {
            return operatetime;
        }

        public void setOperatetime(String operatetime) {
            this.operatetime = operatetime;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getSiptelladdress() {
            return siptelladdress;
        }

        public void setSiptelladdress(String siptelladdress) {
            this.siptelladdress = siptelladdress;
        }

        public String getSippassword() {
            return sippassword;
        }

        public void setSippassword(String sippassword) {
            this.sippassword = sippassword;
        }

        public int getUsertype() {
            return usertype;
        }

        public void setUsertype(int usertype) {
            this.usertype = usertype;
        }

        public String getMmpuser() {
            return mmpuser;
        }

        public void setMmpuser(String mmpuser) {
            this.mmpuser = mmpuser;
        }

        public String getMmpwd() {
            return mmpwd;
        }

        public void setMmpwd(String mmpwd) {
            this.mmpwd = mmpwd;
        }

        public int getUserlevel() {
            return userlevel;
        }

        public void setUserlevel(int userlevel) {
            this.userlevel = userlevel;
        }

        public int getDelflag() {
            return delflag;
        }

        public void setDelflag(int delflag) {
            this.delflag = delflag;
        }

        public String getLastSsid() {
            return lastSsid;
        }

        public void setLastSsid(String lastSsid) {
            this.lastSsid = lastSsid;
        }

        public int getDevice() {
            return device;
        }

        public void setDevice(int device) {
            this.device = device;
        }

        public long getLastTimeStamp() {
            return lastTimeStamp;
        }

        public void setLastTimeStamp(long lastTimeStamp) {
            this.lastTimeStamp = lastTimeStamp;
        }
    }

    public class SystemConfigDto {
        private String configKey;
        private String configValue;

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }
    }
}
