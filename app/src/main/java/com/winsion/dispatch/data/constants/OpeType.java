package com.winsion.dispatch.data.constants;

/**
 * Created by wyl on 2017/8/15
 */
public interface OpeType {
    /**
     * 开始
     */
    int BEGIN = 0;

    /**
     * 正在操作
     */
    int RUNNING = 1;

    /**
     * 作业完成
     */
    int COMPLETE = 2;

    /**
     * 验收未通过
     */
    int NOT_PASS = 3;

    /**
     * 作业监视
     */
    int MONITO = 4;

    /**
     * 验收通过
     */
    int PASS = 5;
}
