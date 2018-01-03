package com.winsion.wisdomstation.operation.modules.mytask.fragment;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.operation.entity.JobEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015.
 */

class MyTaskContract {
    interface View extends BaseView {
        void getMyTaskDataSuccess(List<JobEntity> data);

        void getMyTaskDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getMyTaskData(int sysType);

        int getCurrentSystemType();
    }
}
