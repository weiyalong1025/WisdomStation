package com.winsion.component.contact.activity.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Vibrator;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.contact.R;
import com.winsion.component.contact.entity.ContactEntity;
import com.winsion.component.contact.entity.MyMessage;
import com.winsion.component.contact.entity.MyUser;

import java.io.File;
import java.io.IOException;

import cn.jiguang.imui.commons.models.IMessage;

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
        MyMessage myMessage = new MyMessage();
        myMessage.setText(msg);
        ContactEntity contactEntity = mView.getContactEntity();
        myMessage.setUser(new MyUser(contactEntity.getConId(), contactEntity.getConName(), contactEntity.getConPhotoUrl()));
        myMessage.setType(IMessage.MessageType.SEND_TEXT.ordinal());
        myMessage.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
        mView.sendMessageSuccess(myMessage);
    }

    @Override
    public void sendImage(File file) {
        MyMessage myMessage = new MyMessage();
        myMessage.setType(IMessage.MessageType.SEND_IMAGE.ordinal());
        sendFile(file, myMessage);
    }

    @Override
    public void sendVideo(File file) {
        MyMessage myMessage = new MyMessage();
        myMessage.setType(IMessage.MessageType.SEND_VIDEO.ordinal());
        int duration = MediaPlayer.create(mContext, Uri.fromFile(file)).getDuration() / 1000;
        myMessage.setDuration(duration);
        sendFile(file, myMessage);
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
        if (System.currentTimeMillis() - startTime > 2000) {
            MyMessage myMessage = new MyMessage();
            myMessage.setType(IMessage.MessageType.SEND_VOICE.ordinal());
            int duration = MediaPlayer.create(mContext, Uri.fromFile(voiceFile)).getDuration() / 1000;
            myMessage.setDuration(duration);
            sendFile(voiceFile, myMessage);
        } else {
            ToastUtils.showToast(mContext, "录音时间太短");
        }
    }

    private void sendFile(File file, MyMessage myMessage) {
        myMessage.setMediaFilePath(file.getAbsolutePath());
        ContactEntity contactEntity = mView.getContactEntity();
        myMessage.setUser(new MyUser(contactEntity.getConId(), contactEntity.getConName(), contactEntity.getConPhotoUrl()));
        myMessage.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
        mView.sendMessageSuccess(myMessage);
    }

    @Override
    public void exit() {

    }
}
