package com.winsion.dispatch.modules.grid.activity.submitproblem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.R;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.entity.DeviceEntity;
import com.winsion.dispatch.modules.operation.entity.FileEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/2/5.
 * 上报问题Presenter
 */

public class SubmitProblemPresenter implements SubmitProblemContact.Presenter {
    private SubmitProblemContact.View mView;

    SubmitProblemPresenter(SubmitProblemContact.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void checkDeviceId(String deviceId) {
        ArrayList<WhereClause> whereClauses = new ArrayList<>();
        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setJoinKey(JoinKey.OTHER);
        whereClause.setFields("id");
        whereClause.setValueKey(deviceId);
        whereClauses.add(whereClause);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, null, ViewName.DEVICE_INFO, 1,
                new ResponseListener<ResponseForQueryData<List<DeviceEntity>>>() {
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
                            mView.checkDeviceIdFailed(R.string.device_mismatch);
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.checkDeviceIdFailed(R.string.check_device_failed);
                    }
                });
    }

    @Override
    public void getSubclass(String classificationId) {

    }

    @Override
    public void submitWithDevice(String patrolDetailId, String problemTypeId, List<FileEntity> fileEntities, String comment, String deviceId) {

    }

    @Override
    public void submitWithoutDevice(String patrolDetailId, List<FileEntity> fileEntities, String comment) {

    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
