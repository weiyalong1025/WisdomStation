package com.winsion.dispatch.main.activity;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;

/**
 * Created by wyl on 2017/12/8
 */
class MainContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        int getCurrentSystemType();
    }
}
