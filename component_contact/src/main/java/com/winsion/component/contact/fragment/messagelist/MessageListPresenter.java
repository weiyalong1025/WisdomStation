package com.winsion.component.contact.fragment.messagelist;

import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.entity.UserMessageList;

import java.util.List;

class MessageListPresenter implements MessageListContract.Presenter, DBDataSource.OnDataChangeListener {
    private MessageListContract.View mView;
    private DBDataSource dbDataSource;

    MessageListPresenter(MessageListContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {
        dbDataSource = DBDataSource.getInstance(mView.getContext());
        dbDataSource.addOnDataChangeListener(this);
    }

    @Override
    public List<UserMessageList> getMessageListData() {
        return dbDataSource.getMessageList(CacheDataSource.getUserId());
    }

    @Override
    public void onMessageChange(UserMessage userMessage, int messageState) {
        mView.notifyDataChange();
    }

    @Override
    public void onMessageListChange(UserMessageList userMessageList, int messageState) {
        mView.notifyDataChange();
    }

    @Override
    public void exit() {
        dbDataSource.removeOnDataChangeListener(this);
    }
}
