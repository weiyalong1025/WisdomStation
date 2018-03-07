package com.winsion.dispatch.application;

import android.app.Application;
import android.content.Context;

import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.utils.CrashUtils;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.LogUtils;
import com.winsion.dispatch.BuildConfig;
import com.winsion.dispatch.R;

import java.io.IOException;

/**
 * Created by 10295 on 2017/12/5 0005
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 全局捕获异常并保存异常信息
        CrashUtils.getInstance().init(this);

        // 初始化网络库
        NetDataSource.init(this, BuildConfig.DEBUG, getString(R.string.app_name));

        // 初始化LOG
        initLog();
    }

    private void initLog() {
        String logDir;
        try {
            logDir = DirAndFileUtils.getLogDir().toString();
        } catch (IOException e) {
            logDir = getCacheDir().toString();
        }
        String logTag = getString(R.string.app_name);
        LogUtils.init(BuildConfig.DEBUG, true, logDir, LogUtils.FILTER_V, logTag);
    }
}
