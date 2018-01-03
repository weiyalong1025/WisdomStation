package com.winsion.wisdomstation.login;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.HttpParams;
import com.winsion.wisdomstation.application.AppApplication;
import com.winsion.wisdomstation.common.biz.CommonBiz;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.SPKey;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.login.constants.LoginErrorCode;
import com.winsion.wisdomstation.login.entity.AuthDto;
import com.winsion.wisdomstation.login.entity.UserEntity;
import com.winsion.wisdomstation.login.listener.LoginListener;
import com.winsion.wisdomstation.mqtt.MQTTClient;
import com.winsion.wisdomstation.utils.LogUtils;

import java.util.List;

/**
 * Created by wyl on 2017/3/23.
 */

class LoginPresenter implements LoginContract.Presenter, MQTTClient.ConnectListener {
    private static final String TAG = "LoginPresenter";

    private LoginContract.View mView;
    private Context mContext;
    private DBDataSource mDbDataSource;
    private LoginListener mLoginListener;
    private AuthDto mAuthDto;
    private String mUsername;
    private String mPassword;

    LoginPresenter(LoginContract.View loginView) {
        this.mView = loginView;
        this.mContext = loginView.getContext();
        this.mDbDataSource = DBDataSource.getInstance();
    }

    @Override
    public void start() {
        // 取出本地保存的用户信息进行回显
        UserEntity lastLoginUser = mDbDataSource.getLastLoginUser();
        if (lastLoginUser != null) {
            mView.displayRecentlyLoginUserInfo(lastLoginUser);
        }
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return mDbDataSource.getUserByUsername(username);
    }

    @Override
    public void login(String username, String password, LoginListener loginListener) {
        this.mLoginListener = loginListener;
        this.mUsername = username;
        this.mPassword = password;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            loginListener.loginFailed(LoginErrorCode.CAN_NOT_EMPTY);
            return;
        }

        String ip = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        String port = (String) SPDataSource.get(mContext, SPKey.KEY_PORT, "");
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            loginListener.loginFailed(LoginErrorCode.NO_IP_AND_PORT);
            return;
        }

        loginListener.onLogin();

        // 获取当前用户连接的WIFI信息用来定位
        String bssid = CommonBiz.getBSSID(mContext);

        HttpParams httpParams = new HttpParams();
        httpParams.put("account", username);
        httpParams.put("pwd", password);
        httpParams.put("ssId", bssid);
        httpParams.put("device", 1);

        NetDataSource.post(getClass(), Urls.LOGIN, httpParams, new ResponseListener<AuthDto>() {
            @Override
            public AuthDto convert(String jsonStr) {
                return JSON.parseObject(jsonStr, AuthDto.class);
            }

            @Override
            public void onSuccess(AuthDto authDto) {
                mAuthDto = authDto;
                // 开启MQ
                new MQTTClient.Connector(AppApplication.getContext())
                        .listener(LoginPresenter.this)
                        .connect(ip);
                LogUtils.header(TAG, "用户登录信息");
                LogUtils.i(TAG, "用户名:" + mUsername + ",密码:" + mPassword);
                LogUtils.i(TAG, "IP地址:" + ip + ",端口号:" + port);
                LogUtils.i(TAG, "BSSID地址:" + bssid);
                LogUtils.footer(TAG, "用户登录信息");
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                loginListener.loginFailed(LoginErrorCode.LOGIN_FAILED);
            }
        });
    }

    @Override
    public List<UserEntity> getAllSavedUser() {
        return mDbDataSource.getAllSavedUser();
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        mDbDataSource.deleteUser(userEntity);
    }

    @Override
    public void connectSuccess() {
        saveDataToLocal();
        mLoginListener.loginSuccess();
    }

    @Override
    public void connectFailed() {
        mLoginListener.loginFailed(LoginErrorCode.MQ_CONNECT_FAILED);
        MQTTClient.destroy();
    }

    /**
     * 存储数据到本地
     */
    private void saveDataToLocal() {
        AuthDto.UserDto user = mAuthDto.getUser();
        SPDataSource.put(mContext, SPKey.KEY_USER_ID, mAuthDto.getUserId());
        SPDataSource.put(mContext, SPKey.KEY_HTTP_KEY, mAuthDto.getHttpKey());
        SPDataSource.put(mContext, SPKey.KEY_TOKEN, mAuthDto.getToken());
        SPDataSource.put(mContext, SPKey.KEY_MQ_KEY, mAuthDto.getMqKey());
        SPDataSource.put(mContext, SPKey.KEY_TEAM_ID, mAuthDto.getTeamId());
        SPDataSource.put(mContext, SPKey.KEY_USERNAME, mUsername);
        SPDataSource.put(mContext, SPKey.KEY_PASSWORD, mPassword);
        SPDataSource.put(mContext, SPKey.KEY_SIP_USERNAME, user.getSiptelladdress());
        SPDataSource.put(mContext, SPKey.KEY_SIP_PASSWORD, user.getSippassword());
        SPDataSource.put(mContext, SPKey.KEY_REAL_NAME, user.getUsername());
        SPDataSource.put(mContext, SPKey.KEY_USER_HEAD_ADDRESS, user.getPhoto());

        UserEntity userBean = new UserEntity();
        userBean.setUserId(mAuthDto.getUserId());
        userBean.setUsername(mUsername);
        userBean.setPassword(mPassword);
        userBean.setHeaderUrl(user.getPhoto());
        userBean.setPocUsername(user.getSiptelladdress());
        userBean.setPocPassword(user.getSippassword());
        userBean.setLastLoginTime(System.currentTimeMillis());
        userBean.setLoginIp((String) SPDataSource.get(mContext, SPKey.KEY_IP, ""));
        userBean.setLoginPort((String) SPDataSource.get(mContext, SPKey.KEY_PORT, ""));
        userBean.setIsAutoLogin(true);
        mDbDataSource.saveUserInfo(userBean);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(getClass());
    }
}
