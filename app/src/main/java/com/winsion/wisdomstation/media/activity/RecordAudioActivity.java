package com.winsion.wisdomstation.media.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.view.TitleView;
import com.winsion.wisdomstation.view.WaveView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2016/8/11.
 * 录制音频
 * TODO 动态权限-录音
 */
public class RecordAudioActivity extends BaseActivity {
    @BindView(R.id.btn_record)
    ImageView btnRecord;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_recording)
    TextView tvRecording;
    @BindView(R.id.wave_view)
    WaveView waveView;
    @BindView(R.id.tv_title)
    TitleView tvTitle;

    public static final String FILE = "file";

    private File mFile;
    // 记录是否正在录音
    private boolean isRecording = false;
    private MediaRecorder recorder;

    @SuppressLint("SetTextI18n")
    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        // 更新时间
        if (msg.what != -1) {
            tvTime.setText(msg.what + "s");
            if (msg.what == 60) {
                stop();
            } else {
                mHandler.sendEmptyMessageDelayed(++msg.what, 1000);
            }
        } else {
            // 根据音量大小改变波形
            if (isRecording) {
                double ratio = (double) recorder.getMaxAmplitude() / 100;
                double db = 0;
                if (ratio > 1)
                    db = 20 * Math.log10(ratio);
                //只要有一个线程，不断调用这个方法，就可以使波形变化
                //主要，这个方法必须在ui线程中调用
                waveView.setVolume((int) (db));
                mHandler.sendEmptyMessageDelayed(-1, 100);
            }
        }
    }

    @Override
    protected int setContentView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_record_audio;
    }

    @Override
    protected void start() {
        tvTitle.setOnBackClickListener((View v) -> finish());
        // 录音文件保存路径
        mFile = (File) getIntent().getSerializableExtra(FILE);
        initRecorder();
    }

    private void initRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置封装格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setOutputFile(mFile.getAbsolutePath());
        // 设置编码格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    @OnClick({R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                if (!isRecording) {
                    btnRecord.setEnabled(false);
                    mHandler.postDelayed(() -> btnRecord.setEnabled(true), 1000);
                    // 录音
                    try {
                        recorder.prepare();
                        recorder.start();
                        isRecording = true;
                        btnRecord.setImageResource(R.drawable.btn_record_reverse);
                        tvRecording.setVisibility(View.VISIBLE);
                        waveView.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                        mHandler.sendEmptyMessageDelayed(-1, 100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    stop();
                    btnRecord.setImageResource(R.drawable.btn_record);
                }
                break;
        }
    }

    private void stop() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            mFile.delete();
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
