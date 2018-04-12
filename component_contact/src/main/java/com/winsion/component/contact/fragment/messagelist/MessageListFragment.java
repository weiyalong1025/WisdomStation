package com.winsion.component.contact.fragment.messagelist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.entity.UserMessageList;
import com.winsion.component.contact.R;
import com.winsion.component.contact.activity.chat.ChatActivity;
import com.winsion.component.contact.adapter.MessageListAdapter;
import com.winsion.component.contact.entity.ContactPasser;

import java.util.ArrayList;
import java.util.List;

public class MessageListFragment extends BaseFragment implements MessageListContract.View, AdapterView.OnItemClickListener {
    private FrameLayout flContainer;
    private ListView lvList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvHint;

    private MessageListContract.Presenter mPresenter;
    private List<UserMessageList> mListData = new ArrayList<>();
    private MessageListAdapter mLvAdapter;

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.basic_layout_status, null);
    }

    @Override
    protected void init() {
        initView();
        initPresenter();
        initAdapter();
        initData();
    }

    private void initView() {
        flContainer = findViewById(R.id.fl_container);
        lvList = findViewById(R.id.lv_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tvHint = findViewById(R.id.tv_hint);
        swipeRefreshLayout.setEnabled(false);
    }

    private void initPresenter() {
        mPresenter = new MessageListPresenter(this);
        mPresenter.start();
    }

    private void initAdapter() {
        mLvAdapter = new MessageListAdapter(mContext, mListData);
        lvList.setAdapter(mLvAdapter);
        lvList.setOnItemClickListener(this);
    }

    private void initData() {
        List<UserMessageList> messageListData = mPresenter.getMessageListData();
        if (messageListData.size() == 0) {
            tvHint.setText(R.string.hint_no_data);
            showView(flContainer, tvHint);
        } else {
            mListData.clear();
            mListData.addAll(messageListData);
            mLvAdapter.notifyDataSetChanged();
            showView(flContainer, swipeRefreshLayout);
            // 更新角标
            int unreadCount = 0;
            for (UserMessageList userMessageList : mListData) {
                unreadCount += userMessageList.getUnreadCount();
            }
            CC.obtainBuilder("ComponentRemind")
                    .setActionName("updateMessageListBadge")
                    .addParam("fragment", getParentFragment())
                    .addParam("badgeNum", unreadCount)
                    .build()
                    .call();
        }
    }

    @Override
    public void notifyDataChange() {
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserMessageList userMessageList = mListData.get(position);
        Intent intent = new Intent(mContext, ChatActivity.class);

        ContactPasser contactPasser = new ContactPasser();
        contactPasser.setConId(userMessageList.getChatToId());
        contactPasser.setConMmpId(userMessageList.getChatToMmpId());
        contactPasser.setConName(userMessageList.getChatToName());
        contactPasser.setConType(userMessageList.getContactType());

        intent.putExtra("ContactEntity", contactPasser);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
