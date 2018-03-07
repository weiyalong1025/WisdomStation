package com.winsion.component.user.config.activity;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.user.config.listener.SaveListener;

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
