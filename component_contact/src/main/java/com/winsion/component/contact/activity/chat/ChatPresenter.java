package com.winsion.component.contact.activity.chat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;
import android.text.TextUtils;

import com.winsion.component.basic.constants.ContactType;
import com.winsion.component.basic.constants.MessageType;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.entity.UserMessageList;
import com.winsion.component.contact.R;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.entity.ContactEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatPresenter implements ChatContract.Presenter, DBDataSource.OnDataChangeListener {
    private ChatContract.View mView;
    private Context mContext;
    private MediaRecorder mRecorder;
    private DBDataSource dbDataSource;
    private ContactEntity contactEntity;    // 联系人/班组/联系人组
    private UserMessage draftMessage;   // 草稿消息

    ChatPresenter(ChatContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        contactEntity = mView.getContactEntity();
        dbDataSource = DBDataSource.getInstance(mContext);
        dbDataSource.clearUnreadCount(CacheDataSource.getUserId(), contactEntity.getConId());
        dbDataSource.addOnDataChangeListener(this);
    }

    /**
     * 发送文字消息
     *
     * @param msg 文字消息
     */
    @Override
    public void sendTextMessage(String msg) {
        UserMessage userMessage = new UserMessage();
        userMessage.setTime(System.currentTimeMillis());
        userMessage.setType(MessageType.WORD);
        userMessage.setSenderId(CacheDataSource.getUserId());
        userMessage.setSenderMmpId(CacheDataSource.getSipUsername());
        userMessage.setSenderName(CacheDataSource.getRealName());
        userMessage.setReceiverId(contactEntity.getConId());
        userMessage.setReceiverMmpId(contactEntity.getConMmpId());
        userMessage.setReceiverName(contactEntity.getConName());
        userMessage.setContent(msg);
        userMessage.setBelongUserId(CacheDataSource.getUserId());
        userMessage.setStatus(MessageStatus.SENDING);
        userMessage.setContactType(contactEntity.getConType());

        dbDataSource.saveMessage(userMessage, true);
    }

    /**
     * 开始录音
     */
    @Override
    public void startRecord(File voiceFile) {
        try {
            // 震动一下
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(new long[]{0, 50}, -1);
            }
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置封装格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            // 设置编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile(voiceFile.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    @Override
    public boolean stopRecord(File voiceFile) {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 加载消息列表数据
     *
     * @return 消息列表数据
     */
    @Override
    public List<UserMessage> loadMessage() {
        List<UserMessage> messages = new ArrayList<>();
        int conType = contactEntity.getConType();
        String userId = CacheDataSource.getUserId();
        String conId = contactEntity.getConId();
        switch (conType) {
            case ContactType.TYPE_CONTACTS:
                messages = dbDataSource.getSingMessage(userId, conId);
                break;
            case ContactType.TYPE_TEAM:
            case ContactType.TYPE_CONTACTS_GROUP:
                messages = dbDataSource.getGroupMessage(CacheDataSource.getUserId(), contactEntity.getConId());
                break;
        }

        draftMessage = dbDataSource.getDraft(conType != ContactType.TYPE_CONTACTS, userId, conId);
        if (draftMessage != null) {
            mView.showDraft(draftMessage.getContent());
        }
        return messages;
    }

    @Override
    public void sendFileMessage(File file, int messageType) {
        String content = null;
        switch (messageType) {
            case MessageType.PICTURE:
                content = mContext.getResources().getString(R.string.value_picture);
                break;
            case MessageType.VIDEO:
                content = mContext.getResources().getString(R.string.value_video);
                break;
            case MessageType.VOICE:
                content = mContext.getResources().getString(R.string.value_audio);
                break;
        }

        UserMessage userMessage = new UserMessage();
        userMessage.setType(messageType);
        userMessage.setContent(content);
        userMessage.setTime(System.currentTimeMillis());
        userMessage.setSenderId(CacheDataSource.getUserId());
        userMessage.setSenderMmpId(CacheDataSource.getSipUsername());
        userMessage.setSenderName(CacheDataSource.getRealName());
        userMessage.setReceiverId(contactEntity.getConId());
        userMessage.setReceiverMmpId(contactEntity.getConMmpId());
        userMessage.setReceiverName(contactEntity.getConName());
        userMessage.setBelongUserId(CacheDataSource.getUserId());
        userMessage.setStatus(MessageStatus.SENDING);
        userMessage.setContactType(contactEntity.getConType());
        userMessage.setDescription(file.getAbsolutePath());

        dbDataSource.saveMessage(userMessage, false);
    }

    /**
     * 发送消息
     *
     * @param userMessage
     */
    public static void resendMessage(UserMessage userMessage) {

    }

    @Override
    public void onMessageChange(UserMessage userMessage, int messageState) {
        if (userMessage.getContactType() != ContactType.TYPE_CONTACTS) {
            if (userMessage.getReceiverId().equals(contactEntity.getConId())) {
                mView.sendMessageSuccess(userMessage);
            }
        } else {
            if (userMessage.getReceiverId().equals(contactEntity.getConId()) ||
                    userMessage.getSenderId().equals(contactEntity.getConId())) {
                mView.sendMessageSuccess(userMessage);
            }
        }
    }

    @Override
    public void onMessageListChange(UserMessageList userMessageList, int messageState) {

    }

    @Override
    public void exit() {
        dbDataSource.removeOnDataChangeListener(this);
        updateDraft();
    }

    /**
     * 更新草稿消息
     */
    private void updateDraft() {
        String inputText = mView.getInputText();
        if (draftMessage == null) {
            if (!TextUtils.isEmpty(inputText)) {
                draftMessage = new UserMessage();
                draftMessage.setContent(inputText);
                draftMessage.setTime(System.currentTimeMillis());
                draftMessage.setType(MessageType.DRAFT);
                draftMessage.setSenderId(CacheDataSource.getUserId());
                draftMessage.setSenderMmpId(CacheDataSource.getSipUsername());
                draftMessage.setSenderName(CacheDataSource.getRealName());
                draftMessage.setReceiverId(contactEntity.getConId());
                draftMessage.setReceiverMmpId(contactEntity.getConMmpId());
                draftMessage.setReceiverName(contactEntity.getConName());
                draftMessage.setBelongUserId(CacheDataSource.getUserId());
                draftMessage.setContactType(contactEntity.getConType());
                dbDataSource.saveMessage(draftMessage, false);
            }
        } else {
            if (!TextUtils.isEmpty(inputText)) {
                draftMessage.setContent(inputText);
                dbDataSource.saveMessage(draftMessage, false);
            } else {
                dbDataSource.deleteMessage(draftMessage);
            }
        }
    }
}
