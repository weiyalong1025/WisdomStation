package com.winsion.dispatch.modules.operation.constants;

/**
 * Created by wyl on 2017/4/24.
 * 列车状态
 */

public interface TrainState {
    /**
     * 开始检票
     */
    int IN_PROGRESS = 0;
    /**
     * 完成检票
     */
    int FINISH = 1;
    /**
     * 停止检票
     */
    int STOP = 2;
}
