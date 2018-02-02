package com.winsion.dispatch.modules.operation.activity.taskoperator;

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
import com.winsion.dispatch.data.listener.DownloadListener;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.data.listener.UploadListener;
import com.winsion.dispatch.media.constants.FileStatus;
import com.winsion.dispatch.media.constants.FileType;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;
import com.winsion.dispatch.modules.operation.entity.JobEntity;
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
    // 查询监控人上传附件用的字段
    private static final String FIELD_MONITOR = "jobsid";
    // 查询执行人上传附件用的字段
    private static final String FIELD_PERFORMER = "joboperatorsid";

    private OperatorTaskDetailContract.View mView;

    OperatorTaskDetailPresenter(OperatorTaskDetailContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public ArrayList<LocalRecordEntity> getPerformerLocalFile(String jobOperatorsId) {
        String userId = CacheDataSource.getUserId();
        ArrayList<LocalRecordEntity> performerLocalFile = new ArrayList<>();
        try {
            File performerDir = DirAndFileUtils.getPerformerDir(userId, jobOperatorsId);
            performerLocalFile.addAll(getLocalFile(performerDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return performerLocalFile;
    }

    @Override
    public ArrayList<LocalRecordEntity> getPublisherLocalFile(String jobsId) {
        String userId = CacheDataSource.getUserId();
        ArrayList<LocalRecordEntity> publisherLocalFile = new ArrayList<>();
        try {
            File monitorDir = DirAndFileUtils.getMonitorDir(userId, jobsId);
            publisherLocalFile.addAll(getLocalFile(monitorDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publisherLocalFile;
    }

    private ArrayList<LocalRecordEntity> getLocalFile(File parentFile) {
        ArrayList<LocalRecordEntity> recordEntities = new ArrayList<>();
        File[] files = parentFile.listFiles();
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
        }
        return recordEntities;
    }

    /**
     * 查询命令/协作发布人上传的附件
     */
    @Override
    public void getPublisherUploadedFile(String jobsId) {
        getUploadedFile(FIELD_MONITOR, jobsId, ViewName.MONITOR_FILE);
    }

    /**
     * 查询执行人上传的附件
     */
    @Override
    public void getPerformerUploadedFile(String jobOperatorsId) {
        getUploadedFile(FIELD_PERFORMER, jobOperatorsId, ViewName.FILE_INFO);
    }

    @Override
    public void download(String url, String targetPath, DownloadListener downloadListener) {
        NetDataSource.downloadFile(this, url, targetPath, downloadListener);
    }

    @Override
    public void upload(JobEntity jobEntity, File file, UploadListener uploadListener) {
        NetDataSource.uploadFile(this, jobEntity, file, uploadListener);
    }

    private void getUploadedFile(String field, String valueKey, String viewName) {
        List<WhereClause> whereClauses = new ArrayList<>();
        WhereClause where = new WhereClause();
        where.setFieldKey(FieldKey.EQUALS);
        where.setJoinKey(JoinKey.OTHER);
        where.setFields(field);
        where.setValueKey(valueKey);
        whereClauses.add(where);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, null, viewName,
                1, new ResponseListener<ResponseForQueryData<List<ServerRecordEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<ServerRecordEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<ServerRecordEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<ServerRecordEntity>> result) {
                        if (field.equals(FIELD_MONITOR)) {
                            mView.onPublisherUploadedFileGetSuccess(result.getDataList());
                        } else {
                            mView.onPerformerUploadedFileGetSuccess(result.getDataList());
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {

                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
