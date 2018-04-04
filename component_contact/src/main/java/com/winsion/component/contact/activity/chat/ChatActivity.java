package com.winsion.component.contact.activity.chat;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.winsion.component.basic.activity.RecordVideoActivity;
import com.winsion.component.basic.activity.TakePhotoActivity;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.basic.view.TextImageButton;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.contact.R;
import com.winsion.component.contact.entity.ContactEntity;
import com.winsion.component.contact.entity.MyMessage;
import com.winsion.component.contact.entity.MyUser;

import java.io.File;
import java.io.IOException;

import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;

import static com.winsion.component.basic.constants.Intents.Media.MEDIA_FILE;

public class ChatActivity extends BaseActivity implements ChatContract.View {
    private TitleView tvTitle;
    private MessageList msgList;
    private CheckBox cbType;
    private ImageView ivChangeType;
    private EditText etInput;
    private ImageView ivPic;
    private TextImageButton btnSend;
    private TextImageButton btnRecord;
    private LinearLayout llPic;
    private View viewShader;
    private Button btnCancel;
    private ImageView ivRecordView;

    private static final int CODE_TAKE_PHOTO = 0;
    private static final int CODE_RECORD_VIDEO = 1;
    private static final int CODE_SELECT_PIC = 2;

    private ChatContract.Presenter mPresenter;
    private ContactEntity contactEntity;
    private MsgListAdapter<MyMessage> adapter;
    private MyUser myUser;
    private boolean isSelectPicViewShowing = false;
    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private File photoFile;
    private File videoFile;

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
        llPic = findViewById(R.id.ll_pic);
        viewShader = findViewById(R.id.view_shader);
        btnCancel = findViewById(R.id.btn_cancel);
        ivRecordView = findViewById(R.id.iv_record_view);
    }

    private void initPresenter() {
        mPresenter = new ChatPresenter(this);
    }

    private void initIntentData() {
        contactEntity = (ContactEntity) getIntent().getSerializableExtra("ContactEntity");
        tvTitle.setTitleText(contactEntity.getConName());
        myUser = new MyUser(contactEntity.getConId(), contactEntity.getConName(), contactEntity.getConPhotoUrl());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAdapter() {
        adapter = new MsgListAdapter<>(contactEntity.getConId(), new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                com.winsion.component.basic.utils.ImageLoader.loadAddress(avatarImageView, string,
                        R.drawable.basic_ic_head_single,
                        R.drawable.basic_ic_head_single);
            }

            @Override
            public void loadImage(ImageView imageView, String string) {
                com.winsion.component.basic.utils.ImageLoader.loadAddress(imageView, string);
            }
        });
        adapter.setOnMsgClickListener(message -> {
            int fileType = message.getType();
            String type = fileType == IMessage.MessageType.SEND_IMAGE.ordinal() || fileType == IMessage.MessageType.RECEIVE_IMAGE.ordinal() ? "image/*"
                    : fileType == IMessage.MessageType.SEND_VIDEO.ordinal() || fileType == IMessage.MessageType.RECEIVE_VIDEO.ordinal() ? "video/*" : "";
            if (type.equals("")) return;
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(new File(message.getMediaFilePath()));
                intent.setDataAndType(uri, type);
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                ToastUtils.showToast(mContext, com.winsion.component.basic.R.string.toast_no_corresponding_program);
            }
        });
        msgList.setAdapter(adapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        viewShader.setOnClickListener(v -> hideSelectPicView(null));
        cbType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etInput.setVisibility(View.GONE);
                ivPic.setVisibility(View.GONE);
                btnSend.setVisibility(View.GONE);
                btnRecord.setVisibility(View.VISIBLE);
                BasicBiz.hideKeyboard(etInput);
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
                    btnRecord.setText(R.string.contact_btn_release_to_finish);
                    mPresenter.startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    btnRecord.setBackgroundResource(R.drawable.contact_btn_record_normal);
                    btnRecord.setText(R.string.contact_btn_press_to_talk);
                    mPresenter.stopRecord();
                    break;
            }
            return true;
        });
        msgList.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                msgList.scrollToPosition(0));
        msgList.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                BasicBiz.hideKeyboard(etInput);
                etInput.clearFocus();
            }
            return false;
        });
        addOnClickListeners(R.id.iv_pic, R.id.btn_send, R.id.btn_cancel, R.id.btn_take_photo,
                R.id.btn_record_video, R.id.btn_select_pic);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.iv_pic) {
            showSelectPicView();
        } else if (id == R.id.btn_send) {
            String text = getText(etInput);
            if (!isEmpty(text)) {
                mPresenter.sendText(text);
                etInput.setText("");
            }
        } else if (id == R.id.btn_cancel) {
            btnCancel.setEnabled(false);
            hideSelectPicView(() -> btnCancel.setEnabled(true));
        } else if (id == R.id.btn_take_photo) {
            hideSelectPicView(() -> {
                try {
                    photoFile = BasicBiz.getMediaFile(DirAndFileUtils.getIMDir(), FileType.PICTURE);
                    Intent intent = new Intent(mContext, TakePhotoActivity.class);
                    intent.putExtra(MEDIA_FILE, photoFile);
                    startActivityForResult(intent, CODE_TAKE_PHOTO);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
            });
        } else if (id == R.id.btn_record_video) {
            hideSelectPicView(() -> {
                try {
                    videoFile = BasicBiz.getMediaFile(DirAndFileUtils.getIMDir(), FileType.VIDEO);
                    Intent intent = new Intent(mContext, RecordVideoActivity.class);
                    intent.putExtra(MEDIA_FILE, videoFile);
                    startActivityForResult(intent, CODE_RECORD_VIDEO);
                } catch (IOException e) {
                    showToast(R.string.toast_check_sdcard);
                }
            });
        } else if (id == R.id.btn_select_pic) {
            hideSelectPicView(() -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CODE_SELECT_PIC);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_TAKE_PHOTO:
                    mPresenter.sendImage(photoFile);
                    break;
                case CODE_RECORD_VIDEO:
                    mPresenter.sendVideo(videoFile);
                    break;
                case CODE_SELECT_PIC:
                    String realFilePath = BasicBiz.getRealFilePath(mContext, data.getData());
                    if (realFilePath != null) {
                        mPresenter.sendImage(new File(realFilePath));
                    }
                    break;
            }
        }
    }

    private void showSelectPicView() {
        isSelectPicViewShowing = true;
        // 隐藏键盘
        BasicBiz.hideKeyboard(etInput);
        viewShader.setVisibility(View.VISIBLE);
        llPic.setVisibility(View.VISIBLE);
        if (showAnimation == null) {
            showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);
            showAnimation.setDuration(250);
        }
        llPic.startAnimation(showAnimation);
    }

    private void hideSelectPicView(Runnable onAnimationEnd) {
        isSelectPicViewShowing = false;
        if (hideAnimation == null) {
            hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1);
            hideAnimation.setDuration(250);
        }
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onAnimationEnd != null) {
                    mHandler.post(onAnimationEnd);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        llPic.startAnimation(hideAnimation);
        llPic.setVisibility(View.GONE);
        viewShader.setVisibility(View.GONE);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isSelectPicViewShowing) {
            hideSelectPicView(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void sendMessageSuccess(MyMessage myMessage) {
        adapter.addToStart(myMessage, true);
    }

    @Override
    public ContactEntity getContactEntity() {
        return contactEntity;
    }

    @Override
    public void showRecordView() {
        ivRecordView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecordView() {
        ivRecordView.setVisibility(View.GONE);
    }
}
