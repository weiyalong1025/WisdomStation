package com.winsion.component.contact.activity.chat;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.contact.entity.ContactEntity;
import com.winsion.component.contact.entity.MyMessage;

import java.io.File;

class ChatContract {
    interface View extends BaseView {
        void sendMessageSuccess(MyMessage myMessage);

        void showRecordView();

        void hideRecordView();

        ContactEntity getContactEntity();
    }

    interface Presenter extends BasePresenter {
        void sendText(String msg);

        void sendImage(File file);

        void sendVideo(File file);

        void startRecord();

        void stopRecord();
    }
}
