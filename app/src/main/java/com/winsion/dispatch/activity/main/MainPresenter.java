package com.winsion.dispatch.activity.main;

import android.content.Context;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.MQType;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.MQMessage;
import com.winsion.component.basic.mqtt.MQTTClient;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.dispatch.R;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter, MQTTClient.Observer {
    private final MainContract.View mView;
    private final Context mContext;

    MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 检查更新
        BasicBiz.checkVersionUpdate(mContext, this, false);
        // 监听MQ消息
        MQTTClient.addObserver(this);
    }

    @Override
    public void onMessageArrive(MQMessage msg) {
        switch (msg.getMessageType()) {
            case MQType.USER_LOGIN:
                String data = msg.getData();
                if (data.equals(CacheDataSource.getUserId())) {
                    // 用户在别的设备登录，强制下线
                    ToastUtils.showToast(mContext, R.string.toast_user_login_on_other_device);
                    CC.obtainBuilder("ComponentUser")
                            .setActionName("logout")
                            .build()
                            .call();
                }
                break;
        }
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
        MQTTClient.removeObserver(this);
    }
}
