package com.winsion.component.user.activity.config;

import android.content.Context;
import android.text.TextUtils;

import com.winsion.component.basic.data.SPDataSource;
import com.winsion.component.basic.constants.SPKey;
import com.winsion.component.user.constants.SaveErrorCode;
import com.winsion.component.user.constants.UrlRegular;

/**
 * Created by wyl on 2017/3/28.
 * 登录配置Presenter
 */

class LoginConfigPresenter implements LoginConfigContract.Presenter {

    private final LoginConfigContract.View mView;
    private final Context mContext;

    LoginConfigPresenter(LoginConfigContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        String ip = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        String port = (String) SPDataSource.get(mContext, SPKey.KEY_PORT, "");
        mView.redisplayHost(ip, port);
    }

    @Override
    public void saveHost(String ip, String port, SaveListener saveListener) {
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            saveListener.saveFailed(SaveErrorCode.CAN_NOT_BE_NULL);
        } else if (!ip.matches(UrlRegular.IP_REGULAR) || !port.matches(UrlRegular.PORT_REGULAR)) {
            saveListener.saveFailed(SaveErrorCode.FORMAT_ERROR);
        } else {
            SPDataSource.put(mContext, SPKey.KEY_IP, ip);
            SPDataSource.put(mContext, SPKey.KEY_PORT, port);
            saveListener.saveSuccess();
        }
    }

    @Override
    public void exit() {

    }

    interface SaveListener {
        void saveSuccess();

        void saveFailed(int saveErrorCode);
    }
}
