package com.winsion.component.contact.fragment.messagelist;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.entity.UserMessageList;

import java.util.List;

class MessageListContract {
    interface View extends BaseView {
        void notifyDataChange();
    }

    interface Presenter extends BasePresenter {
        List<UserMessageList> getMessageListData();
    }
}
