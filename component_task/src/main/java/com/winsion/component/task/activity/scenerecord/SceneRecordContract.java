package com.winsion.component.task.activity.scenerecord;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.media.entity.ServerRecordEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/3/22.
 * 现场记录Contract
 */

class SceneRecordContract {
    interface View extends BaseView {
        void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList);

        void onPerformerUploadFileGetFailed();
    }

    interface Presenter extends BasePresenter {
        void getPerformerUploadedFile(String jobOperatorsId);
    }
}
