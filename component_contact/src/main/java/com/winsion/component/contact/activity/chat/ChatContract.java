package com.winsion.component.contact.activity.chat;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.contact.entity.ContactEntity;

import java.io.File;
import java.util.List;

class ChatContract {
    interface View extends BaseView {
        void sendMessageSuccess(UserMessage userMessage);

        void showRecordView();

        void hideRecordView();

        ContactEntity getContactEntity();

        void showDraft(String draft);

        String getInputText();
    }

    interface Presenter extends BasePresenter {
        void sendText(String msg);

        void sendImage(File file);

        void sendVideo(File file);

        void startRecord();

        void stopRecord();

        List<UserMessage> loadMessage();

        void updateDraft();
    }
}
