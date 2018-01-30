package com.winsion.dispatch.modules.grid.modules.patrolplan.fragment;

import com.winsion.dispatch.base.BasePresenter;
import com.winsion.dispatch.base.BaseView;
import com.winsion.dispatch.modules.grid.entity.PatrolTaskEntity;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 */

public class PatrolPlanContract {
    interface View extends BaseView{
        void getPatrolPlanDataSuccess(List<PatrolTaskEntity> patrolPlanDate);
        void getPatrolPlanDataFailed();
    }

    interface Presenter extends BasePresenter{
        void getPatrolPlanData();
    }
}
