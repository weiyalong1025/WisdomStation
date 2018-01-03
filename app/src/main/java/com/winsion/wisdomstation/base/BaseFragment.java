package com.winsion.wisdomstation.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winsion.wisdomstation.utils.HandlerUtils;
import com.winsion.wisdomstation.utils.LogUtils;
import com.winsion.wisdomstation.utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by yalong on 2016/6/13.
 */
public abstract class BaseFragment extends Fragment implements HandlerUtils.OnReceiveMessageListener {
    private View mContentView;
    protected Context mContext;
    public HandlerUtils.HandlerHolder mHandler;

    @Override
    public void handlerMessage(Message msg) {

    }

    public BaseFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mHandler = new HandlerUtils.HandlerHolder(this);
        // 缓存Fragment，防止切换造成UI的重绘
        if (mContentView == null) {
            mContentView = setContentView();
            ButterKnife.bind(this, mContentView);
            init();
        }
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
        return mContentView;
    }

    /**
     * 设置Fragment的内容View
     */
    protected abstract View setContentView();

    /**
     * 初始化
     */
    protected abstract void init();

    public void showToast(int resId) {
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
     * 跳转Activity
     *
     * @param cls
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(cls, null);
    }

    /**
     * 跳转Activity
     *
     * @param cls
     */
    public void startActivity(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
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
        return textView.getText().toString();
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

    public int getColor(@ColorRes int colorResId) {
        return getResources().getColor(colorResId);
    }

    public boolean onKeyDown(int keyCode) {
        return false;
    }
}
