package com.winsion.wisdomstation.grid.fragment.problemmanage;


import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.operation.entity.TaskEntity;

import java.util.List;

/**
 * Created by wyl on 2017/6/29
 */
public class ProblemManageContract {
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
