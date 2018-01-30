package com.winsion.dispatch.common.entity;

/**
 * Created by 10295 on 2018/1/30
 */

public class UpdateEntity {
    private String versionCode;
    private String downloadUrl;
    private String updateInfo;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }
}
