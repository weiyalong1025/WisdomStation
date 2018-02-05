package com.winsion.dispatch.modules.grid.activity.submitproblem;

import android.support.annotation.StringRes;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.grid.entity.SubclassEntity;
import com.winsion.dispatch.modules.operation.entity.FileEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/2/5.
 * 上报问题Contact
 */

public class SubmitProblemContact {
    interface View extends BaseView {
        void checkDeviceIdSuccess(String deviceName, String classificationId, String deviceId);

        void checkDeviceIdFailed(@StringRes int errorInfo);

        void getSubclassSuccess(List<SubclassEntity> list);

        void getSubclassFailed();

        void submitSuccess();

        void submitFailed();
    }

    interface Presenter extends BasePresenter {
        void checkDeviceId(String deviceId);

        void getSubclass(String classificationId);

        void submitWithDevice(String patrolDetailId, String problemTypeId, List<FileEntity> fileEntities, String comment, String deviceId);

        void submitWithoutDevice(String patrolDetailId, List<FileEntity> fileEntities, String comment);
    }
}
