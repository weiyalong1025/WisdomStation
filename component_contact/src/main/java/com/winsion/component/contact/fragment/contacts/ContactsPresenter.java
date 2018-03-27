package com.winsion.component.contact.fragment.contacts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.constants.Mode;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.OrderBy;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.contact.entity.ContactEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Presenter
 */

public class ContactsPresenter implements ContactsContract.Presenter {
    private ContactsContract.View mView;

    ContactsPresenter(ContactsContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getContactsData() {
        List<OrderBy> orderBy = new ArrayList<>();

        OrderBy order = new OrderBy();
        order.setField("loginstatus");
        order.setMode(Mode.ASC);
        orderBy.add(order);

        NetDataSource.post(this, Urls.BASE_QUERY, null, orderBy, ViewName.TEAM_USERS_INFO,
                1, new ResponseListener<ResponseForQueryData<List<ContactEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<ContactEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<ContactEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<ContactEntity>> result) {
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
