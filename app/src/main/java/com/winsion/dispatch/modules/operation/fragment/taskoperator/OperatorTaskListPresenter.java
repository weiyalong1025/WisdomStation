package com.winsion.dispatch.modules.operation.fragment.taskoperator;

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
import com.winsion.component.basic.utils.JsonUtils;
import com.winsion.dispatch.modules.operation.biz.ChangeStatusBiz;
import com.winsion.dispatch.modules.operation.entity.JobEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015
 */

public class OperatorTaskListPresenter extends ChangeStatusBiz implements OperatorTaskListContract.Presenter {
    private final OperatorTaskListContract.View mView;
    private final Context mContext;

    OperatorTaskListPresenter(OperatorTaskListContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

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
    }
}
