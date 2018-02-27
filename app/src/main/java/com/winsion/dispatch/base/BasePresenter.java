package com.winsion.dispatch.base;

/**
 * Created by wyl on 2017/3/20.
 * BasePresenter
 */

public interface BasePresenter {
    void start();

    /**
     * 这里可以做一些资源回收相关的操作，建议在onDestroy()中调用
     * 适用场景1：界面关闭后丢掉还没有完成的网络请求
     * 适用场景2：......
     */
    void exit();
}
