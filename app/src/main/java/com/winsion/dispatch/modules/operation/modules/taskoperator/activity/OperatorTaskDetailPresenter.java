package com.winsion.dispatch.modules.operation.modules.taskoperator.activity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.media.constants.FileStatus;
import com.winsion.dispatch.media.constants.FileType;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;
import com.winsion.dispatch.utils.DirAndFileUtils;
import com.winsion.dispatch.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Presenter
 */

public class OperatorTaskDetailPresenter implements OperatorTaskDetailContract.Presenter {
    private OperatorTaskDetailContract.View mView;

    OperatorTaskDetailPresenter(OperatorTaskDetailContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public ArrayList<LocalRecordEntity> getLocalFile(String jobsId) {
        ArrayList<LocalRecordEntity> recordEntities = new ArrayList<>();
        try {
            String userId = CacheDataSource.getUserId();
            File performerDir = DirAndFileUtils.getPerformerDir(userId, jobsId);
            File[] files = performerDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileStatus(FileStatus.NO_UPLOAD);
                    localRecordEntity.setFile(file);
                    if (name.endsWith(".jpg")) {
                        localRecordEntity.setFileType(FileType.PICTURE);
                        recordEntities.add(localRecordEntity);
                    } else if (name.endsWith(".mp4")) {
                        localRecordEntity.setFileType(FileType.VIDEO);
                        recordEntities.add(localRecordEntity);
                    } else if (name.endsWith(".aac")) {
                        localRecordEntity.setFileType(FileType.AUDIO);
                        recordEntities.add(localRecordEntity);
                    } else if (name.endsWith(".txt")) {
                        String noteContent = FileUtils.readFile2String(file, "UTF-8");
                        if (!TextUtils.isEmpty(noteContent)) {
                            localRecordEntity.setFileType(FileType.TEXT);
                            recordEntities.add(0, localRecordEntity);
                        }
                    }
                }
                return recordEntities;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recordEntities;
    }

    @Override
    public void getServerFile(String jobOperatorsId) {
        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause where = new WhereClause();
        where.setFieldKey(FieldKey.EQUALS);
        where.setJoinKey(JoinKey.OTHER);
        where.setFields("joboperatorsid");
        where.setValueKey(jobOperatorsId);
        whereClauses.add(where);

        NetDataSource.post(getClass(), Urls.BASE_QUERY, whereClauses, null, ViewName.FILE_INFO,
                1, new ResponseListener<ResponseForQueryData<List<ServerRecordEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<ServerRecordEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<ServerRecordEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<ServerRecordEntity>> result) {
                        mView.onServerFileGetSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {

                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(getClass());
    }
}
