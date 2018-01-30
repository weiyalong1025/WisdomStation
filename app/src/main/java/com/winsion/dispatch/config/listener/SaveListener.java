package com.winsion.dispatch.config.listener;

/**
 * Created by 10295 on 2017/12/6 0006.
 */

public interface SaveListener {
    void saveSuccess();

    void saveFailed(int saveErrorCode);
}
