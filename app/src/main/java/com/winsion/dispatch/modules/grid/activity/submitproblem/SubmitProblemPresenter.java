package com.winsion.dispatch.modules.grid.activity.submitproblem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.R;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.data.listener.UploadListener;
import com.winsion.dispatch.modules.grid.biz.SubmitBiz;
import com.winsion.dispatch.modules.grid.entity.DeviceEntity;
import com.winsion.dispatch.modules.grid.entity.SubclassEntity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/5.
 * 上报问题Presenter
 */

public class SubmitProblemPresenter extends SubmitBiz implements SubmitProblemContact.Presenter {
    private SubmitProblemContact.View mView;

    SubmitProblemPresenter(SubmitProblemContact.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void checkDeviceId(String deviceId) {
        if (AppApplication.TEST_MODE) {
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
        if (AppApplication.TEST_MODE) {
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
