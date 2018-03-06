package com.winsion.dispatch.modules.grid.activity.patrolitem;

import com.winsion.component.basic.base.BasePresenter;
import com.winsion.component.basic.base.BaseView;
import com.winsion.dispatch.modules.grid.entity.PatrolItemEntity;

import java.util.List;

/**
 * Created by 10295 on 2018/2/1.
 * 巡检项Contract
 */

class PatrolItemContract {
    interface View extends BaseView {
        void getPatrolItemDataSuccess(List<PatrolItemEntity> patrolItemEntities);

        void getPatrolItemDataFailed();
    }

    interface Presenter extends BasePresenter {
        void getPatrolItemData(String patrolId);
    }
}
