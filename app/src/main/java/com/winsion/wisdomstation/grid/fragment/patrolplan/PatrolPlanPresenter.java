package com.winsion.wisdomstation.grid.fragment.patrolplan;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.FieldKey;
import com.winsion.wisdomstation.data.constants.JoinKey;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.constants.ViewName;
import com.winsion.wisdomstation.data.entity.ResponseForQueryData;
import com.winsion.wisdomstation.data.entity.WhereClause;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.grid.entity.PatrolTaskEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 */

public class PatrolPlanPresenter implements PatrolPlanContract.Presenter {
    private PatrolPlanContract.View mView;
    private final Context mContext;

    public PatrolPlanPresenter(PatrolPlanContract.View view) {
        this.mView = view;
        this.mContext = mView.getContext();
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
