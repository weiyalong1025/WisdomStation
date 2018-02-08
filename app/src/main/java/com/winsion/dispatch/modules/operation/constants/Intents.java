package com.winsion.dispatch.modules.operation.constants;

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
         * 接收班组(可为空)
         */
        String TO_TEAM_ENTITY = "TO_TEAM_ENTITY";
        /**
         * 选择的班组
         */
        String SELECT_TEAM = "SELECT_TEAM";
    }

    interface OperatorTaskDetail {
        String JOB_ENTITY = "JOB_ENTITY";
    }
}
