package com.winsion.wisdomstation.login.activity;

import android.support.annotation.NonNull;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.login.entity.UserEntity;
import com.winsion.wisdomstation.login.listener.LoginListener;

import java.util.List;

/**
 * Created by wyl on 2017/3/22.
 */

class LoginContract {
    interface View extends BaseView {
        void displayRecentlyLoginUserInfo(@NonNull UserEntity userEntity);
    }

    interface Presenter extends BasePresenter {
        UserEntity getUserByUsername(String username);

        void login(String username, String password, LoginListener loginListener);

        List<UserEntity> getAllSavedUser();

        void deleteUser(UserEntity userEntity);
    }
}
