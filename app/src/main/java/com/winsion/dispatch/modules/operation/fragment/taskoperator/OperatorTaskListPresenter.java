package com.winsion.dispatch.modules.operation.fragment.taskoperator;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Mode;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.OrderBy;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.operation.constants.TaskType;
import com.winsion.dispatch.modules.operation.entity.JobEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015
 */

public class OperatorTaskListPresenter implements OperatorTaskListContract.Presenter {
    private OperatorTaskListContract.View mView;
    private Context mContext;

    OperatorTaskListPresenter(OperatorTaskListContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void getMyTaskData(int sysType) {
        boolean isGrid = sysType == SystemType.GRID;
        int fieldKey = isGrid ? FieldKey.EQUALS : FieldKey.NOEQUAL;

        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.AND);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClause.setFields("teamsid");
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
    public int getCurrentSystemType() {
        return (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
