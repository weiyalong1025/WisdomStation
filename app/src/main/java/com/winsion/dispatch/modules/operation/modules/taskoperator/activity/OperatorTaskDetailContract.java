package com.winsion.dispatch.modules.operation.modules.taskoperator.activity;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Contract
 */

class OperatorTaskDetailContract {
    interface View extends BaseView {
        void onPublisherUploadedFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);

        void onPerformerUploadedFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);
    }

    interface Presenter extends BasePresenter {
        ArrayList<LocalRecordEntity> getPerformerLocalFile(String jobOperatorsId);

        ArrayList<LocalRecordEntity> getPublisherLocalFile(String jobsId);

        void getPublisherUploadedFile(String jobsId);

        void getPerformerUploadedFile(String jobOperatorsId);
    }
}
