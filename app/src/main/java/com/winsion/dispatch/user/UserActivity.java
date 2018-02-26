package com.winsion.dispatch.user;

import android.view.View;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.utils.ImageLoader;
import com.winsion.dispatch.view.CircleImageView;
import com.winsion.dispatch.view.CustomDialog;
import com.winsion.dispatch.view.TitleView;

/**
 * Created by 10295 on 2017/12/19 0019
 * 用户信息界面
 */

public class UserActivity extends BaseActivity {
    private TitleView tvTitle;
    private CircleImageView ivHead;
    private TextView tvUsername;
    private TextView tvRoleName;

    private CustomDialog customDialog;

    @Override
    protected int setContentView() {
        return R.layout.activity_user;
    }

    @Override
    protected void start() {
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        ivHead = findViewById(R.id.iv_head);
        tvUsername = findViewById(R.id.tv_username);
        tvRoleName = findViewById(R.id.tv_role_name);
    }

    private void initData() {
        tvUsername.setText(CacheDataSource.getRealName());
        ImageLoader.loadUrl(ivHead, CacheDataSource.getUserHeadAddress(), R.drawable.ic_head_single, R.drawable.ic_head_single);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        addOnClickListeners(R.id.rl_check_update, R.id.btn_logout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_check_update:
                CommonBiz.checkVersionUpdate(mContext, this, true);
                break;
            case R.id.btn_logout:
                showDialog();
                CommonBiz.logout(mContext, this::hideDialog);
                break;
        }
    }

    private void showDialog() {
        // 注销中，显示dialog
        if (customDialog == null) {
            customDialog = new CustomDialog.StateBuilder(mContext)
                    .setStateText(getString(R.string.dialog_on_logout))
                    .setIrrevocable()
                    .create();
        }
        customDialog.show();
    }

    private void hideDialog() {
        if (customDialog != null) {
            customDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
