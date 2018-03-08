package com.winsion.dispatch.modules.operation.activity.taskoperator;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.biz.CommonBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.data.constants.FieldKey;
import com.winsion.component.basic.data.constants.JoinKey;
import com.winsion.component.basic.data.constants.OpeType;
import com.winsion.component.basic.data.constants.Urls;
import com.winsion.component.basic.data.constants.ViewName;
import com.winsion.component.basic.data.entity.ResponseForQueryData;
import com.winsion.component.basic.data.entity.WhereClause;
import com.winsion.component.basic.data.listener.MyDownloadListener;
import com.winsion.component.basic.data.listener.ResponseListener;
import com.winsion.component.basic.data.listener.UploadListener;
import com.winsion.dispatch.media.constants.FileStatus;
import com.winsion.component.basic.constants.FileType;
import com.winsion.dispatch.media.entity.LocalRecordEntity;
import com.winsion.dispatch.media.entity.ServerRecordEntity;
import com.winsion.dispatch.modules.operation.biz.ChangeStatusBiz;
import com.winsion.dispatch.modules.operation.constants.TrainAreaType;
import com.winsion.dispatch.modules.operation.entity.FileEntity;
import com.winsion.dispatch.modules.operation.entity.JobEntity;
import com.winsion.dispatch.modules.operation.entity.JobParameter;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Presenter
 */

public class OperatorTaskDetailPresenter extends ChangeStatusBiz implements OperatorTaskDetailContract.Presenter {
    private static final String FIELD_MONITOR = "jobsid";   // 查询监控人上传附件用的字段
    private static final String FIELD_PERFORMER = "joboperatorsid"; // 查询执行人上传附件用的字段

    private final OperatorTaskDetailContract.View mView;
    private final Context mContext;

    OperatorTaskDetailPresenter(OperatorTaskDetailContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
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
                localRecordEntity.setFileName(file.getName());
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
    public void download(String url, String targetPath, MyDownloadListener myDownloadListener) {
        NetDataSource.downloadFile(this, url, targetPath, myDownloadListener).start();
    }

    @Override
    public void upload(JobEntity jobEntity, File file, UploadListener uploadListener) {
        JobParameter jobParameter = new JobParameter();
        jobParameter.setSsId(CommonBiz.getBSSID(mContext));
        jobParameter.setTaskId(jobEntity.getTasksid());
        jobParameter.setOpormotId(jobEntity.getJoboperatorsid());
        jobParameter.setOpType(OpeType.RUNNING);
        jobParameter.setJobsId(jobEntity.getJobsid());
        jobParameter.setUsersId(CacheDataSource.getUserId());

        List<FileEntity> fileList = new ArrayList<>();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getName());
        fileEntity.setFileType(getFileType(file.getName()));
        fileList.add(fileEntity);
        jobParameter.setFileList(fileList);

        NetDataSource.uploadFile(this, jobParameter, file, uploadListener);
    }

    /**
     * 根据文件名返回文件类型
     *
     * @param fileName 文件名
     * @return 没有符合的返回-1
     */
    private static int getFileType(String fileName) {
        if (fileName.endsWith(".jpg")) {
            return FileType.PICTURE;
        } else if (fileName.endsWith(".mp4")) {
            return FileType.VIDEO;
        } else if (fileName.endsWith(".aac")) {
            return FileType.AUDIO;
        }
        return -1;
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

    /**
     * 格式化车次数据
     *
     * @return {股道，站台，检票口，候车室}
     */
    @Override
    public String[] formatTrainData(String[] areaType, String[] name) {
        String track = "--";
        String platform = "--";
        String waitRoom = "--";
        String checkPort = "--";
        for (int i = 0; i < name.length; i++) {
            switch (areaType[i]) {
                case TrainAreaType.TRACK:
                    if (TextUtils.equals(track, "--")) {
                        track = name[i];
                    } else {
                        track += "," + name[i];
                    }
                    break;
                case TrainAreaType.PLATFORM:
                    if (TextUtils.equals(platform, "--")) {
                        platform = name[i];
                    } else {
                        platform += "," + name[i];
                    }
                    break;
                case TrainAreaType.WAITING_ROOM:
                    if (TextUtils.equals(waitRoom, "--")) {
                        waitRoom = name[i];
                    } else {
                        waitRoom += "," + name[i];
                    }
                    break;
                case TrainAreaType.TICKET_ENTRANCE:
                    if (TextUtils.equals(checkPort, "--")) {
                        checkPort = name[i];
                    } else {
                        checkPort += "," + name[i];
                    }
                    break;
            }
        }
        return new String[]{track, platform, waitRoom, checkPort};
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
