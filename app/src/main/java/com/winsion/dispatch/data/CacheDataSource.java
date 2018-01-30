package com.winsion.dispatch.data;

import android.text.TextUtils;

import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.utils.ConvertUtils;

/**
 * Created by 10295 on 2017/12/5 0005.
 * 缓存数据，静态变量保存，静态变量被回收会去本地取
 * 应保证缓存的数据只有在登录成功才被存储，否则数据可能不匹配
 */

public class CacheDataSource {
    private static String baseUrl;
    private static String httpKey;
    private static String token;
    private static String ssid;
    private static String teamId;
    private static String userId;
    private static String realName;
    private static String userHeadAddress;

    public static String getBaseUrl() {
        if (TextUtils.isEmpty(baseUrl)) {
            String ip = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_IP, "");
            String port = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_PORT, "");
            baseUrl = ConvertUtils.formatURL(ip, port);
        }
        return baseUrl;
    }

    public static void clearBaseUrl() {
        baseUrl = null;
    }

    public static String getHttpKey() {
        if (TextUtils.isEmpty(httpKey)) {
            httpKey = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_HTTP_KEY, "");
        }
        return httpKey;
    }

    public static String getToken() {
        if (TextUtils.isEmpty(token)) {
            token = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_TOKEN, "");
        }
        return token;
    }

    public static String getSsid() {
        if (TextUtils.isEmpty(ssid)) {
            ssid = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_SSID, "");
        }
        return ssid;
    }

    public static String getTeamId() {
        if (TextUtils.isEmpty(teamId)) {
            teamId = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_TEAM_ID, "");
        }
        return teamId;
    }

    public static String getUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_USER_ID, "");
        }
        return userId;
    }

    public static String getRealName() {
        if (TextUtils.isEmpty(realName)) {
            realName = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_REAL_NAME, "");
        }
        return realName;
    }

    public static String getUserHeadAddress() {
        if (TextUtils.isEmpty(userHeadAddress)) {
            userHeadAddress = (String) SPDataSource.get(AppApplication.getContext(), SPKey.KEY_USER_HEAD_ADDRESS, "");
        }
        return userHeadAddress;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        baseUrl = null;
        httpKey = null;
        token = null;
        ssid = null;
        teamId = null;
        userId = null;
        realName = null;
        userHeadAddress = null;
    }
}
