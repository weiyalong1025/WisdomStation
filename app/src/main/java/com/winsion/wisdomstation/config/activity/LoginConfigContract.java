package com.winsion.wisdomstation.config.activity;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.config.listener.SaveListener;

/**
 * Created by wyl on 2017/3/28.
 */

class LoginConfigContract {
    interface View extends BaseView {
        void redisplayHost(String ip, String port);
    }

    interface Presenter extends BasePresenter {
        void saveHost(String ip, String port, SaveListener saveListener);
    }
}
