package com.winsion.wisdomstation.modules.operation.modules.taskmonitor.fragment;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.wisdomstation.common.constants.SystemType;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.FieldKey;
import com.winsion.wisdomstation.data.constants.JoinKey;
import com.winsion.wisdomstation.data.constants.Mode;
import com.winsion.wisdomstation.data.constants.SPKey;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.constants.ViewName;
import com.winsion.wisdomstation.data.entity.OrderBy;
import com.winsion.wisdomstation.data.entity.ResponseForQueryData;
import com.winsion.wisdomstation.data.entity.WhereClause;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.entity.TaskEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/25.
 */

public class TaskMonitorPresenter implements TaskMonitorContract.Presenter {
    private TaskMonitorContract.View mView;
    private Context mContext;

    TaskMonitorPresenter(TaskMonitorContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void getMonitorTaskData(int sysType) {
        boolean isGrid = sysType == SystemType.GRID;
        int fieldKey = isGrid ? FieldKey.EQUALS : FieldKey.NOEQUAL;

        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.LIKE);
        whereClause.setJoinKey(JoinKey.AND);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClause.setFields("monitorteamid");
        whereClauses.add(whereClause);

        WhereClause whereClause1 = new WhereClause();
        whereClause1.setFields("taktype");
        whereClause1.setValueKey(String.valueOf(TaskType.GRID));
        whereClause1.setJoinKey(JoinKey.OTHER);
        whereClause1.setFieldKey(fieldKey);
        whereClauses.add(whereClause1);

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
    public int getCurrentSystemType() {
        return (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
