package com.winsion.component.basic.listener;

import java.io.File;

/**
 * Created by 10295 on 2018/1/9.
 * 上传文件事件监听
 */

public interface MyUploadListener {
    void uploadProgress(File uploadFile, int progress);

    void uploadSuccess(File uploadFile);

    void uploadFailed(File uploadFile);
}
