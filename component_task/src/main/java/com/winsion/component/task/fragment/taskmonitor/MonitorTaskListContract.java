package com.winsion.component.task.fragment.taskmonitor;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.task.entity.TaskEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/25
 */

class MonitorTaskListContract {
    interface View extends BaseView {
        void getMonitorTaskDataSuccess(List<TaskEntity> data);

        void getMonitorTaskDataFailed();

        void confirmFailed(String tasksId);

        void confirmSuccess(String tasksId, int opeType);
    }

    interface Presenter extends BasePresenter {
        void getMonitorTaskData();

        /**
         * 问题状态变更(通过/未通过)
         */
        void confirm(TaskEntity taskEntity, int opeType);
    }
}
