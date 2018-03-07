package com.winsion.dispatch.modules.grid.biz;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.HttpParams;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.constants.DeviceState;
import com.winsion.dispatch.modules.grid.entity.PatrolItemEntity;
import com.winsion.dispatch.modules.operation.entity.FileEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/2/7.
 * 上报问题Biz
 */

public class SubmitBiz {
    public interface SubmitListener {
        void submitSuccess(PatrolItemEntity patrolItemEntity, String deviceState);

        void submitFailed();
    }

    /**
     * 设备无关问题上报
     *
     * @param patrolItemEntity 巡检项对象
     * @param deviceState      状态
     * @param submitListener   上报状态回调
     */
    public void submitWithoutDevice(PatrolItemEntity patrolItemEntity, String deviceState, SubmitListener submitListener) {
        submitWithoutDevice(patrolItemEntity, deviceState, null, null, submitListener);
    }

    /**
     * 设备无关问题上报
     *
     * @param patrolItemEntity 巡检项对象
     * @param deviceState      状态
     * @param fileEntities     上传附件
     * @param comment          描述
     * @param submitListener   上报状态回调
     */
    public void submitWithoutDevice(PatrolItemEntity patrolItemEntity, String deviceState,
                                    List<FileEntity> fileEntities, String comment,
                                    SubmitListener submitListener) {
        if (CacheDataSource.getTestMode()) {
            submitListener.submitSuccess(patrolItemEntity, deviceState);
            return;
        }
        String problemImageLink = "";
        if (fileEntities != null) {
            problemImageLink = JSON.toJSONString(fileEntities);
        }
        if (comment == null) {
            comment = "";
        }

        HttpParams httpParams = new HttpParams();
        httpParams.put("patrolDetailId", patrolItemEntity.getId());
        httpParams.put("userId", CacheDataSource.getUserId());
        httpParams.put("teamId", CacheDataSource.getTeamId());
        httpParams.put("deviceState", deviceState);
        httpParams.put("problemImageLink", problemImageLink);
        httpParams.put("comment", comment);

        NetDataSource.post(this, Urls.SUBMIT_WITHOUT_DEVICE, httpParams, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                if (result.equals("true"))
                    submitListener.submitSuccess(patrolItemEntity, deviceState);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                submitListener.submitFailed();
            }
        });
    }

    /**
     * 设备相关问题上报
     *
     * @param patrolItemEntity 巡检项对象
     * @param problemTypeId    问题类型ID
     * @param fileEntities     上传附件
     * @param comment          描述
     * @param deviceId         设备ID
     * @param submitListener   上报状态回调
     */
    public void submitWithDevice(PatrolItemEntity patrolItemEntity, String problemTypeId,
                                 List<FileEntity> fileEntities, String comment,
                                 String deviceId, SubmitListener submitListener) {
        if (CacheDataSource.getTestMode()) {
            submitListener.submitSuccess(patrolItemEntity, DeviceState.FAILURE);
            return;
        }
        String problemImageLink = "";
        if (fileEntities.size() != 0) {
            problemImageLink = JSON.toJSONString(fileEntities);
        }

        HttpParams httpParams = new HttpParams();
        httpParams.put("patrolDetailId", patrolItemEntity.getId());
        httpParams.put("deviceState", DeviceState.FAILURE);
        httpParams.put("userId", CacheDataSource.getUserId());
        httpParams.put("teamId", CacheDataSource.getTeamId());
        httpParams.put("problemTypeId", problemTypeId);
        httpParams.put("problemImageLink", problemImageLink);
        httpParams.put("comment", comment);
        httpParams.put("deviceId", deviceId);

        NetDataSource.post(this, Urls.SUBMIT_WITH_DEVICE, httpParams, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                if (TextUtils.equals(result, "true")) {
                    submitListener.submitSuccess(patrolItemEntity, DeviceState.FAILURE);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                submitListener.submitFailed();
            }
        });
    }
}
