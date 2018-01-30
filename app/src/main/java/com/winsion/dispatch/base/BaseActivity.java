package com.winsion.dispatch.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winsion.dispatch.utils.HandlerUtils;
import com.winsion.dispatch.utils.LogUtils;
import com.winsion.dispatch.utils.ToastUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by admin on 2016/11/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener {
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
            ButterKnife.bind(this);
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
        startActivity(cls, null, false);
    }

    /**
     * 开启activity
     *
     * @param cls                  要打开的activity
     * @param closeCurrentActivity 是否需要关闭当前页面
     */
    public void startActivity(Class<? extends Activity> cls, boolean closeCurrentActivity) {
        startActivity(cls, null, closeCurrentActivity);
    }

    /**
     * 开启activity
     *
     * @param cls                  要打开的activity
     * @param bundle               传递的数据
     * @param closeCurrentActivity 是否需要关闭当前页面
     */
    public void startActivity(Class<? extends Activity> cls, Bundle bundle, boolean closeCurrentActivity) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (closeCurrentActivity) {
            finish();
        }
    }

    public void startActivityForResult(Class<? extends Activity> cls, int requestCode) {
        startActivityForResult(cls, requestCode, null);
    }

    public void startActivityForResult(Class<? extends Activity> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
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
     *
     * @param a
     * @param b
     * @return
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
}
