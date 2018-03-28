package com.winsion.component.contact.fragment;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.contact.constants.ContactType;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Contract
 */

class ContactContract {
    interface View<T> extends BaseView {
        void getContactsDataSuccess(List<T> contactEntities);

        void getContactsDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getContactData(@ContactType int contactType);
    }
}
