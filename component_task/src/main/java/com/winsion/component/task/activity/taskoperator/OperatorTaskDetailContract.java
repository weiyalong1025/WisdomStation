package com.winsion.component.task.activity.taskoperator;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.listener.MyUploadListener;
import com.winsion.component.media.entity.ServerRecordEntity;
import com.winsion.component.task.entity.JobEntity;

import java.io.File;
import java.util.List;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Contract
 */

class OperatorTaskDetailContract {
    interface View extends BaseView {
        void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);

        void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);
    }

    interface Presenter extends BasePresenter {
        void getPublisherUploadedFile(String jobsId);

        void getPerformerUploadedFile(String jobOperatorsId);

        void uploadFile(JobEntity jobEntity, File file, MyUploadListener myUploadListener);
    }
}
