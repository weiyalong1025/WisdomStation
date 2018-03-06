package com.winsion.dispatch.modules.operation.fragment.taskoperator;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
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
        void getMyTaskData();
    }
}
