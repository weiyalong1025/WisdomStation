package com.winsion.component.task.activity.taskoperator;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.data.listener.MyDownloadListener;
import com.winsion.component.basic.data.listener.UploadListener;
import com.winsion.component.basic.media.entity.LocalRecordEntity;
import com.winsion.component.basic.media.entity.ServerRecordEntity;
import com.winsion.component.task.entity.JobEntity;

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

        void download(String url, String targetPath, MyDownloadListener myDownloadListener);

        void upload(JobEntity jobEntity, File file, UploadListener uploadListener);

        String[] formatTrainData(String[] areaType, String[] name);
    }
}
