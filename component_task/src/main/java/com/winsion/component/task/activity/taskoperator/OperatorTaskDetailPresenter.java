package com.winsion.component.task.activity.taskoperator;

import android.content.Context;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.listener.MyUploadListener;
import com.winsion.component.media.constants.FileType;
import com.winsion.component.media.entity.ServerRecordEntity;
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.entity.FileEntity;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.JobParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.winsion.component.task.constants.SearchFileField.FIELD_MONITOR;
import static com.winsion.component.task.constants.SearchFileField.FIELD_PERFORMER;

/**
 * Created by 10295 on 2018/1/19.
 * 我的任务Presenter
 */

public class OperatorTaskDetailPresenter extends TaskBiz implements OperatorTaskDetailContract.Presenter, TaskBiz.UploadFileGetListener {
    private final OperatorTaskDetailContract.View mView;
    private final Context mContext;

    OperatorTaskDetailPresenter(OperatorTaskDetailContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    /**
     * 查询命令/协作发布人上传的附件
     */
    @Override
    public void getPublisherUploadedFile(String jobsId) {
        getUploadedFile(FIELD_MONITOR, jobsId, ViewName.MONITOR_FILE, this);
    }

    /**
     * 查询执行人上传的附件
     */
    @Override
    public void getPerformerUploadedFile(String jobOperatorsId) {
        getUploadedFile(FIELD_PERFORMER, jobOperatorsId, ViewName.FILE_INFO, this);
    }

    @Override
    public void onPublisherUploadFileGetSuccess(List<ServerRecordEntity> dataList) {
        mView.onPublisherUploadFileGetSuccess(dataList);
    }

    @Override
    public void onPublisherUploadFileGetFailed() {

    }

    @Override
    public void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> dataList) {
        mView.onPerformerUploadFileGetSuccess(dataList);
    }

    @Override
    public void onPerformerUploadFileGetFailed() {

    }

    @Override
    public void uploadFile(JobEntity jobEntity, File file, MyUploadListener myUploadListener) {
        JobParameter jobParameter = new JobParameter();
        jobParameter.setSsId(BasicBiz.getBSSID(mContext));
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

        NetDataSource.uploadFile(this, jobParameter, file, myUploadListener).start();
    }

    /**
     * 根据文件名返回文件类型
     *
     * @param fileName 文件名
     * @return 没有符合的返回-1
     */
    private int getFileType(String fileName) {
        if (fileName.endsWith(".jpg")) {
            return FileType.PICTURE;
        } else if (fileName.endsWith(".mp4")) {
            return FileType.VIDEO;
        } else if (fileName.endsWith(".aac")) {
            return FileType.AUDIO;
        }
        return -1;
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
    }
}
