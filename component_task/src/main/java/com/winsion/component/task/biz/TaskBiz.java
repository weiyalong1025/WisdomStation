package com.winsion.component.task.biz;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.FieldKey;
import com.winsion.component.basic.constants.FileStatus;
import com.winsion.component.basic.constants.FileType;
import com.winsion.component.basic.constants.JoinKey;
import com.winsion.component.basic.constants.OpeCode;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.entity.LocalRecordEntity;
import com.winsion.component.basic.entity.ResponseForQueryData;
import com.winsion.component.basic.entity.ServerRecordEntity;
import com.winsion.component.basic.entity.WhereClause;
import com.winsion.component.basic.listener.MyDownloadListener;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.listener.StateListener;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.FileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.task.R;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.constants.TrainAreaType;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.JobParameter;
import com.winsion.component.task.entity.WarnTaskStep;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.SearchFileField.FIELD_MONITOR;

/**
 * Created by 10295 on 2017/12/17 0017
 * 操作作业状态
 */

public class TaskBiz {
    /**
     * 更改任务状态
     *
     * @param opType {@link OpeType}
     */
    public void changeJobStatus(Context context, JobEntity jobEntity, int opType, StateListener listener) {
        // 获取备注
        String note = getNote(jobEntity.getJoboperatorsid());
        JobParameter jobParameter = new JobParameter();
        jobParameter.setUsersId(CacheDataSource.getUserId());
        jobParameter.setJobsId(jobEntity.getJobsid());
        jobParameter.setSsId(BasicBiz.getBSSID(context));
        jobParameter.setTaskId(jobEntity.getTasksid());
        jobParameter.setOpormotId(jobEntity.getJoboperatorsid());
        jobParameter.setNote(note);
        jobParameter.setOpType(opType);
        NetDataSource.post(this, Urls.JOb, jobParameter, OpeCode.TASK, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                listener.onSuccess();
                // 如果是预警任务开始需要调用确认接口
                if (opType == OpeType.BEGIN && jobEntity.getTaktype() == TaskType.PLAN) {
                    confirmWarning(context, jobEntity.getRunsid());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                listener.onFailed();
            }
        });
    }

    /**
     * 确认预警
     */
    private void confirmWarning(Context context, String stepID) {
        WarnTaskStep warnTaskStep = new WarnTaskStep();
        warnTaskStep.setWarnTaskStepID(stepID);
        NetDataSource.post(this, Urls.WARN_TASK_STEP, warnTaskStep, 0, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                ToastUtils.showToast(context, R.string.toast_confirm_success);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                ToastUtils.showToast(context, R.string.toast_confirm_failed);
            }
        });
    }

    /**
     * 获取任务备注
     */
    private String getNote(String jobOperatorId) {
        String note = "";
        try {
            File performerDir = DirAndFileUtils.getPerformerDir(CacheDataSource.getUserId(), jobOperatorId);
            File[] files = performerDir.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".txt")) {
                    note = FileUtils.readFile2String(f, "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note;
    }

    /**
     * 格式化车次数据
     *
     * @return {股道，站台，检票口，候车室}
     */
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

    public void downloadFile(String url, String targetPath, MyDownloadListener myDownloadListener) {
        NetDataSource.downloadFile(this, url, targetPath, myDownloadListener).start();
    }

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

    protected void getUploadedFile(String field, String valueKey, String viewName, UploadFileGetListener uploadFileGetListener) {
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
                            uploadFileGetListener.onPublisherUploadFileGetSuccess(result.getDataList());
                        } else {
                            uploadFileGetListener.onPerformerUploadFileGetSuccess(result.getDataList());
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        if (field.equals(FIELD_MONITOR)) {
                            uploadFileGetListener.onPublisherUploadFileGetFailed();
                        } else {
                            uploadFileGetListener.onPerformerUploadFileGetFailed();
                        }
                    }
                });
    }

    public interface UploadFileGetListener {
        void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> dataList);

        void onPublisherUploadFileGetFailed();

        void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> dataList);

        void onPerformerUploadFileGetFailed();
    }
}
