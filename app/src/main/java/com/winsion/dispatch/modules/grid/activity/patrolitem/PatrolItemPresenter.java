package com.winsion.dispatch.modules.grid.activity.patrolitem;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.FieldKey;
import com.winsion.component.basic.data.constants.JoinKey;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.data.constants.ViewName;
import com.winsion.component.basic.data.entity.ResponseForQueryData;
import com.winsion.component.basic.data.entity.WhereClause;
import com.winsion.component.basic.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.biz.SubmitBiz;
import com.winsion.dispatch.modules.grid.entity.PatrolItemEntity;
import com.winsion.component.basic.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/1
 */

public class PatrolItemPresenter extends SubmitBiz implements PatrolItemContract.Presenter {
    private PatrolItemContract.View mView;
    private Context mContext;

    PatrolItemPresenter(PatrolItemContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
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
        if (CacheDataSource.getTestMode()) {
            List<PatrolItemEntity> patrolItemEntities = JsonUtils.getTestEntities(mContext, PatrolItemEntity.class);
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

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
