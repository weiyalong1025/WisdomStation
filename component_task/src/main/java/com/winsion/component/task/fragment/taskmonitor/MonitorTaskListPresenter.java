package com.winsion.component.task.fragment.taskmonitor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.MQType;
import com.winsion.component.basic.constants.Mode;
import com.winsion.component.basic.constants.OpeCode;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.MQMessage;
import com.winsion.component.basic.entity.OrderBy;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.mqtt.MQTTClient;
import com.winsion.component.basic.utils.JsonUtils;
import com.winsion.component.basic.utils.NotifyUtils;
import com.winsion.component.basic.utils.TTSUtils;
import com.winsion.component.task.R;
import com.winsion.component.task.activity.taskmonitor.MonitorTaskDetailActivity;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.JobParameter;
import com.winsion.component.task.entity.TaskEntity;
import com.winsion.component.task.entity.message.TaskMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/25
 */

public class MonitorTaskListPresenter implements MonitorTaskListContract.Presenter, MQTTClient.Observer {
    private final MonitorTaskListContract.View mView;
    private final Context mContext;

    MonitorTaskListPresenter(MonitorTaskListContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 监听MQ消息
        MQTTClient.addObserver(this);
    }

    @Override
    public void onMessageArrive(MQMessage msg) {
        if (msg.getMessageType() == MQType.TASK_STATE) {
            String data = msg.getData();
            TaskMessage taskMessage = JSON.parseObject(data, TaskMessage.class);
            if (TextUtils.equals(taskMessage.getMonitorteamid(), CacheDataSource.getTeamId())) {
                // 任务状态发生改变，该条任务的监控组ID为当前登录用户的组ID，全刷数据
                getMonitorTaskData();
                // TTS
                TTSUtils.getInstance(mContext.getApplicationContext()).synth(mContext, msg.getDesc());
                // 发送通知
                sendNotification(msg.getDesc());
            }
        }
    }

    private void sendNotification(String desc) {
        Intent intent = new Intent(mContext, MonitorTaskDetailActivity.class);
        PendingIntent activity = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        new NotifyUtils(mContext, 1).notifyNormalMoreLine(activity, R.mipmap.basic_ic_launcher,
                desc, 123, "任务状态改变通知", desc, true, true, true);
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
        MQTTClient.removeObserver(this);
    }
}
