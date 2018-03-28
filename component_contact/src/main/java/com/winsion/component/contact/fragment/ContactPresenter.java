package com.winsion.component.contact.fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.ContactsGroupEntity;
import com.winsion.component.contact.entity.TeamEntity;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Presenter
 */

public class ContactPresenter<T> implements ContactContract.Presenter {
    private ContactContract.View<T> mView;

    ContactPresenter(ContactContract.View<T> view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getContactData(@ContactType int contactType) {
        /*List<OrderBy> orderBy = new ArrayList<>();

        OrderBy order = new OrderBy();
        order.setField("loginstatus");
        order.setMode(Mode.ASC);
        orderBy.add(order);*/
        String viewName;
        Type type;
        switch (contactType) {
            case ContactType.TYPE_CONTACTS:
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
                1, new ResponseListener<ResponseForQueryData<List<T>>>() {
                    @Override
                    public ResponseForQueryData<List<T>> convert(String jsonStr) {
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<T>> result) {
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
