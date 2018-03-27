package com.winsion.component.contact.fragment.contacts;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.contact.entity.ContactEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Contract
 */

class ContactsContract {
    interface View extends BaseView {
        void getContactsDataSuccess(List<ContactEntity> contactEntities);

        void getContactsDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getContactsData();
    }
}
