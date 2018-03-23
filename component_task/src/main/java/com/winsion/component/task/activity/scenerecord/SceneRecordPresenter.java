package com.winsion.component.task.activity.scenerecord;

import com.winsion.component.basic.constants.ViewName;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.media.entity.ServerRecordEntity;
import com.winsion.component.task.biz.TaskBiz;

import java.util.List;

import static com.winsion.component.task.constants.SearchFileField.FIELD_PERFORMER;

/**
 * Created by 10295 on 2018/3/22.
 * 现场记录Presenter
 */

public class SceneRecordPresenter extends TaskBiz implements SceneRecordContract.Presenter, TaskBiz.UploadFileGetListener {
    private SceneRecordContract.View mView;

    SceneRecordPresenter(SceneRecordContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {

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
        mView.onPerformerUploadFileGetFailed();
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
    }
}
