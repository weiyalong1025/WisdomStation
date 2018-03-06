package com.winsion.dispatch.modules.grid.activity.submitproblem;

import android.support.annotation.StringRes;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.basic.data.listener.UploadListener;
import com.winsion.dispatch.modules.grid.entity.SubclassEntity;

import java.io.File;
import java.util.List;

/**
 * Created by 10295 on 2018/2/5.
 * 上报问题Contact
 */

class SubmitProblemContact {
    interface View extends BaseView {
        void checkDeviceIdSuccess(String deviceName, String classificationId, String deviceId);

        void checkDeviceIdFailed(@StringRes int errorInfo);

        void getSubclassSuccess(List<SubclassEntity> list);

        void getSubclassFailed();
    }

    interface Presenter extends BasePresenter {
        void checkDeviceId(String deviceId);

        void getSubclass(String classificationId);

        void uploadFile(File uploadFile, UploadListener uploadListener);
    }
}
