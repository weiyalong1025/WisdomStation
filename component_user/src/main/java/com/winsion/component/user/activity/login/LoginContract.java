package com.winsion.component.user.activity.login;

import android.support.annotation.NonNull;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.entity.UserEntity;

import java.util.List;

/**
 * Created by wyl on 2017/3/22.
 * 登录界面Contract
 */

class LoginContract {
    interface View extends BaseView {
        void displayRecentlyLoginUserInfo(@NonNull UserEntity userEntity);
    }

    interface Presenter extends BasePresenter {
        UserEntity getUserByUsername(String username);

        void login(String username, String password, LoginPresenter.LoginListener loginListener);

        List<UserEntity> getAllSavedUser();

        void deleteUser(UserEntity userEntity);
    }
}
