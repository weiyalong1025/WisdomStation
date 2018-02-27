package com.winsion.dispatch.config.activity;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.config.listener.SaveListener;

/**
 * Created by wyl on 2017/3/28.
 * 登录配置Contract
 */

class LoginConfigContract {
    interface View extends BaseView {
        void redisplayHost(String ip, String port);
    }

    interface Presenter extends BasePresenter {
        void saveHost(String ip, String port, SaveListener saveListener);
    }
}
