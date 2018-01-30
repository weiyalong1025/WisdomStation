package com.winsion.dispatch.media.constants;

/**
 * Created by wyl on 2017/6/16
 */
public interface FileStatus {
    /**
     * 未上传
     */
    int NO_UPLOAD = 0;

    /**
     * 未下载
     */
    int NO_DOWNLOAD = 1;

    /**
     * 上传中
     */
    int UPLOADING = 2;

    /**
     * 下载中
     */
    int DOWNLOADING = 3;

    /**
     * 已同步
     */
    int SYNCHRONIZED = 4;
}
