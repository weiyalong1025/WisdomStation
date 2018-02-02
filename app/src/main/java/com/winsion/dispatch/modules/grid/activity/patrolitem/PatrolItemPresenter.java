package com.winsion.dispatch.modules.grid.activity.patrolitem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lzy.okgo.model.HttpParams;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.DBDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.entity.PatrolItemEntity;
import com.winsion.dispatch.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/1
 */

public class PatrolItemPresenter implements PatrolItemContract.Presenter {
    private PatrolItemContract.View mView;
    private final DBDataSource dbDataSource;

    PatrolItemPresenter(PatrolItemContract.View view) {
        this.mView = view;
        this.dbDataSource = DBDataSource.getInstance();
    }

    @Override
    public void start() {

    }

    /**
     * 获取巡检项数据
     *
     * @param patrolId 巡检任务ID
     */
    @Override
    public void getPatrolItemData(String patrolId) {
        if (AppApplication.TEST_MODE) {
            List<PatrolItemEntity> patrolItemEntities = JsonUtils.getTestEntities(mView.getContext(), PatrolItemEntity.class);
            mView.getPatrolItemDataSuccess(patrolItemEntities);
            return;
        }
        List<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setFields("patrolsid");
        whereClause.setValueKey(patrolId);
        whereClauses.add(whereClause);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, null, ViewName.PATROL_DETAILS_INFO,
                1, new ResponseListener<ResponseForQueryData<List<PatrolItemEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<PatrolItemEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<PatrolItemEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<PatrolItemEntity>> result) {
                        List<PatrolItemEntity> dataList = result.getDataList();
                        mView.getPatrolItemDataSuccess(dataList);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getPatrolItemDataFailed();
                    }
                });
    }

    /**
     * 设备无关问题上报
     */
    @Override
    public void submitProblemWithoutDevice(PatrolItemEntity patrolItemEntity, String deviceState) {
        if (AppApplication.TEST_MODE) {
            mView.problemStateChangeSuccess(patrolItemEntity, deviceState);
            return;
        }
        HttpParams httpParams = new HttpParams();
        httpParams.put("patrolDetailId", patrolItemEntity.getId());
        httpParams.put("userId", CacheDataSource.getUserId());
        httpParams.put("teamId", CacheDataSource.getTeamId());
        httpParams.put("deviceState", deviceState);
        httpParams.put("problemImageLink", "");
        httpParams.put("comment", "");
        NetDataSource.post(this, Urls.SUBMIT_WITHOUT_DEVICE, httpParams, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                if (result.equals("true"))
                    mView.problemStateChangeSuccess(patrolItemEntity, deviceState);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                mView.problemStateChangeFailed();
            }
        });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
