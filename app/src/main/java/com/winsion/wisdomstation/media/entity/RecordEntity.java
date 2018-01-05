package com.winsion.wisdomstation.media.entity;

/**
 * Created by wyl on 2017/6/16
 */
public class RecordEntity {
    /**
     * 文件类型 FileType
     */
    private int fileType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 状态 FileState
     */
    private int fileStatus;

    /**
     * 文件本地路径
     */
    private String localPath;

    /**
     * 文件服务器路径
     */
    private String serverPath;

    /**
     * 进度 0-100
     */
    private int progress;

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
