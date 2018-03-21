package com.winsion.component.task.fragment.taskmonitor;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.Mode;
import com.winsion.component.basic.constants.OpeCode;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.OrderBy;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.utils.JsonUtils;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.JobParameter;
import com.winsion.component.task.entity.TaskEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/25
 */

public class MonitorTaskListPresenter implements MonitorTaskListContract.Presenter {
    private final MonitorTaskListContract.View mView;
    private final Context mContext;

    MonitorTaskListPresenter(MonitorTaskListContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void getMonitorTaskData() {
        if (CacheDataSource.getTestMode()) {
            mView.getMonitorTaskDataSuccess(JsonUtils.getTestEntities(mContext, TaskEntity.class));
            return;
        }
        List<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.LIKE);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClause.setFields("monitorteamid");
        whereClauses.add(whereClause);

        List<OrderBy> orderBy = new ArrayList<>();
        OrderBy order = new OrderBy();
        order.setField("planstarttime");
        order.setMode(Mode.ASC);
        orderBy.add(order);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, orderBy, ViewName.TASK_INFO, 1,
                new ResponseListener<ResponseForQueryData<List<TaskEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<TaskEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<TaskEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<TaskEntity>> result) {
                        mView.getMonitorTaskDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getMonitorTaskDataFailed();
                    }
                });
    }

    @Override
    public void confirm(TaskEntity taskEntity, int opeType) {
        if (CacheDataSource.getTestMode()) {
            mView.confirmSuccess(taskEntity.getTasksid(), opeType);
            return;
        }
        // 先用taskId查询JobEntity在调用确认接口
        List<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setValueKey(taskEntity.getTasksid());
        whereClause.setFields("tasksid");
        whereClauses.add(whereClause);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, null,
                ViewName.JOB_INFO, 1, new ResponseListener<ResponseForQueryData<List<JobEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<JobEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<JobEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<JobEntity>> result) {
                        List<JobEntity> dataList = result.getDataList();
                        if (dataList != null && dataList.size() == 1) {
                            getJobInfoFinished(dataList.get(0), opeType);
                        } else {
                            mView.confirmFailed(taskEntity.getTasksid());
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.confirmFailed(taskEntity.getTasksid());
                    }
                });
    }

    private void getJobInfoFinished(JobEntity jobDto, int opeType) {
        JobParameter jobParameter = new JobParameter();
        jobParameter.setUsersId(CacheDataSource.getUserId());
        jobParameter.setJobsId(jobDto.getJobsid());
        jobParameter.setSsId(BasicBiz.getBSSID(mContext));
        jobParameter.setTaskId(jobDto.getTasksid());
        jobParameter.setOpormotId(jobDto.getJoboperatorsid());
        jobParameter.setOpType(opeType);
        NetDataSource.post(this, Urls.JOb, jobParameter, OpeCode.TASK, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                mView.confirmSuccess(jobDto.getTasksid(), opeType);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                mView.confirmFailed(jobDto.getTasksid());
            }
        });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
