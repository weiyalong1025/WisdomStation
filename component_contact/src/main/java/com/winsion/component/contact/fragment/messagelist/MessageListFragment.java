package com.winsion.component.contact.fragment.messagelist;

import android.view.View;

import com.winsion.component.basic.base.BaseFragment;

public class MessageListFragment extends BaseFragment implements MessageListContract.View {
    private MessageListContract.Presenter mPresenter;

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void init() {
        initView();
        initPresenter();
    }

    private void initView() {

    }

    private void initPresenter() {
        mPresenter = new MessageListPresenter(this);
    }
}
