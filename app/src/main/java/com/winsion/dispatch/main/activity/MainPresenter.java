package com.winsion.dispatch.main.activity;

import android.content.Context;

import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter {
    private MainContract.View mView;
    private Context mContext;

    MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 检查更新
        CommonBiz.checkVersionUpdate(mContext, this, false);
    }

    @Override
    public int getCurrentSystemType() {
        return (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
