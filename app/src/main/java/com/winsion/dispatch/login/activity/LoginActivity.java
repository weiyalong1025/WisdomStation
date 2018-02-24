package com.winsion.dispatch.login.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.winsion.dispatch.R;
import com.winsion.dispatch.SwitchSysActivity;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.config.activity.LoginConfigActivity;
import com.winsion.dispatch.login.adapter.UserListAdapter;
import com.winsion.dispatch.login.constants.LoginErrorCode;
import com.winsion.dispatch.login.entity.UserEntity;
import com.winsion.dispatch.login.listener.LoginListener;
import com.winsion.dispatch.utils.ImageLoader;
import com.winsion.dispatch.utils.ViewUtils;
import com.winsion.dispatch.view.CircleImageView;
import com.winsion.dispatch.view.CustomDialog;
import com.winsion.dispatch.view.WrapContentListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wyl on 2017/12/5
 */
public class LoginActivity extends BaseActivity implements LoginContract.View, TextWatcher, LoginListener, AdapterView.OnItemClickListener {
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.civ_head)
    CircleImageView civHead;
    @BindView(R.id.rl_username)
    RelativeLayout rlUsername;
    @BindView(R.id.ll_username)
    LinearLayout llUsername;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.iv_visibility)
    ImageView ivVisibility;

    private List<UserEntity> mAllSavedUser = new ArrayList<>();

    private LoginContract.Presenter mPresenter;
    private CustomDialog customDialog;
    private PopupWindow mUserListPopup;
    private RotateAnimation mUpAnim;
    private RotateAnimation mDownAnim;
    private boolean isSoftShow;

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void start() {
        initPresenter();
        initListener();
        initData();
    }

    private void initPresenter() {
        mPresenter = new LoginPresenter(this);
        mPresenter.start();
    }

    /**
     * 初始化EditText监听器
     */
    private void initListener() {
        etUsername.addTextChangedListener(this);
    }

    /**
     * 获取所有保存过的用户信息
     */
    private void initData() {
        mAllSavedUser.addAll(mPresenter.getAllSavedUser());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 显示最近一次登录的用户数据
     */
    @Override
    public void displayRecentlyLoginUserInfo(@NonNull UserEntity userEntity) {
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();

        etUsername.setText(username);
        etUsername.setSelection(username.length());
        etPassword.setText(password);
        ImageLoader.loadUrl(civHead, userEntity.getHeaderUrl(), R.drawable.ic_head_single, R.drawable.ic_head_single);

        // 是否需要自动登录
        if (userEntity.getIsAutoLogin()) {
            mPresenter.login(username, password, this);
        }
    }

    /**
     * 判断软键盘是否弹出
     */
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            isSoftShow = !(bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0);
        });
    }

    private boolean mVisibility = false;

    @OnClick({R.id.iv_arrow, R.id.iv_visibility, R.id.btn_login, R.id.tv_login_config})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_arrow:
                showPopupWindow(view);
                break;
            case R.id.iv_visibility:
                mVisibility = !mVisibility;
                if (mVisibility) {
                    ivVisibility.setImageResource(R.drawable.ic_visibility);
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivVisibility.setImageResource(R.drawable.ic_visibility_off);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPassword.setSelection(etPassword.getText().toString().length());
                break;
            case R.id.btn_login:
                mPresenter.login(getText(etUsername), getText(etPassword), this);
                break;
            case R.id.tv_login_config:
                startActivity(LoginConfigActivity.class, false);
                break;
        }
    }

    private void showPopupWindow(View view) {
        if (mAllSavedUser.size() != 0) {
            if (mUserListPopup == null) {
                initUserListPopup();
            }
            if (!mUserListPopup.isShowing()) {
                long delayMillis = 0;
                if (isSoftShow) {
                    // 如果软键盘是弹出状态先隐藏软键盘
                    delayMillis = 100;
                    CommonBiz.hideKeyboard(view);
                }
                mHandler.postDelayed(() -> {
                    arrowUp();
                    int yOff = (llUsername.getHeight() - rlUsername.getHeight()) / 2;
                    mUserListPopup.showAsDropDown(rlUsername, 0, yOff);
                }, delayMillis);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void initUserListPopup() {
        UserListAdapter userListAdapter = new UserListAdapter(mContext, mAllSavedUser);
        userListAdapter.setDeleteBtnClickListener(userEntity -> {
            mPresenter.deleteUser(userEntity);
            mAllSavedUser.remove(userEntity);
            userListAdapter.notifyDataSetChanged();
            if (TextUtils.equals(userEntity.getUsername(), getText(etUsername))) {
                // 如果删除的用户是当前回显的用户，清除界面数据
                etUsername.setText("");
                etPassword.setText("");
                civHead.setImageResource(R.drawable.ic_head_single);
            }
            if (mAllSavedUser.size() == 0) {
                mUserListPopup.dismiss();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_user_option, null);
        itemView.measure(0, 0);
        int suggestMaxHeight = ViewUtils.getSuggestMaxHeight(mContext, itemView.getMeasuredHeight());
        ListView listView = new WrapContentListView(mContext, suggestMaxHeight);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(this);
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray5)));
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.d1));

        mUserListPopup = new PopupWindow(listView, rlUsername.getWidth(), -2, true);
        mUserListPopup.setContentView(listView);
        mUserListPopup.setAnimationStyle(R.style.popupWindowAnimStyle);
        mUserListPopup.setBackgroundDrawable(new BitmapDrawable());
        mUserListPopup.setOnDismissListener(this::arrowDown);
    }

    /**
     * 箭头向上转的动画
     */
    private void arrowUp() {
        if (mUpAnim == null) {
            mUpAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            mUpAnim.setDuration(300);
            mUpAnim.setFillAfter(true);
            mUpAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        ivArrow.startAnimation(mUpAnim);
    }

    /**
     * 箭头向下转的动画
     */
    private void arrowDown() {
        if (mDownAnim == null) {
            mDownAnim = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            mDownAnim.setDuration(300);
            mDownAnim.setFillAfter(true);
            mDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        ivArrow.startAnimation(mDownAnim);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * 监听用户名EditText，实时更新头像和密码
     */
    @Override
    public void afterTextChanged(Editable editable) {
        String changedUsername = editable.toString();
        UserEntity userEntity = mPresenter.getUserByUsername(changedUsername);
        if (userEntity != null) {
            ImageLoader.loadUrl(civHead, userEntity.getHeaderUrl(), R.drawable.ic_head_single, R.drawable.ic_head_single);
            etPassword.setText(userEntity.getPassword());
        } else {
            ImageLoader.loadRes(civHead, R.drawable.ic_head_single);
            etPassword.setText("");
        }
    }

    /**
     * 登录中，显示dialog
     */
    @Override
    public void onLogin() {
        // 隐藏软键盘
        CommonBiz.hideKeyboard(etUsername);
        // 登陆中，显示dialog
        if (customDialog == null) {
            customDialog = new CustomDialog.StateBuilder(mContext)
                    .setStateText(R.string.dialog_on_login)
                    .setIrrevocable()
                    .create();
        }
        customDialog.show();
    }

    @Override
    public void loginSuccess() {
        // 隐藏dialog
        hideDialog();
        startActivity(SwitchSysActivity.class, true);
    }

    @Override
    public void loginFailed(int loginErrorCode) {
        // 隐藏dialog
        hideDialog();
        switch (loginErrorCode) {
            case LoginErrorCode.CAN_NOT_EMPTY:
                showToast(R.string.toast_complete_login_data);
                break;
            case LoginErrorCode.NO_IP_AND_PORT:
                showToast(R.string.toast_need_config_ip_port);
                break;
            case LoginErrorCode.LOGIN_FAILED:
                showToast(R.string.toast_login_failed);
                break;
            case LoginErrorCode.WRONG_USER_INFO:
                showToast(R.string.toast_incorrect_username_pwd);
                break;
            case LoginErrorCode.MQ_CONNECT_FAILED:
                showToast(R.string.toast_mq_connect_failed);
                break;
        }
    }

    /**
     * 隐藏状态对话框
     */
    private void hideDialog() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    /**
     * 下拉用户列表item点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserEntity userEntity = mAllSavedUser.get(position);
        String username = userEntity.getUsername();
        String password = userEntity.getPassword();

        // 关掉下拉框
        mUserListPopup.dismiss();
        // 设置新选用户的信息
        ImageLoader.loadUrl(civHead, userEntity.getHeaderUrl(), R.drawable.ic_head_single, R.drawable.ic_head_single);
        etUsername.setText(username);
        etPassword.setText(password);
        etUsername.setSelection(username.length());
        etPassword.setSelection(password.length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
