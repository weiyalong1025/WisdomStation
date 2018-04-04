package com.winsion.component.task.activity.taskmonitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.entity.ServerRecordEntity;
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.entity.JobEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.SearchFileField.FIELD_MONITOR;

/**
 * Created by 10295 on 2018/3/13.
 * 监控任务详情Presenter
 */

public class MonitorTaskDetailPresenter extends TaskBiz implements MonitorTaskDetailContract.Presenter, TaskBiz.UploadFileGetListener {
    private final MonitorTaskDetailContract.View mView;

    MonitorTaskDetailPresenter(MonitorTaskDetailContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getTaskDetailInfo(String taskId) {
        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setValueKey(taskId);
        whereClause.setFields("tasksid");
        whereClauses.add(whereClause);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, null, ViewName.JOB_INFO,
                1, new ResponseListener<ResponseForQueryData<List<JobEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<JobEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<JobEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<JobEntity>> result) {
                        List<JobEntity> dataList = result.getDataList();
                        if (dataList == null || dataList.size() == 0) {
                            mView.getTaskDetailInfoFailed();
                        } else {
                            mView.getTaskDetailInfoSuccess(dataList);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getTaskDetailInfoFailed();
                    }
                });
    }

    /**
     * 查询命令/协作发布人上传的附件
     */
    @Override
    public void getPublisherUploadedFile(String jobsId) {
        getUploadedFile(FIELD_MONITOR, jobsId, ViewName.MONITOR_FILE, this);
    }

    @Override
    public void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> dataList) {
        mView.onPublisherUploadFileGetSuccess(dataList);
    }

    @Override
    public void onPublisherUploadFileGetFailed() {

    }

    @Override
    public void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> dataList) {

    }

    @Override
    public void onPerformerUploadFileGetFailed() {

    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
    }
}
