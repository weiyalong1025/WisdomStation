package com.winsion.dispatch.modules.operation.fragment.taskmonitor;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.FieldKey;
import com.winsion.component.basic.data.constants.JoinKey;
import com.winsion.component.basic.data.constants.Mode;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.data.constants.ViewName;
import com.winsion.component.basic.data.entity.OrderBy;
import com.winsion.component.basic.data.entity.ResponseForQueryData;
import com.winsion.component.basic.data.entity.WhereClause;
import com.winsion.component.basic.data.listener.ResponseListener;
import com.winsion.dispatch.modules.operation.constants.TaskType;
import com.winsion.dispatch.modules.operation.entity.TaskEntity;

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
        whereClause1.setFieldKey(FieldKey.NOEQUAL);
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
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
