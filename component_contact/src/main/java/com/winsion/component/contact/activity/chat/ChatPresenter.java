package com.winsion.component.contact.activity.chat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.contact.R;
import com.winsion.component.contact.constants.MessageStatus;
import com.winsion.component.contact.constants.MessageType;
import com.winsion.component.contact.entity.UserMessage;

import java.io.File;
import java.io.IOException;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View mView;
    private Context mContext;
    private long startTime;
    private File voiceFile;
    private MediaRecorder mRecorder;

    ChatPresenter(ChatContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void sendText(String msg) {
        UserMessage userMessage = new UserMessage();
        userMessage.setSenderId(CacheDataSource.getUserId());
        userMessage.setType(MessageType.WORD);
        userMessage.setStatus(MessageStatus.SEND_SUCCESS);
        userMessage.setContent(msg);
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

    private void sendFile(File file, UserMessage userMessage) {
        userMessage.setSenderId(CacheDataSource.getUserId());
        userMessage.setStatus(MessageStatus.SEND_SUCCESS);
        userMessage.setDescription(file.getAbsolutePath());
        mView.sendMessageSuccess(userMessage);
    }

    public static void resendMessage(UserMessage myMessage) {

    }

    @Override
    public void exit() {

    }
}
