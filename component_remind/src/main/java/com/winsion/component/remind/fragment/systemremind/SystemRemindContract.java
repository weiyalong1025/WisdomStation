package com.winsion.component.remind.fragment.systemremind;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.component.remind.entity.SystemRemindEntity;

import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:21
 */

class SystemRemindContract {
    interface View extends BaseView {
        void getRemindDataSuccess(List<SystemRemindEntity> remindEntities);

        void getRemindDataFailed();

        void handleRemindsSuccess(List<SystemRemindEntity> reminds, int handleType);

        void handleRemindsFailed(int handleType);
    }

    interface Presenter extends BasePresenter {
        void getRemindData();

        void handleReminds(List<SystemRemindEntity> reminds, int handleType);
    }
}
