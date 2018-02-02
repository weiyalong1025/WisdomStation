package com.winsion.dispatch.modules.grid.fragment.patrolplan;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.grid.entity.PatrolPlanEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 * 巡检计划Contract
 */

class PatrolPlanContract {
    interface View extends BaseView{
        void getPatrolPlanDataSuccess(List<PatrolPlanEntity> patrolPlanDate);
        void getPatrolPlanDataFailed();
    }

    interface Presenter extends BasePresenter{
        void getPatrolPlanData();
    }
}
