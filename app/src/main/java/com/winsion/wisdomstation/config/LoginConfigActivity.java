package com.winsion.wisdomstation.config;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.config.constants.SaveErrorCode;
import com.winsion.wisdomstation.config.listener.SaveListener;
import com.winsion.wisdomstation.view.TitleView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2017/12/6 0006.
 */

public class LoginConfigActivity extends BaseActivity implements LoginConfigContract.View, SaveListener {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_port)
    EditText etPort;

    private LoginConfigContract.Presenter mPresenter;

    @Override
    public int setContentView() {
        return R.layout.activity_login_config;
    }

    @Override
    public void start() {
        tvTitle.setOnBackClickListener((view) -> finish());
        initPresenter();
    }

    private void initPresenter() {
        mPresenter = new LoginConfigPresenter(this);
        mPresenter.start();
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

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
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
                showToast(R.string.address_and_port_can_not_be_empty);
                break;
            case SaveErrorCode.FORMAT_ERROR:
                showToast(R.string.format_error);
                break;
        }
    }

}
