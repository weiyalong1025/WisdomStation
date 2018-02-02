package com.winsion.dispatch.modules.operation.fragment.taskmonitor;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.operation.entity.TaskEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/25
 */

class MonitorTaskListContract {
    interface View extends BaseView {
        void getMonitorTaskDataSuccess(List<TaskEntity> data);

        void getMonitorTaskDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getMonitorTaskData(int sysType);

        int getCurrentSystemType();
    }
}
