package com.winsion.dispatch.modules.grid.constants;

/**
 * Created by 10295 on 2018/2/7
 */

public interface Intents {
    interface SubmitProblem {
        /**
         * 问题关联巡检ID
         */
        String PATROL_DETAIL_ID = "PATROL_DETAIL_ID";
        /**
         * 地点
         */
        String SITE_NAME = "SITE_NAME";
        /**
         * 是否与设备相关
         */
        String DEVICE_DEPENDENT = "DEVICE_DEPENDENT";
    }

    interface PatrolItem {
        String PATROL_TASK_ENTITY = "PATROL_TASK_ENTITY";
    }
}
