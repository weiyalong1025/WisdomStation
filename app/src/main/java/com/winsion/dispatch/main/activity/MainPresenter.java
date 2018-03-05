package com.winsion.dispatch.main.activity;

import android.content.Context;

import com.winsion.dispatch.R;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.mqtt.MQTTClient;
import com.winsion.dispatch.mqtt.constants.MQType;
import com.winsion.dispatch.mqtt.entity.MQMessage;
import com.winsion.dispatch.utils.ToastUtils;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter, MQTTClient.Observer {
    private MainContract.View mView;
    private Context mContext;

    MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 检查更新
        CommonBiz.checkVersionUpdate(mContext, this, false);
        // 监听MQ消息
        MQTTClient.addObserver(this);
    }

    @Override
    public void onMessageArrive(MQMessage msg) {
        int messageType = msg.getMessageType();
        String data = msg.getData();
        if (messageType == MQType.USER_LOGIN && data.equals(CacheDataSource.getUserId())) {
            // 用户在别的设备登录，强制下线
            ToastUtils.showToast(mContext, R.string.toast_user_login_on_other_device);
            CommonBiz.logout(mContext, null);
        }
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        MQTTClient.removeObserver(this);
    }
}
