package com.winsion.dispatch.splash;

import android.os.Message;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.utils.LogUtils;
import com.winsion.component.user.login.activity.LoginActivity;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void start() {
        logScreenInfo();
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);

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
