package com.winsion.wisdomstation.splash;

import android.os.Message;

import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.login.activity.LoginActivity;
import com.winsion.wisdomstation.utils.LogUtils;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void start() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        logScreenInfo();
        startActivity(LoginActivity.class, true);
    }

    private void logScreenInfo() {
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int densityDpi = getResources().getDisplayMetrics().densityDpi;
        LogUtils.i(TAG, "heightPixels: " + heightPixels + ",widthPixels：" + widthPixels + ",density：" + density + ",densityDpi：" + densityDpi);
    }
}
