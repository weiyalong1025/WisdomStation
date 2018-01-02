package com.winsion.wisdomstation.login.listener;

/**
 * Created by 10295 on 2017/12/5 0005.
 */

public interface LoginListener {
    // 登录中回调
    void onLogin();
    // 登录成功回调
    void loginSuccess();
    // 登录失败回调
    void loginFailed(int loginErrorCode);
}
