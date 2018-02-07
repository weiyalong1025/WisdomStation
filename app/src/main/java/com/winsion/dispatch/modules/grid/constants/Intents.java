package com.winsion.dispatch.modules.grid.constants;

/**
 * Created by 10295 on 2018/2/7
 */

public interface Intents {
    interface SubmitProblem {
        /**
         * 巡检项对象
         */
        String PATROL_ITEM_ENTITY = "PATROL_ITEM_ENTITY";
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
