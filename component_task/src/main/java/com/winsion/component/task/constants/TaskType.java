package com.winsion.component.task.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wyl on 2017/6/13
 * 任务类型
 */
@IntDef({TaskType.COOPERATE, TaskType.COMMAND, TaskType.TASK, TaskType.GRID, TaskType.PLAN})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskType {
    /**
     * 协作
     */
    int COOPERATE = 0;
    /**
     * 命令
     */
    int COMMAND = 1;
    /**
     * 任务
     */
    int TASK = 2;
    /**
     * 网格
     */
    int GRID = 3;
    /**
     * 预案
     */
    int PLAN = 5;
}
