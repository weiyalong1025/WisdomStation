package com.winsion.component.contact.fragment.contact;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.utils.JsonUtils;
import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.entity.ContactEntity;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.ContactsGroupEntity;
import com.winsion.component.contact.entity.TeamEntity;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Presenter
 */

public class ContactPresenter implements ContactContract.Presenter {
    private ContactContract.View mView;

    ContactPresenter(ContactContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getContactData(@ContactType int contactType) {
        String viewName;
        Type type;
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

        NetDataSource.post(this, Urls.BASE_QUERY, null, null, viewName,
                1, new ResponseListener<ResponseForQueryData<List<? extends ContactEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<? extends ContactEntity>> convert(String jsonStr) {
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<? extends ContactEntity>> result) {
                        mView.getContactsDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getContactsDataFailed();
                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
