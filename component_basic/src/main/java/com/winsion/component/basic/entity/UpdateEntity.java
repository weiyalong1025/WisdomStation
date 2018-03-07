package com.winsion.component.basic.entity;

/**
 * Created by 10295 on 2018/1/30
 */

public class UpdateEntity {
    private int versionNumber;
    private String filePath;
    private String versionContent;

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getVersionContent() {
        return versionContent;
    }

    public void setVersionContent(String versionContent) {
        this.versionContent = versionContent;
    }
}
