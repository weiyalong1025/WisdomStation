package com.winsion.wisdomstation.login;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Editable;
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

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.SwitchSysActivity;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.biz.CommonBiz;
import com.winsion.wisdomstation.config.LoginConfigActivity;
import com.winsion.wisdomstation.login.constants.LoginErrorCode;
import com.winsion.wisdomstation.login.entity.UserEntity;
import com.winsion.wisdomstation.login.listener.LoginListener;
import com.winsion.wisdomstation.utils.ImageLoader;
import com.winsion.wisdomstation.utils.constants.ListType;
import com.winsion.wisdomstation.view.CircleImageView;
import com.winsion.wisdomstation.view.TipDialog;
import com.winsion.wisdomstation.view.WrapContentListView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

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

    private List<UserEntity> mAllSavedUser = new ArrayList<>();

    private LoginContract.Presenter mPresenter;
    private TipDialog mLoadingDialog;
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
            if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                isSoftShow = false;
            } else {
                isSoftShow = true;
            }
        });
    }

    @OnClick({R.id.iv_arrow, R.id.btn_login, R.id.tv_login_config})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_arrow:
                showPopupWindow(view);
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

    private void initUserListPopup() {
        CommonAdapter<UserEntity> commonAdapter = new CommonAdapter<UserEntity>(mContext, R.layout.item_user_option, mAllSavedUser) {
            @Override
            protected void convert(ViewHolder viewHolder, UserEntity item, int position) {
                // 设置头像
                ImageView imageView = viewHolder.getView(R.id.civ_head);
                ImageLoader.loadUrl(imageView, item.getHeaderUrl(), R.drawable.ic_head_single, R.drawable.ic_head_single);

                // 设置用户名
                String username = item.getUsername();
                viewHolder.setText(R.id.item_text, username);

                // 删除点击事件
                viewHolder.setOnClickListener(R.id.iv_delete, (v) -> {
                    mPresenter.deleteUser(item);
                    mAllSavedUser.remove(item);
                    notifyDataSetChanged();
                    if (TextUtils.equals(username, getText(etUsername))) {
                        // 如果删除的用户是当前回显的用户，清除界面数据
                        etUsername.setText("");
                        etPassword.setText("");
                        civHead.setImageResource(R.drawable.ic_head_single);
                    }
                    if (mAllSavedUser.size() == 0) {
                        mUserListPopup.dismiss();
                    }
                });
            }
        };

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View itemView = layoutInflater.inflate(R.layout.item_user_option, null);
        itemView.measure(0, 0);
        int suggestMaxHeight = CommonBiz.getSuggestMaxHeight(mContext, itemView.getMeasuredHeight(), ListType.TYPE_POPUP);
        ListView listView = new WrapContentListView(mContext, suggestMaxHeight);
        listView.setAdapter(commonAdapter);
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
        if (mLoadingDialog == null) {
            mLoadingDialog = new TipDialog.Builder(mContext)
                    .setIconType(TipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getString(R.string.on_login))
                    .create();
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
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
                showToast(R.string.username_and_password_can_not_be_empty);
                break;
            case LoginErrorCode.NO_IP_AND_PORT:
                showToast(R.string.please_configure_the_IP_address_and_port_number_first);
                break;
            case LoginErrorCode.LOGIN_FAILED:
                showToast(R.string.login_failed);
                break;
            case LoginErrorCode.WRONG_USER_INFO:
                showToast(R.string.incorrect_username_or_password);
                break;
            case LoginErrorCode.MQ_CONNECT_FAILED:
                showToast(R.string.mq_connect_failed);
                break;
        }
    }

    /**
     * 隐藏用户列表下拉框
     */
    private void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
