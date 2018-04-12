package com.winsion.component.contact.fragment.contact;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.contact.entity.ContactEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 联系人Contract
 */

class ContactContract {
    interface View extends BaseView {
        void getContactsDataSuccess(List<? extends ContactEntity> contactEntities);

        void getContactsDataFailed();

        void notifyDataChange();
    }

    interface Presenter extends BasePresenter {
        void getContactData(@ContactType int contactType);
    }
}
