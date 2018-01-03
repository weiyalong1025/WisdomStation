package com.winsion.wisdomstation.operation.modules.taskmonitor.fragment;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.operation.entity.TaskEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/25.
 */

public class TaskMonitorContract {
    interface View extends BaseView {
        void getMonitorTaskDataSuccess(List<TaskEntity> data);

        void getMonitorTaskDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getMonitorTaskData(int sysType);

        int getCurrentSystemType();
    }
}
