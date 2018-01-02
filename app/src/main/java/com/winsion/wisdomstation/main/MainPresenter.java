package com.winsion.wisdomstation.main;

import android.content.Context;

import com.winsion.wisdomstation.common.constants.SystemType;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.SPKey;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter {
    private MainContract.View mView;
    private Context mContext;

    public MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public int getCurrentSystemType() {
        return (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
    }

    @Override
    public void exit() {

    }
}
