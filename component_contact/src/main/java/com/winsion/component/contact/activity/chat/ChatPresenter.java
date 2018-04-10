package com.winsion.component.contact.activity.chat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;
import android.text.TextUtils;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.constants.MessageType;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.entity.UserMessage;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.contact.R;
import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.entity.ContactEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View mView;
    private Context mContext;
    private long startTime;
    private File voiceFile;
    private MediaRecorder mRecorder;
    private DBDataSource dbDataSource;
    private ContactEntity contactEntity;

    ChatPresenter(ChatContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        contactEntity = mView.getContactEntity();
        dbDataSource = DBDataSource.getInstance(mContext);
    }

    @Override
    public void sendText(String msg) {
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

        dbDataSource.saveMessage(userMessage);
        mView.sendMessageSuccess(userMessage);
    }

    @Override
    public void sendImage(File file) {
        UserMessage userMessage = new UserMessage();
        userMessage.setType(MessageType.PICTURE);
        sendFile(file, userMessage);
    }

    @Override
    public void sendVideo(File file) {
        UserMessage userMessage = new UserMessage();
        userMessage.setType(MessageType.VIDEO);
        sendFile(file, userMessage);
    }

    @Override
    public void startRecord() {
        try {
            mView.showRecordView();
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
            voiceFile = BasicBiz.getMediaFile(DirAndFileUtils.getIMDir(), FileType.AUDIO);
            startTime = System.currentTimeMillis();
            mRecorder.setOutputFile(voiceFile.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            ToastUtils.showToast(mContext, R.string.toast_check_sdcard);
            mView.hideRecordView();
        }
    }

    @Override
    public void stopRecord() {
        mView.hideRecordView();
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        // 将录音发送出去并发送给服务器
        if (System.currentTimeMillis() - startTime > 1000) {
            UserMessage userMessage = new UserMessage();
            userMessage.setType(MessageType.VOICE);
            sendFile(voiceFile, userMessage);
        } else {
            ToastUtils.showToast(mContext, "录音时间太短");
        }
    }

    private UserMessage draftMessage;

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
    public void updateDraft() {
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
                dbDataSource.saveMessage(draftMessage);
            }
        } else {
            if (!TextUtils.isEmpty(inputText)) {
                draftMessage.setContent(inputText);
                dbDataSource.saveMessage(draftMessage);
            } else {
                dbDataSource.deleteMessage(draftMessage);
            }
        }
    }

    private void sendFile(File file, UserMessage userMessage) {
        userMessage.setTime(System.currentTimeMillis());
        userMessage.setSenderId(CacheDataSource.getUserId());
        userMessage.setSenderMmpId(CacheDataSource.getSipUsername());
        userMessage.setSenderName(CacheDataSource.getRealName());
        userMessage.setReceiverId(contactEntity.getConId());
        userMessage.setReceiverMmpId(contactEntity.getConMmpId());
        userMessage.setReceiverName(contactEntity.getConName());
        /*userMessage.setSenderId(contactEntity.getConId());
        userMessage.setSenderMmpId(contactEntity.getConMmpId());
        userMessage.setSenderName(contactEntity.getConName());

        userMessage.setReceiverId(CacheDataSource.getUserId());
        userMessage.setReceiverMmpId(CacheDataSource.getSipUsername());
        userMessage.setReceiverName(CacheDataSource.getRealName());*/
        userMessage.setBelongUserId(CacheDataSource.getUserId());
        userMessage.setStatus(MessageStatus.SENDING);
        userMessage.setContactType(contactEntity.getConType());
        userMessage.setDescription(file.getAbsolutePath());

        dbDataSource.saveMessage(userMessage);
        mView.sendMessageSuccess(userMessage);
    }

    public static void resendMessage(UserMessage userMessage) {

    }

    @Override
    public void exit() {

    }
}
