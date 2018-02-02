package com.winsion.dispatch.modules.reminder.fragment.systemremind;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.reminder.entity.RemindEntity;

import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:21
 */

class SystemRemindContract {
    interface View extends BaseView {
        void getRemindDataSuccess(List<RemindEntity> remindEntities);

        void getRemindDataFailed();

        void handleRemindsSuccess(List<RemindEntity> reminds, int handleType);

        void handleRemindsFailed(int handleType);
    }

    interface Presenter extends BasePresenter {
        void getRemindData();

        void handleReminds(List<RemindEntity> reminds, int handleType);
    }
}
