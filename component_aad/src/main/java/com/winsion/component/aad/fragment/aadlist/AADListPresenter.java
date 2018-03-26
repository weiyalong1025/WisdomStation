package com.winsion.component.aad.fragment.aadlist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.aad.entity.AADEntity;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.listener.ResponseListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 10295 on 2018/3/26.
 * 到发列表Presenter
 */

public class AADListPresenter implements AADListContract.Presenter {
    private AADListContract.View mView;

    AADListPresenter(AADListContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void getAADListData(int aadType) {
        NetDataSource.post(this, Urls.BASE_QUERY_AAD, null, null, ViewName.AAD,
                1, new ResponseListener<ResponseForQueryData<List<AADEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<AADEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<AADEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<AADEntity>> result) {
                        mView.getAADListDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getAADListDataFailed();
                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
