package com.winsion.component.task.activity.submitproblem;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.listener.UploadListener;
import com.winsion.component.task.R;
import com.winsion.component.task.biz.SubmitBiz;
import com.winsion.component.task.entity.DeviceEntity;
import com.winsion.component.task.entity.SubclassEntity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/5.
 * 上报问题Presenter
 */

public class SubmitProblemPresenter extends SubmitBiz implements SubmitProblemContact.Presenter {
    private final SubmitProblemContact.View mView;
    private final Context mContext;

    SubmitProblemPresenter(SubmitProblemContact.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void checkDeviceId(String deviceId) {
        if (CacheDataSource.getTestMode()) {
            mView.checkDeviceIdSuccess("水龙头", "666", deviceId);
            return;
        }
        ArrayList<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setFields("id");
        whereClause.setValueKey(deviceId);
        whereClauses.add(whereClause);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, null, ViewName.DEVICE_INFO,
                1, new ResponseListener<ResponseForQueryData<List<DeviceEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<DeviceEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<DeviceEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<DeviceEntity>> result) {
                        List<DeviceEntity> dataList = result.getDataList();
                        if (dataList != null && dataList.size() == 1) {
                            DeviceEntity deviceDto = dataList.get(0);
                            String deviceName = deviceDto.getDevicename();
                            String classificationId = deviceDto.getClassificationid();
                            mView.checkDeviceIdSuccess(deviceName, classificationId, deviceId);
                        } else {
                            mView.checkDeviceIdFailed(R.string.toast_device_mismatch);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.checkDeviceIdFailed(R.string.toast_check_device_failed);
                    }
                });
    }

    @Override
    public void getSubclass(String classificationId) {
        if (CacheDataSource.getTestMode()) {
            List<SubclassEntity> subclassEntities = new ArrayList<>();
            SubclassEntity subclassEntity = new SubclassEntity();
            subclassEntity.setId("123");
            subclassEntity.setClassificationid("456");
            subclassEntity.setPlancosttime(20);
            subclassEntity.setPriority(2);
            subclassEntity.setTypename("测试数据");
            subclassEntities.add(subclassEntity);
            mView.getSubclassSuccess(subclassEntities);
            return;
        }
        ArrayList<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setFields("classificationId");
        whereClause.setValueKey(classificationId);
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClauses.add(whereClause);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, null, ViewName.SUBCLASS_INFO,
                1, new ResponseListener<ResponseForQueryData<List<SubclassEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<SubclassEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<SubclassEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<SubclassEntity>> result) {
                        mView.getSubclassSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getSubclassFailed();
                    }
                });
    }

    @Override
    public void uploadFile(File uploadFile, UploadListener uploadListener) {
        NetDataSource.uploadFileNoData(this, uploadFile, uploadListener);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
