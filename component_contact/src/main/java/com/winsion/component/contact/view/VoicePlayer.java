package com.winsion.component.contact.view;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by wyl on 2017/4/26.
 */

public class VoicePlayer {
    private static VoicePlayer mInstance;
    private boolean isPlaying;
    // 记录当前正在播放的音频路径
    private String currentPath;
    private MediaPlayer mediaPlayer;
    private OnEndListener mListener;

    private VoicePlayer() {
    }

    public static VoicePlayer getInstance() {
        if (mInstance == null) {
            synchronized (VoicePlayer.class) {
                if (mInstance == null) {
                    mInstance = new VoicePlayer();
                }
            }
        }
        return mInstance;
    }

    public int getDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = ((int) Math.ceil(Integer.valueOf(durationStr) / 1000f));
        mmr.release();
        return duration;
    }

    public void playRecord(String path, final OnEndListener listener) {
        if (mListener != null) {
            if (mListener.isPlaying()) {
                mListener.onEnd();
            }
        }
        this.mListener = listener;
        if (TextUtils.equals(path, currentPath)) {
            if (!isPlaying) {
                startPlay(path, listener);
            } else {
                stopPlay();
            }
        } else {
            currentPath = path;
            startPlay(path, listener);
        }
    }

    private void startPlay(String path, final OnEndListener listener) {
        isPlaying = true;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                mediaPlayer.release();
                mediaPlayer = null;
                currentPath = null;
                if (listener != null) {
                    listener.onEnd();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null && isPlaying) {
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            currentPath = null;
        }
    }

    /**
     * 返回当前正在播放的音乐的路径，如果没有播放返回null
     *
     * @return
     */
    public String getCurrentPath() {
        return currentPath;
    }

    public interface OnEndListener {
        void onEnd();

        boolean isPlaying();
    }
}
