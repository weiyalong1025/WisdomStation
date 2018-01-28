package com.winsion.wisdomstation.modules.operation.modules.taskoperator.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.FieldKey;
import com.winsion.wisdomstation.data.constants.JoinKey;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.constants.ViewName;
import com.winsion.wisdomstation.data.entity.ResponseForQueryData;
import com.winsion.wisdomstation.data.entity.WhereClause;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.media.constants.FileStatus;
import com.winsion.wisdomstation.media.constants.FileType;
import com.winsion.wisdomstation.media.entity.LocalRecordEntity;
import com.winsion.wisdomstation.media.entity.ServerRecordEntity;
import com.winsion.wisdomstation.utils.DirAndFileUtils;

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
                        localRecordEntity.setFileType(FileType.TEXT);
                        recordEntities.add(0, localRecordEntity);
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
