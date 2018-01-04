package com.winsion.wisdomstation.modules.reminder.modules.systemremind.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.FieldKey;
import com.winsion.wisdomstation.data.constants.JoinKey;
import com.winsion.wisdomstation.data.constants.Mode;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.constants.ViewName;
import com.winsion.wisdomstation.data.entity.OrderBy;
import com.winsion.wisdomstation.data.entity.ResponseForQueryData;
import com.winsion.wisdomstation.data.entity.WhereClause;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.modules.reminder.entity.MessageHandling;
import com.winsion.wisdomstation.modules.reminder.entity.RemindEntity;

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

    SystemRemindPresenter(SystemRemindContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getRemindData() {
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
        orderBy.setMode(Mode.DESC);
        orderBy.setField("sendtime");
        orderBies.add(orderBy);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, orderBies, ViewName.TASK_REMIND_INFO,
                1, new ResponseListener<ResponseForQueryData<List<RemindEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<RemindEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<RemindEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<RemindEntity>> result) {
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
    public void handleReminds(List<RemindEntity> reminds, int handleType) {
        MessageHandling messageHandling = new MessageHandling();
        ArrayList<String> ids = new ArrayList<>();
        for (RemindEntity bean : reminds) {
            ids.add(bean.getId());
        }
        messageHandling.setId(ids);
        messageHandling.setHandle(handleType);
        NetDataSource.post(getClass(), Urls.MESSAGE_HANDLING, messageHandling, 0, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                mView.handleRemindsSuccess(reminds,handleType);
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
