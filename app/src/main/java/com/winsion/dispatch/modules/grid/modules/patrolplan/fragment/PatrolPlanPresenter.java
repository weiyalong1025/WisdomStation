package com.winsion.dispatch.modules.grid.modules.patrolplan.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.entity.PatrolTaskEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.constants.Formatter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 */

public class PatrolPlanPresenter implements PatrolPlanContract.Presenter {
    private PatrolPlanContract.View mView;

    PatrolPlanPresenter(PatrolPlanContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getPatrolPlanData() {
        List<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setFields("teamid");
        whereClause.setJoinKey(JoinKey.AND);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClauses.add(whereClause);

        WhereClause whereClause1 = new WhereClause();
        whereClause1.setFieldKey(FieldKey.EQUALS);
        whereClause1.setFields("createdate");
        whereClause1.setJoinKey(JoinKey.OTHER);
        whereClause1.setValueKey(ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT4));
        whereClauses.add(whereClause1);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, null, ViewName.PATROL_INFO, 1,
                new ResponseListener<ResponseForQueryData<List<PatrolTaskEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<PatrolTaskEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<PatrolTaskEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<PatrolTaskEntity>> result) {
                        mView.getPatrolPlanDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getPatrolPlanDataFailed();
                    }
                });
    }

    @Override
    public void exit() {

    }
}
