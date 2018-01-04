package com.winsion.wisdomstation.modules.operation.constants;

/**
 * Created by wyl on 2017/4/24.
 * 列车运行状态
 */

public interface RunState {
    /**
     * 正点
     */
    int ON_TIME = 0;

    /**
     * 晚点
     */
    int LATE = 1;

    /**
     * 晚点未定
     */
    int LATE_UNSURE = 2;

    /**
     * 停运
     */
    int STOP = 3;
}
