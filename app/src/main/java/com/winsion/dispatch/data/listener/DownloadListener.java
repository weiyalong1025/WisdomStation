package com.winsion.dispatch.data.listener;

/**
 * Created by 10295 on 2018/1/9.
 * 上传文件事件监听
 */

public interface DownloadListener {
    void downloadProgress(String serverUri, float progress);

    void downloadSuccess(String serverUri);

    void downloadFailed(String serverUri);
}
