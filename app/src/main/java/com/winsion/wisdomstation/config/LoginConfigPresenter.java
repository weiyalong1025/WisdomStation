package com.winsion.wisdomstation.config;

import android.content.Context;
import android.text.TextUtils;

import com.winsion.wisdomstation.config.constants.SaveErrorCode;
import com.winsion.wisdomstation.config.constants.UrlRegular;
import com.winsion.wisdomstation.config.listener.SaveListener;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.SPKey;

/**
 * Created by wyl on 2017/3/28.
 */

class LoginConfigPresenter implements LoginConfigContract.Presenter {

    private LoginConfigContract.View mLoginConfigView;
    private Context mContext;

    LoginConfigPresenter(LoginConfigContract.View loginConfigView) {
        this.mLoginConfigView = loginConfigView;
        this.mContext = loginConfigView.getContext();
    }

    @Override
    public void start() {
        String ip = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        String port = (String) SPDataSource.get(mContext, SPKey.KEY_PORT, "");
        mLoginConfigView.redisplayHost(ip, port);
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
            CacheDataSource.clearBaseUrl();
            saveListener.saveSuccess();
        }
    }

    @Override
    public void exit() {

    }
}
