package com.winsion.component.contact.fragment.messagelist;

import android.annotation.SuppressLint;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.contact.R;

public class MessageListFragment extends BaseFragment implements MessageListContract.View {
    private MessageListContract.Presenter mPresenter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.basic_layout_status, null);
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
