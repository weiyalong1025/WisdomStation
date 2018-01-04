package com.winsion.wisdomstation.modules.operation.constants;

/**
 * Created by wyl on 2017/4/24.
 * 任务状态
 */

public interface TaskState {
    /**
     * 未开始
     */
    int NOT_STARTED = 0;
    /**
     * 进行中
     */
    int RUN = 1;
    /**
     * 已结束
     */
    int DONE = 2;
    /**
     * 网格任务状态--验收通过
     */
    int GRID_CONFIRMED = 3;
    /**
     * 网格任务状态--验收未通过
     */
    int GRID_NOT_PASS = 4;
}
