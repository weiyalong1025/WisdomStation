package com.winsion.wisdomstation.main.activity;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;

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
