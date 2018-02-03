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
import com.winsion.dispatch.view.TipDialog;
import com.winsion.dispatch.view.TitleView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2017/12/19 0019
 * 用户信息界面
 */

public class UserActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TitleView tvTitle;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_role_name)
    TextView tvRoleName;

    private TipDialog mLoadingDialog;

    @Override
    protected int setContentView() {
        return R.layout.activity_user;
    }

    @Override
    protected void start() {
        tvUsername.setText(CacheDataSource.getRealName());
        ImageLoader.loadUrl(ivHead, CacheDataSource.getUserHeadAddress(), R.drawable.ic_head_single, R.drawable.ic_head_single);
        tvTitle.setOnBackClickListener(v -> finish());
    }

    @OnClick({R.id.rl_check_update, R.id.btn_logout})
    public void onViewClicked(View view) {
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
        if (mLoadingDialog == null) {
            mLoadingDialog = new TipDialog.Builder(mContext)
                    .setIconType(TipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getString(R.string.on_logout))
                    .create();
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
    }

    private void hideDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
    }
}
