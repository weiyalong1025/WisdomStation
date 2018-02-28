package com.winsion.dispatch.media.entity;

import java.io.File;

/**
 * Created by wyl on 2017/6/16
 */
public class LocalRecordEntity {
    private File file;  // 文件
    private int fileType;   // 文件类型 FileType
    private int fileStatus; // 状态 FileState
    private String serverUri;   // 文件服务器路径
    private int progress;   // 进度 0-100
    private String fileName;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
