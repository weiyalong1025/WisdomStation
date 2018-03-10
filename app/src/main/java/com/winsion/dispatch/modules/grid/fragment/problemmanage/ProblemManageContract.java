package com.winsion.dispatch.modules.grid.fragment.problemmanage;


import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.task.entity.TaskEntity;

import java.util.List;

/**
 * Created by wyl on 2017/6/29
 */
class ProblemManageContract {
    interface View extends BaseView {
        void getProblemDataSuccess(List<TaskEntity> dataList);

        void getProblemDataFailed(String errorInfo);

        void confirmFailed(String tasksId);

        void confirmSuccess(String tasksId, int opeType);
    }

    interface Presenter extends BasePresenter {
        void getProblemData();

        /**
         * 问题状态变更为已确认
         */
        void confirm(TaskEntity taskDto, int opeType);

        void exit();
    }
}
