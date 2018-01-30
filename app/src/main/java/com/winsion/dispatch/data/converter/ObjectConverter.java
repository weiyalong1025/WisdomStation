package com.winsion.dispatch.data.converter;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.convert.Converter;
import com.winsion.dispatch.data.entity.KingkongResponse;
import com.winsion.dispatch.data.listener.ResponseListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 10295 on 2017/12/18 0018
 */

public class ObjectConverter<T> implements Converter<T> {
    private ResponseListener<T> mListener;

    public ObjectConverter(ResponseListener<T> listener) {
        this.mListener = listener;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null || mListener == null) return null;
        KingkongResponse kingkongResponse = JSON.parseObject(body.string(), KingkongResponse.class);
        int code = kingkongResponse.getCode();
        if (code != 0 || !kingkongResponse.isSuccess()) {
            Observable.empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> mListener.onFailed(code, kingkongResponse.getMessage()))
                    .subscribe();
            return null;
        }
        return mListener.convert(kingkongResponse.getData());
    }
}
