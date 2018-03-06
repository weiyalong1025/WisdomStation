package com.winsion.dispatch.application;

import android.app.Application;
import android.content.Context;

import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.utils.CrashUtils;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.LogUtils;
import com.winsion.dispatch.BuildConfig;
import com.winsion.dispatch.MyObjectBox;
import com.winsion.dispatch.R;

import java.io.IOException;

import io.objectbox.BoxStore;

/**
 * Created by 10295 on 2017/12/5 0005
 */

public class AppApplication extends Application {
    private static Context mApplicationContext;
    private static BoxStore mBoxStore;
    // 测试模式开关
    public static boolean TEST_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();

        // 全局捕获异常并保存异常信息
        CrashUtils.getInstance().init(this);

        // 初始化数据库
        initDB();

        // 初始化网络库
        NetDataSource.init(this, BuildConfig.DEBUG, getString(R.string.app_name));

        // 初始化LOG
        initLog();
    }

    private void initDB() {
        mBoxStore = MyObjectBox.builder().androidContext(this).build();
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

    public static Context getContext() {
        return mApplicationContext;
    }

    public static BoxStore getBoxStore() {
        return mBoxStore;
    }
}
