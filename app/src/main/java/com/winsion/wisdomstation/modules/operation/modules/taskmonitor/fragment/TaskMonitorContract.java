package com.winsion.wisdomstation.modules.operation.modules.taskmonitor.fragment;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.modules.operation.entity.TaskEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/25.
 */

class TaskMonitorContract {
    interface View extends BaseView {
        void getMonitorTaskDataSuccess(List<TaskEntity> data);

        void getMonitorTaskDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getMonitorTaskData(int sysType);

        int getCurrentSystemType();
    }
}
