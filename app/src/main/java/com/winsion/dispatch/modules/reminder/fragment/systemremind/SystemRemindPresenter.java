package com.winsion.dispatch.modules.reminder.fragment.systemremind;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.application.AppApplication;
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
import com.winsion.dispatch.modules.reminder.entity.MessageHandling;
import com.winsion.dispatch.modules.reminder.entity.SystemRemindEntity;
import com.winsion.component.basic.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:21
 */

public class SystemRemindPresenter implements SystemRemindContract.Presenter {
    private SystemRemindContract.View mView;
    private Context mContext;

    SystemRemindPresenter(SystemRemindContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void getRemindData() {
        if (CacheDataSource.getTestMode()) {
            mView.getRemindDataSuccess(JsonUtils.getTestEntities(mContext, SystemRemindEntity.class));
            return;
        }
        ArrayList<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause1 = new WhereClause();
        whereClause1.setFields("operatorteamid");
        whereClause1.setFieldKey(FieldKey.EQUALS);
        whereClause1.setValueKey(CacheDataSource.getTeamId());
        whereClause1.setJoinKey(JoinKey.OR);

        WhereClause whereClause2 = new WhereClause();
        whereClause2.setFields("monitorteamid");
        whereClause2.setFieldKey(FieldKey.EQUALS);
        whereClause2.setValueKey(CacheDataSource.getTeamId());
        whereClause2.setJoinKey(JoinKey.OTHER);
        whereClauses.add(whereClause1);
        whereClauses.add(whereClause2);

        ArrayList<OrderBy> orderBies = new ArrayList<>();
        OrderBy orderBy = new OrderBy();
        orderBy.setMode(Mode.ASC);
        orderBy.setField("sendtime");
        orderBies.add(orderBy);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, orderBies, ViewName.TASK_REMIND_INFO,
                1, new ResponseListener<ResponseForQueryData<List<SystemRemindEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<SystemRemindEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<SystemRemindEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<SystemRemindEntity>> result) {
                        mView.getRemindDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getRemindDataFailed();
                    }
                });
    }

    /**
     * 处理提醒信息，已读/删除(可以是多条)
     */
    @Override
    public void handleReminds(List<SystemRemindEntity> reminds, int handleType) {
        MessageHandling messageHandling = new MessageHandling();
        ArrayList<String> ids = new ArrayList<>();
        for (SystemRemindEntity bean : reminds) {
            ids.add(bean.getId());
        }
        messageHandling.setId(ids);
        messageHandling.setHandle(handleType);
        NetDataSource.post(this, Urls.MESSAGE_HANDLING, messageHandling, 0, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                mView.handleRemindsSuccess(reminds, handleType);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                mView.handleRemindsFailed(handleType);
            }
        });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
