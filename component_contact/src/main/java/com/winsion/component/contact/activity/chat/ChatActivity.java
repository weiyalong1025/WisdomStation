package com.winsion.component.contact.activity.chat;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.contact.R;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.MyMessage;
import com.winsion.component.contact.entity.MyUser;

import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;

public class ChatActivity extends BaseActivity implements ChatContract.View {
    private TitleView tvTitle;
    private MessageList msgList;

    private ChatContract.Presenter mPresenter;
    private ContactsEntity contactsEntity;
    private MsgListAdapter<MyMessage> adapter;
    private MyUser myUser;

    @Override
    protected int setContentView() {
        return R.layout.contact_activity_chat;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initIntentData();
        initAdapter();
        initListener();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        msgList = findViewById(R.id.msg_list);
    }

    private void initPresenter() {
        mPresenter = new ChatPresenter(this);
    }

    private void initIntentData() {
        contactsEntity = (ContactsEntity) getIntent().getSerializableExtra("ContactsEntity");
        tvTitle.setTitleText(contactsEntity.getUsername());
        myUser = new MyUser(contactsEntity.getUsersid(), contactsEntity.getUsername(), contactsEntity.getPhotourl());
    }

    private void initAdapter() {
        adapter = new MsgListAdapter<>(contactsEntity.getUsersid(), new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                com.winsion.component.basic.utils.ImageLoader.loadUrl(avatarImageView, string,
                        R.drawable.basic_ic_head_single,
                        R.drawable.basic_ic_head_single);
            }

            @Override
            public void loadImage(ImageView imageView, String string) {

            }
        });
        msgList.setAdapter(adapter);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
