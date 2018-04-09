package com.winsion.component.task.fragment.taskoperator;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.MQType;
import com.winsion.component.basic.constants.Mode;
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
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.message.TaskMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015
 */

public class OperatorTaskListPresenter extends TaskBiz implements OperatorTaskListContract.Presenter, MQTTClient.Observer {
    private final OperatorTaskListContract.View mView;
    private final Context mContext;

    OperatorTaskListPresenter(OperatorTaskListContract.View view) {
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
            if (TextUtils.equals(taskMessage.getOperatorteamid(), CacheDataSource.getTeamId())) {
                // 任务状态发生改变，该条任务的操作组ID为当前登录用户的组ID，全刷数据
                getMyTaskData();
            }
        }
    }

    @Override
    public void getMyTaskData() {
        if (CacheDataSource.getTestMode()) {
            mView.getMyTaskDataSuccess(JsonUtils.getTestEntities(mContext, JobEntity.class));
            return;
        }

        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClause.setFields("teamsid");
        whereClauses.add(whereClause);

        List<OrderBy> orderBy = new ArrayList<>();
        OrderBy order = new OrderBy();
        order.setField("planstarttime");
        order.setMode(Mode.ASC);
        orderBy.add(order);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, orderBy, ViewName.JOB_INFO, 1,
                new ResponseListener<ResponseForQueryData<List<JobEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<JobEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<JobEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<JobEntity>> result) {
                        mView.getMyTaskDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getMyTaskDataFailed();
                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        MQTTClient.removeObserver(this);
    }
}
