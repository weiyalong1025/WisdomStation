package com.winsion.component.contact.activity.chat;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View mView;

    ChatPresenter(ChatContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void exit() {

    }
}
