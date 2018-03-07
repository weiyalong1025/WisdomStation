package com.winsion.component.user.biz;

import android.app.NotificationManager;
import android.content.Context;

import com.lzy.okgo.model.HttpParams;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.listener.SuccessListener;
import com.winsion.component.user.mqtt.MQTTClient;

/**
 * Created by 10295 on 2018/3/7.
 * 注销Biz
 */

public class LogoutBiz {
    /**
     * 用户注销
     */
    public static void logout(Context context, SuccessListener listener) {
        MQTTClient.destroy();
        // 取消自动登录
        DBDataSource.getInstance(context).cancelAutoLogin();
        // 清空所有通知
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.cancelAll();
        // 用户请求退出接口
        HttpParams httpParams = new HttpParams();
        httpParams.put("userId", CacheDataSource.getUserId());
        NetDataSource.post(null, Urls.USER_LOGOUT, httpParams, null);
        // 清除缓存信息
        CacheDataSource.clearCache();
        if (listener != null) {
            listener.onSuccess();
        }
    }
}
