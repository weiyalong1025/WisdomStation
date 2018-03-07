package com.winsion.component.basic.data;

/**
 * Created by 10295 on 2017/12/5 0005.
 * 缓存数据，静态变量保存
 * 应保证缓存的数据只有在登录成功被存储，注销需主动清除缓存
 */

public class CacheDataSource {
    private static boolean loginState;  // 登录状态
    private static String ip;
    private static String port;
    private static String baseUrl;
    private static String httpKey;
    private static String token;
    private static String mqKey;
    private static String ssid; // 配置的SSID，人员定位用到，符合该SSID的再上报BSSID
    private static String userId;   // 用户ID
    private static String teamId;   // 班组ID
    private static String username; // 用户登录名
    private static String password; // 用户登录密码
    private static String sipUsername;  // SIP登录用户名
    private static String sipPassword;  // SIP登录密码
    private static String realName; // 用户昵称
    private static String userHeadAddress;  // 用户头像地址
    private static boolean testMode;    // 测试模式开关

    public static boolean getLoginState() {
        return loginState;
    }

    public static void setLoginState(boolean loginState) {
        CacheDataSource.loginState = loginState;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        CacheDataSource.ip = ip;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        CacheDataSource.port = port;
    }

    public static void setBaseUrl(String baseUrl) {
        CacheDataSource.baseUrl = baseUrl;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setHttpKey(String httpKey) {
        CacheDataSource.httpKey = httpKey;
    }

    public static String getHttpKey() {
        return httpKey;
    }

    public static void setToken(String token) {
        CacheDataSource.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static String getMqKey() {
        return mqKey;
    }

    public static void setMqKey(String mqKey) {
        CacheDataSource.mqKey = mqKey;
    }

    public static void setSsid(String ssid) {
        CacheDataSource.ssid = ssid;
    }

    public static String getSsid() {
        return ssid;
    }

    public static void setUserId(String userId) {
        CacheDataSource.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setTeamId(String teamId) {
        CacheDataSource.teamId = teamId;
    }

    public static String getTeamId() {
        return teamId;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        CacheDataSource.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        CacheDataSource.password = password;
    }

    public static String getSipUsername() {
        return sipUsername;
    }

    public static void setSipUsername(String sipUsername) {
        CacheDataSource.sipUsername = sipUsername;
    }

    public static String getSipPassword() {
        return sipPassword;
    }

    public static void setSipPassword(String sipPassword) {
        CacheDataSource.sipPassword = sipPassword;
    }

    public static void setRealName(String realName) {
        CacheDataSource.realName = realName;
    }

    public static String getRealName() {
        return realName;
    }

    public static void setUserHeadAddress(String userHeadAddress) {
        CacheDataSource.userHeadAddress = userHeadAddress;
    }

    public static String getUserHeadAddress() {
        return userHeadAddress;
    }

    public static boolean getTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        CacheDataSource.testMode = testMode;
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        loginState = false;
        ip = null;
        port = null;
        baseUrl = null;
        httpKey = null;
        token = null;
        mqKey = null;
        ssid = null;
        userId = null;
        teamId = null;
        username = null;
        password = null;
        sipUsername = null;
        sipPassword = null;
        realName = null;
        userHeadAddress = null;
        testMode = false;
    }
}
