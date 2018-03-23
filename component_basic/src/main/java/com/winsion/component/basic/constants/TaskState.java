package com.winsion.component.basic.constants;

/**
 * Created by wyl on 2017/6/29
 */
public interface TaskState {
    /**
     * 未开始
     */
    String NODONE = "ACKNOWLEGED";
    /**
     * 进行中
     */
    String RUN = "Progress";
    /**
     * 已结束
     */
    String DONE = "FIXED";
    /**
     * 已确认
     */
    String CONFIRMED = "CONFRIMED";
    /**
     * 未通过
     */
    String UNPASS = "UNPASS";
}
