package com.winsion.component.user.user;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.CommonBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.view.CircleImageView;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.user.R;
import com.winsion.component.user.biz.LogoutBiz;
import com.winsion.component.user.login.activity.LoginActivity;

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
        return R.layout.user_activity_user;
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
        ImageLoader.loadUrl(ivHead, CacheDataSource.getUserHeadAddress(), R.drawable.basic_ic_head_single, R.drawable.basic_ic_head_single);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        addOnClickListeners(R.id.rl_check_update, R.id.btn_logout);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_check_update) {
            CommonBiz.checkVersionUpdate(mContext, this, true);
        } else if (id == R.id.btn_logout) {
            showDialog();
            LogoutBiz.logout(mContext, this::hideDialog);
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
            // 跳转登录界面
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
