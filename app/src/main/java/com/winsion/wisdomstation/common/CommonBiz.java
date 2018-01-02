package com.winsion.wisdomstation.common;

import android.content.Context;
import android.content.Intent;

import com.lzy.okgo.model.HttpParams;
import com.winsion.wisdomstation.common.listener.SuccessListener;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.login.LoginActivity;
import com.winsion.wisdomstation.mqtt.MQTTClient;
import com.winsion.wisdomstation.utils.CommonUtils;

/**
 * Created by 10295 on 2017/12/21 0021.
 */

public class CommonBiz {
    /**
     * 用户注销
     */
    public static void logout(Context context, SuccessListener listener) {
        MQTTClient.destroy();
        // 取消自动登录
        DBDataSource.getInstance().cancelAutoLogin();
        // 清空所有通知
        CommonUtils.cancelAllNotification(context);
        // 请求用户退出接口
        HttpParams httpParams = new HttpParams();
        httpParams.put("userId", CacheDataSource.getUserId());
        NetDataSource.post(null, Urls.USER_LOGOUT, httpParams, null);
        // 清除缓存信息
        CacheDataSource.clearCache();
        listener.onSuccess();
        // 跳转登录界面
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
