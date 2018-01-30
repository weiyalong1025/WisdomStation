package com.winsion.dispatch.data.listener;

/**
 * Created by admin on 2016/7/28.
 */
public interface ResponseListener<T> {
    /**
     * 我运行在子线程
     */
    T convert(String jsonStr);

    void onSuccess(T result);

    void onFailed(int errorCode, String errorInfo);
}
