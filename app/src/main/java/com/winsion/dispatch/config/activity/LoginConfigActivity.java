package com.winsion.dispatch.config.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.winsion.dispatch.R;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.dispatch.config.constants.SaveErrorCode;
import com.winsion.dispatch.config.listener.SaveListener;
import com.winsion.dispatch.view.TitleView;

/**
 * Created by 10295 on 2017/12/6 0006.
 * 登录配置界面
 */

public class LoginConfigActivity extends BaseActivity implements LoginConfigContract.View, SaveListener {
    private TitleView tvTitle;
    private EditText etAddress;
    private EditText etPort;

    private LoginConfigContract.Presenter mPresenter;

    @Override
    public int setContentView() {
        return R.layout.activity_login_config;
    }

    @Override
    public void start() {
        initView();
        initPresenter();
        initListener();
    }

    private void initPresenter() {
        mPresenter = new LoginConfigPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        etAddress = findViewById(R.id.et_address);
        etPort = findViewById(R.id.et_port);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener((view) -> finish());
        addOnClickListeners(R.id.btn_confirm);
    }

    @Override
    public void redisplayHost(String ip, String port) {
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            return;
        }
        etAddress.setText(ip);
        etPort.setText(port);
        // 设置光标在最后
        etPort.requestFocus();
        etPort.setSelection(port.length());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onClick(View view) {
        mPresenter.saveHost(getText(etAddress), getText(etPort), this);
    }

    @Override
    public void saveSuccess() {
        finish();
    }

    @Override
    public void saveFailed(int saveErrorCode) {
        switch (saveErrorCode) {
            case SaveErrorCode.CAN_NOT_BE_NULL:
                showToast(R.string.toast_complete_ip_port);
                break;
            case SaveErrorCode.FORMAT_ERROR:
                showToast(R.string.toast_format_error);
                break;
        }
    }
}
