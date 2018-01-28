package com.winsion.wisdomstation.modules.operation.modules.taskoperator.activity;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.media.entity.LocalRecordEntity;
import com.winsion.wisdomstation.media.entity.ServerRecordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Contract
 */

class OperatorTaskDetailContract {
    interface View extends BaseView {
        void onServerFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);
    }

    interface Presenter extends BasePresenter {
        ArrayList<LocalRecordEntity> getLocalFile(String jobOperatorsId);

        void getServerFile(String jobOperatorsId);
    }
}
