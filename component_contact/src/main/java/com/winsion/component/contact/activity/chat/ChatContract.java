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

        ContactEntity getContactEntity();

        void showDraft(String draft);

        String getInputText();
    }

    interface Presenter extends BasePresenter {
        void sendTextMessage(String msg);

        void sendFileMessage(File file, int messageType);

        void startRecord(File voiceFile);

        boolean stopRecord(File voiceFile);

        List<UserMessage> loadMessage();
    }
}
