package com.winsion.dispatch.login.activity;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.HttpParams;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.data.DBDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.login.constants.LoginErrorCode;
import com.winsion.dispatch.login.entity.AuthEntity;
import com.winsion.dispatch.login.entity.UserEntity;
import com.winsion.dispatch.login.listener.LoginListener;
import com.winsion.dispatch.mqtt.MQTTClient;
import com.winsion.dispatch.utils.JsonUtils;
import com.winsion.dispatch.utils.LogUtils;

import java.util.List;

/**
 * Created by wyl on 2017/3/23
 */

class LoginPresenter implements LoginContract.Presenter, MQTTClient.ConnectListener {
    private static final String TAG = "LoginPresenter";

    private LoginContract.View mView;
    private Context mContext;
    private DBDataSource mDbDataSource;
    private LoginListener mLoginListener;
    private AuthEntity mAuthEntity;
    private String mUsername;
    private String mPassword;

    LoginPresenter(LoginContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
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

        // 测试模式
        if (mUsername.equals("admin") && mPassword.equals("admin")) {
            mUsername = "1010";
            mPassword = "123456";
            AppApplication.TEST_MODE = true;
        }

        if (AppApplication.TEST_MODE) {
            mAuthEntity = JsonUtils.getTestEntity(mContext, AuthEntity.class);
            connectSuccess();
            return;
        }

        // 获取当前用户连接的WIFI信息用来定位
        String bssid = CommonBiz.getBSSID(mContext);

        HttpParams httpParams = new HttpParams();
        httpParams.put("account", username);
        httpParams.put("pwd", password);
        httpParams.put("ssId", bssid);
        httpParams.put("device", 1);

        NetDataSource.post(this, Urls.LOGIN, httpParams, new ResponseListener<AuthEntity>() {
            @Override
            public AuthEntity convert(String jsonStr) {
                return JSON.parseObject(jsonStr, AuthEntity.class);
            }

            @Override
            public void onSuccess(AuthEntity authEntity) {
                mAuthEntity = authEntity;
                // 开启MQ
                new MQTTClient.Connector(mContext)
                        .listener(LoginPresenter.this)
                        .host(ip).connect();
                LogUtils.i(TAG, "用户名:" + mUsername + ",密码:" + mPassword);
                LogUtils.i(TAG, "IP地址:" + ip + ",端口号:" + port);
                LogUtils.i(TAG, "BSSID地址:" + bssid);
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
        AuthEntity.UserDto user = mAuthEntity.getUser();
        SPDataSource.put(mContext, SPKey.KEY_USER_ID, mAuthEntity.getUserId());
        SPDataSource.put(mContext, SPKey.KEY_HTTP_KEY, mAuthEntity.getHttpKey());
        SPDataSource.put(mContext, SPKey.KEY_TOKEN, mAuthEntity.getToken());
        SPDataSource.put(mContext, SPKey.KEY_MQ_KEY, mAuthEntity.getMqKey());
        SPDataSource.put(mContext, SPKey.KEY_TEAM_ID, mAuthEntity.getTeamId());
        SPDataSource.put(mContext, SPKey.KEY_USERNAME, mUsername);
        SPDataSource.put(mContext, SPKey.KEY_PASSWORD, mPassword);
        SPDataSource.put(mContext, SPKey.KEY_SIP_USERNAME, user.getSiptelladdress());
        SPDataSource.put(mContext, SPKey.KEY_SIP_PASSWORD, user.getSippassword());
        SPDataSource.put(mContext, SPKey.KEY_REAL_NAME, user.getUsername());
        SPDataSource.put(mContext, SPKey.KEY_USER_HEAD_ADDRESS, user.getPhoto());

        UserEntity userBean = new UserEntity();
        userBean.setUserId(mAuthEntity.getUserId());
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
        NetDataSource.unSubscribe(this);
    }
}
