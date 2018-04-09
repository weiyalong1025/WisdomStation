package com.winsion.component.contact.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.component.contact.R;

import java.io.File;

/**
 * Created by wyl on 2017/4/25.
 */

public class VoiceView extends RelativeLayout {
    private LinearLayout moduleLeft;
    private LinearLayout moduleRight;
    private LinearLayout llLengthLeft;
    private LinearLayout llLengthRight;
    private AnimationDrawable animLeft;
    private AnimationDrawable animRight;
    private TextView tvDurationLeft;
    private TextView tvDurationRight;

    private boolean mAnimState;
    private String mVoiceFilePath;
    private VoicePlayer mVoicePlayer;
    private int mWhichSide;

    // 最大时长
    private int maxDuration = 60;
    // 最小宽度
    private float MIN_WIDTH = pd2px(83);

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.contact_view_voice, this, true);
        initView();
        initListener();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoiceView);
        int side = typedArray.getInt(R.styleable.VoiceView_side, 0);
        setSide(side);
        Drawable drawableLeft = typedArray.getDrawable(R.styleable.VoiceView_leftBg);
        Drawable drawableRight = typedArray.getDrawable(R.styleable.VoiceView_rightBg);
        typedArray.recycle();
        if (drawableLeft != null) {
            llLengthLeft.setBackground(drawableLeft);
        }
        if (drawableRight != null) {
            llLengthRight.setBackground(drawableRight);
        }
    }

    private void initView() {
        moduleLeft = findViewById(R.id.left);
        moduleRight = findViewById(R.id.right);

        llLengthLeft = findViewById(R.id.ll_length_left);
        llLengthRight = findViewById(R.id.ll_length_right);

        ImageView ivVoiceLeft = findViewById(R.id.iv_voice_left);
        ImageView ivVoiceRight = findViewById(R.id.iv_voice_right);

        animLeft = (AnimationDrawable) ivVoiceLeft.getDrawable();
        animRight = (AnimationDrawable) ivVoiceRight.getDrawable();

        tvDurationLeft = findViewById(R.id.tv_duration_left);
        tvDurationRight = findViewById(R.id.tv_duration_right);

        mVoicePlayer = VoicePlayer.getInstance();
    }

    private void initListener() {
        OnClickListener onClickListener = (view) -> {
            if (TextUtils.isEmpty(mVoiceFilePath) || !(new File(mVoiceFilePath).exists())) {
                return;
            }
            if (!mAnimState) {
                startPlay();
            } else {
                stopPlay();
            }
            mAnimState = !mAnimState;
        };
        llLengthLeft.setOnClickListener(onClickListener);
        llLengthRight.setOnClickListener(onClickListener);
    }

    private void startPlay() {
        // 开始动画
        startAnim();
        // 开始播放音频
        if (!TextUtils.isEmpty(mVoiceFilePath)) {
            mVoicePlayer.playRecord(mVoiceFilePath, new VoicePlayer.OnEndListener() {
                @Override
                public void onEnd() {
                    mAnimState = !mAnimState;
                    // 停止动画
                    stopAnim();
                }

                @Override
                public boolean isPlaying() {
                    return mAnimState;
                }
            });
        }
    }

    private void stopPlay() {
        // 停止动画
        stopAnim();
        // 停止播放音频
        if (!TextUtils.isEmpty(mVoiceFilePath)) {
            mVoicePlayer.stopPlay();
        }
    }

    private void startAnim() {
        if (mWhichSide == LEFT) {
            animLeft.start();
        } else if (mWhichSide == RIGHT) {
            animRight.start();
        }
    }

    private void stopAnim() {
        if (mWhichSide == LEFT) {
            animLeft.stop();
            animLeft.selectDrawable(0);
        } else if (mWhichSide == RIGHT) {
            animRight.stop();
            animRight.selectDrawable(0);
        }
    }

    /**
     * 设置左边还是右边
     *
     * @param side
     */
    public void setSide(int side) {
        this.mWhichSide = side;
        if (mWhichSide == LEFT) {
            moduleLeft.setVisibility(VISIBLE);
            moduleRight.setVisibility(GONE);
        } else if (mWhichSide == RIGHT) {
            moduleRight.setVisibility(VISIBLE);
            moduleLeft.setVisibility(GONE);
        }
    }

    /**
     * 设置音频文件的路径
     *
     * @param path
     */
    public void setVoiceFileDir(final String path) {
        // 判断是否应该做动画
        if (TextUtils.equals(mVoicePlayer.getCurrentPath(), path)) {
            startAnim();
            mAnimState = true;
        } else {
            stopAnim();
            mAnimState = false;
        }
        File file = new File(path);
        if (file.exists()) {
            this.mVoiceFilePath = path;
            // 设置时长
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    int duration = mVoicePlayer.getDuration(path);
                    post(() -> {
                        String durationStr = duration + "s";
                        if (mWhichSide == LEFT) {
                            tvDurationLeft.setText(durationStr);
                        } else if (mWhichSide == RIGHT) {
                            tvDurationRight.setText(durationStr);
                        }
                        float per = duration * 1f / maxDuration;
                        if (per > 1) {
                            per = 1;
                        }
                        if (mWhichSide == LEFT) {
                            ViewGroup.LayoutParams layoutParams = llLengthLeft.getLayoutParams();
                            layoutParams.width = (int) (getAdjustableWidth() * per + MIN_WIDTH);
                            llLengthLeft.setLayoutParams(layoutParams);
                        } else if (mWhichSide == RIGHT) {
                            ViewGroup.LayoutParams layoutParams = llLengthRight.getLayoutParams();
                            layoutParams.width = (int) (getAdjustableWidth() * per + MIN_WIDTH);
                            llLengthRight.setLayoutParams(layoutParams);
                        }
                    });
                }
            }.start();
        }
    }

    /**
     * 获取可调节的宽度
     *
     * @return
     */
    private int getAdjustableWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            return (int) (wm.getDefaultDisplay().getWidth() * 0.6f - MIN_WIDTH);
        }
        return 0;
    }

    private float pd2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimState) {
            stopPlay();
        }
    }
}
