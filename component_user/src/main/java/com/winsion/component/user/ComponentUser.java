package com.winsion.component.user;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponent;
import com.lzy.okgo.model.HttpParams;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.DBDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.listener.StateListener;
import com.winsion.component.basic.mqtt.MQTTClient;
import com.winsion.component.user.activity.login.LoginActivity;
import com.winsion.component.user.activity.user.UserActivity;

/**
 * Created by 10295 on 2018/3/7.
 * User组件
 */

public class ComponentUser implements IComponent {
    @Override
    public String getName() {
        return "ComponentUser";
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        Context context = cc.getContext();
        Intent intent;
        switch (actionName) {
            case "toLoginActivity":
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                return true;
            case "toLoginActivityClearTask":
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("callId", cc.getCallId());
                context.startActivity(intent);
                return true;
            case "toUserActivity":
                intent = new Intent(context, UserActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
            case "logout":
                logout(cc.getContext(), cc.getCallId(), null);
                break;
        }
        return false;
    }

    /**
     * 用户注销
     */
    public static void logout(Context context, String callId, StateListener successListener) {
        MQTTClient.destroy();
        // 取消自动登录
        DBDataSource.getInstance(context.getApplicationContext()).cancelAutoLogin();
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
        if (successListener != null) {
            successListener.onSuccess();
        }
        // 跳转登录界面
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("callId", callId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
