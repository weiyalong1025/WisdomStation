package com.winsion.wisdomstation.application;

import android.app.Application;
import android.content.Context;

import com.winsion.wisdomstation.BuildConfig;
import com.winsion.wisdomstation.MyObjectBox;
import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.utils.CrashUtils;
import com.winsion.wisdomstation.utils.LogUtils;

import io.objectbox.BoxStore;

/**
 * Created by 10295 on 2017/12/5 0005.
 */

public class AppApplication extends Application {
    private static Context mApplicationContext;
    private static BoxStore mBoxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
        // 全局捕获异常并保存异常信息
        CrashUtils.getInstance().init(this);
        // 初始化数据库
        mBoxStore = MyObjectBox.builder().androidContext(this).build();
        // 初始化网络库
        NetDataSource.init(this);
        // 初始化LOG
        LogUtils.init(this, BuildConfig.DEBUG, true, LogUtils.FILTER_V, getString(R.string.log_tag));
    }

    public static Context getContext() {
        return mApplicationContext;
    }

    public static BoxStore getBoxStore() {
        return mBoxStore;
    }
}
