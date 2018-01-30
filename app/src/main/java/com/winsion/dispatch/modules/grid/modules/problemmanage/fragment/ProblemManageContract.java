package com.winsion.dispatch.modules.grid.modules.problemmanage.fragment;


import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.operation.entity.TaskEntity;

import java.util.List;

/**
 * Created by wyl on 2017/6/29
 */
class ProblemManageContract {
    interface View extends BaseView {
        void getDataSuccess(List<TaskEntity> dataList);

        void getDataFailed(String errorInfo);

        void confirmSuccess();

        void confirmFailed(String errorInfo);
    }

    interface Presenter extends BasePresenter {
        void getData();

        /**
         * 问题状态变更为已确认
         */
        void confirm(TaskEntity taskDto, int opeType);

        void exit();
    }
}
