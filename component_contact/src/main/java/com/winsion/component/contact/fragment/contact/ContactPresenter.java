package com.winsion.component.contact.fragment.contact;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.entity.UserMessageList;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.utils.JsonUtils;
import com.winsion.component.contact.entity.ContactEntity;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.ContactsGroupEntity;
import com.winsion.component.contact.entity.TeamEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Presenter
 */

public class ContactPresenter implements ContactContract.Presenter, DBDataSource.OnDataChangeListener {
    private ContactContract.View mView;

    ContactPresenter(ContactContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {
        DBDataSource.getInstance(mView.getContext()).addOnDataChangeListener(this);
    }

    @Override
    public void getContactData(@ContactType int contactType) {
        String viewName;
        Type type;
        List<WhereClause> whereClauses = new ArrayList<>();
        switch (contactType) {
            case ContactType.TYPE_CONTACTS:
                if (CacheDataSource.getTestMode()) {
                    List<ContactsEntity> testEntities = JsonUtils.getTestEntities(mView.getContext(), ContactsEntity.class);
                    mView.getContactsDataSuccess(testEntities);
                    return;
                }
                viewName = ViewName.TEAM_USERS_INFO;
                type = new TypeReference<ResponseForQueryData<List<ContactsEntity>>>() {
                }.getType();
                WhereClause whereClause = new WhereClause();
                whereClause.setFieldKey(FieldKey.NOEQUAL);
                whereClause.setFields("usersid");
                whereClause.setJoinKey(JoinKey.OTHER);
                whereClause.setValueKey(CacheDataSource.getUserId());
                whereClauses.add(whereClause);
                break;
            case ContactType.TYPE_TEAM:
                viewName = ViewName.TEAMS_INFO;
                type = new TypeReference<ResponseForQueryData<List<TeamEntity>>>() {
                }.getType();
                break;
            case ContactType.TYPE_CONTACTS_GROUP:
                viewName = ViewName.USER_GROUP_INFO;
                type = new TypeReference<ResponseForQueryData<List<ContactsGroupEntity>>>() {
                }.getType();
                break;
            default:
                viewName = ViewName.TEAM_USERS_INFO;
                type = new TypeReference<ResponseForQueryData<List<ContactsEntity>>>() {
                }.getType();
                break;
        }

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, null, viewName,
                1, new ResponseListener<ResponseForQueryData<List<? extends ContactEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<? extends ContactEntity>> convert(String jsonStr) {
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<? extends ContactEntity>> result) {
                        List<? extends ContactEntity> dataList = result.getDataList();
                        mView.getContactsDataSuccess(dataList);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getContactsDataFailed();
                    }
                });
    }

    @Override
    public void onMessageChange(UserMessage userMessage, int messageState) {
        mView.notifyDataChange();
    }

    @Override
    public void onMessageListChange(UserMessageList userMessageList, int messageState) {

    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        DBDataSource.getInstance(mView.getContext()).removeOnDataChangeListener(this);
    }
}
