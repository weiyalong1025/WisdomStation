package com.winsion.component.basic.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winsion.component.basic.utils.HandlerUtils;
import com.winsion.component.basic.utils.LogUtils;
import com.winsion.component.basic.utils.ToastUtils;

import java.util.List;

/**
 * Created by admin on 2016/11/18.
 * 基类-Activity
 */

public abstract class BaseActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, View.OnClickListener {
    /**
     * 默认的REQUEST_CODE
     */
    private static final int CODE_DEFAULT = 0;

    public Context mContext;
    public LayoutInflater mInflater;
    public HandlerUtils.HandlerHolder mHandler;

    @Override
    public void handlerMessage(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInflater = LayoutInflater.from(mContext);
        mHandler = new HandlerUtils.HandlerHolder(this);
        if (setContentView() != 0) {
            setContentView(setContentView());
        }
        start();
    }

    /**
     * 设置显示布局
     *
     * @return layout资源ID
     */
    protected abstract int setContentView();

    /**
     * 入口
     */
    protected abstract void start();

    public void showToast(@StringRes int resId) {
        ToastUtils.showToast(mContext, resId);
    }

    public void showToast(String msg) {
        ToastUtils.showToast(mContext, msg);
    }

    /**
     * 显示LOG，TAG为当前类名
     *
     * @param msg
     */
    public void logI(String msg) {
        LogUtils.i(getClass().getSimpleName(), msg);
    }

    /**
     * 显示LOG，TAG为当前类名
     *
     * @param msg
     */
    public void logE(String msg) {
        LogUtils.e(getClass().getSimpleName(), msg);
    }

    /**
     * 开启activity，默认不关闭当前activity
     *
     * @param cls 要打开的activity
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(cls, false);
    }

    /**
     * 开启activity
     *
     * @param cls                  要打开的activity
     * @param closeCurrentActivity 是否需要关闭当前页面
     */
    public void startActivity(Class<? extends Activity> cls, boolean closeCurrentActivity) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (closeCurrentActivity) {
            finish();
        }
    }

    /**
     * 不需要requestCode时调用，会传入默认的code
     *
     * @param intent 要跳转的Activity
     */
    public void startActivityForResult(Intent intent) {
        startActivityForResult(intent, CODE_DEFAULT);
    }

    /**
     * 不需要requestCode时调用，会传入默认的code
     *
     * @param cls 要跳转的Activity
     */
    public void startActivityForResult(Class<? extends Activity> cls) {
        startActivityForResult(cls, CODE_DEFAULT);
    }

    public void startActivityForResult(Class<? extends Activity> cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    public void showView(ViewGroup container, View v) {
        v.setVisibility(View.VISIBLE);
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = container.getChildAt(i);
            if (childAt.getId() != v.getId()) {
                childAt.setVisibility(View.GONE);
            }
        }
    }

    public String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    /**
     * 判断两个字符串是否相等
     */
    public boolean equals(CharSequence a, CharSequence b) {
        return TextUtils.equals(a, b);
    }

    public boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 实现fragment拦截该事件，fragment不做处理再交给activity
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
            for (Fragment childFragment : childFragments) {
                if (childFragment.isVisible() &&
                        childFragment.getUserVisibleHint() &&
                        childFragment instanceof BaseFragment &&
                        ((BaseFragment) childFragment).onKeyDown(keyCode)) {
                    return true;
                }
            }
            if (fragment.isVisible() &&
                    fragment.getUserVisibleHint() &&
                    fragment instanceof BaseFragment &&
                    ((BaseFragment) fragment).onKeyDown(keyCode)) {
                return true;
            }
        }
        // 按返回键不退出程序,当且仅当当前activity为根activity才会生效
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addOnClickListeners(@IdRes int... ids) {
        if (ids != null) {
            for (@IdRes int id : ids) {
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
    }
}
