package com.winsion.component.task.activity.taskmonitor;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.entity.ServerRecordEntity;
import com.winsion.component.task.entity.JobEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/3/13.
 * 监控任务详情Contract
 */

class MonitorTaskDetailContract {
    interface View extends BaseView {
        void getTaskDetailInfoSuccess(List<JobEntity> dataList);

        void getTaskDetailInfoFailed();

        void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);
    }

    interface Presenter extends BasePresenter {
        void getTaskDetailInfo(String taskId);

        void getPublisherUploadedFile(String jobsId);
    }
}
