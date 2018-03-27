package com.winsion.component.user.activity.user;

import android.view.View;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.listener.StateListener;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.view.CircleImageView;
import com.winsion.component.basic.view.CustomDialog;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.user.ComponentUser;
import com.winsion.component.user.R;

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
    private String callId;

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
        callId = getIntent().getStringExtra("callId");
        if (isEmpty(callId)) {
            tvTitle.showBackButton(false);
        }
        tvUsername.setText(CacheDataSource.getRealName());
        tvRoleName.setText(String.format("(%s)", CacheDataSource.getTeamName()));
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
            BasicBiz.checkVersionUpdate(mContext, this, true);
        } else if (id == R.id.btn_logout) {
            showDialog();
            ComponentUser.logout(mContext, callId, new StateListener() {
                @Override
                public void onSuccess() {
                    if (customDialog != null) {
                        customDialog.dismiss();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
    }
}
