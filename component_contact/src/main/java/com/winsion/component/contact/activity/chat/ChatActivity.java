package com.winsion.component.contact.activity.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.view.TextImageButton;
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
    private CheckBox cbType;
    private ImageView ivChangeType;
    private EditText etInput;
    private ImageView ivPic;
    private TextImageButton btnSend;
    private TextImageButton btnRecord;

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
        cbType = findViewById(R.id.cb_type);
        etInput = findViewById(R.id.et_input);
        ivPic = findViewById(R.id.iv_pic);
        btnSend = findViewById(R.id.btn_send);
        btnRecord = findViewById(R.id.btn_record);

        BasicBiz.showKeyboard(etInput, false);
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

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        cbType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etInput.setVisibility(View.GONE);
                ivPic.setVisibility(View.GONE);
                btnSend.setVisibility(View.GONE);
                btnRecord.setVisibility(View.VISIBLE);
                BasicBiz.hideKeyboard(buttonView);
            } else {
                etInput.setVisibility(View.VISIBLE);
                ivPic.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.VISIBLE);
                btnRecord.setVisibility(View.GONE);
                BasicBiz.showKeyboard(etInput, false);
            }
        });
        btnRecord.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btnRecord.setBackgroundResource(R.drawable.contact_btn_record_press);
                    btnRecord.setText(R.string.btn_release_to_finish);
                    break;
                case MotionEvent.ACTION_UP:
                    btnRecord.setBackgroundResource(R.drawable.contact_btn_record_normal);
                    btnRecord.setText(R.string.btn_press_to_talk);
                    break;
            }
            return true;
        });
        addOnClickListeners(R.id.iv_pic, R.id.btn_send);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.iv_pic) {
            showToast("选择图片");
        } else if (id == R.id.btn_send) {
            showToast("发送");
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
