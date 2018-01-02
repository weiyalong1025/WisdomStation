package com.winsion.wisdomstation.reminder.fragment.systemremind;

import com.winsion.wisdomstation.base.BasePresenter;
import com.winsion.wisdomstation.base.BaseView;
import com.winsion.wisdomstation.reminder.entity.RemindEntity;

import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:21
 */

public class SystemRemindContract {
    interface View extends BaseView {
        void getRemindDataSuccess(List<RemindEntity> remindEntities);

        void getRemindDataFailed();

        void handleRemindsSuccess(int handleType);

        void handleRemindsFailed(int handleType);
    }

    interface Presenter extends BasePresenter {
        void getRemindData();

        void handleReminds(List<String> remindIds, int handleType);
    }
}
