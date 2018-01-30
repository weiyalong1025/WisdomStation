package com.winsion.dispatch.login.constants;

/**
 * Created by 10295 on 2017/12/5 0005.
 */

public interface LoginErrorCode {
    // 用户名密码不能为空
    int CAN_NOT_EMPTY = 0;
    // 没有配置IP和PORT
    int NO_IP_AND_PORT = 1;
    // 登录失败
    int LOGIN_FAILED = 2;
    // 用户名密码错误
    int WRONG_USER_INFO = 3;
    // MQ连接失败
    int MQ_CONNECT_FAILED = 4;
}
