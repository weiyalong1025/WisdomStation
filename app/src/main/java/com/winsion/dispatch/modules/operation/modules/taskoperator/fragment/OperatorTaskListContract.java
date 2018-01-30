package com.winsion.dispatch.modules.operation.modules.taskoperator.fragment;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.operation.entity.JobEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015
 */

class OperatorTaskListContract {
    interface View extends BaseView {
        void getMyTaskDataSuccess(List<JobEntity> data);

        void getMyTaskDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getMyTaskData(int sysType);

        int getCurrentSystemType();
    }
}
