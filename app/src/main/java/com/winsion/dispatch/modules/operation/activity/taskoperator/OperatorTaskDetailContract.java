package com.winsion.dispatch.modules.operation.activity.taskoperator;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.data.listener.DownloadListener;
import com.winsion.dispatch.data.listener.UploadListener;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;
import com.winsion.dispatch.modules.operation.entity.JobEntity;

import java.io.File;
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

        void download(String url, String targetPath, DownloadListener downloadListener);

        void upload(JobEntity jobEntity, File file, UploadListener uploadListener);
    }
}
