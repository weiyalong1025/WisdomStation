package com.winsion.component.task.constants;

/**
 * Created by 10295 on 2018/2/7.
 * 页面跳转Intent携带数据name
 */

public interface Intents {
    interface Issue {
        /**
         * 发布类型(命令/协作)
         */
        String ISSUE_TYPE = "ISSUE_TYPE";
        /**
         * 选择的班组
         */
        String SELECT_TEAM = "SELECT_TEAM";
    }

    interface OperatorTaskDetail {
        String JOB_ENTITY = "JOB_ENTITY";
    }

    interface MontorTaskDetail {
        String TASK_ENTITY = "TASK_ENTITY";
    }

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

    interface SceneRecord {
        String JOB_OPERATORS_ID = "JOB_OPERATORS_ID";
    }

    interface Media {
        /**
         * 保存文件
         */
        String MEDIA_FILE = "MEDIA_FILE";
    }
}
