package com.winsion.component.basic.constants;

/**
 * Created by wyl on 2017/8/30
 */
public interface MQType {
    /**
     * 用户登录通知
     */
    int USER_LOGIN = 0;

    /**
     * 灯光状态通知
     */
    int LAMP_STATE = 1;

    /**
     * 作业状态变化通知
     */
    int JOB_STATE = 2;

    /**
     * 作业操作状态变化通知
     */
    int JOBOPERATOR_STATE = 3;

    /**
     * 发布任务或者命令
     */
    int PUBLISH_TASK = 4;

    /**
     * 任务状态变化通知
     */
    int TASK_STATE = 5;

    /**
     * 检测用户最后登录通知
     */
    int CHECK_USER_LASTLOGIN = 6;

    /**
     * 上传附件通知
     */
    int UPLOAD_APPENDIX = 7;

    /**
     * 用户离线状态通知
     */
    int OFFLINE_STATE = 8;

    /**
     * 电梯状态通知
     */
    int ELEVATOR_STATE = 9;

    /**
     * 用户退出通知
     */
    int USER_LOGOUT = 10;

    /**
     * 报警
     */
    int ALARM = 11;

    /**
     * 警情任务发送
     */
    int ALARM_CONTENT = 12;

    /**
     * 警情设备关闭通知
     */
    int ALRAM_CLOSEDEVICE = 13;

    /**
     * 警情设备状态更新通知
     */
    int ALARM_DEVICESTATE = 14;

    /**
     * 警报解除
     */
    int CLOSE_ALARM = 15;

    /**
     * 停车场状态
     */
    int PARKINGSTATE = 16;

    /**
     * 设定状态
     */
    int SET_DEVICE_STATE = 17;

    /**
     * 设定设备模式
     */
    int SET_DEVICE_MDOE = 18;

    /**
     * 设定设备值
     */
    int SET_DEVICE_VALUE = 19;

    /**
     * 预案步骤
     */
    int ALARM_STEP = 21;

    /**
     * 任务提醒
     */
    int TASK_REMIND = 26;

    /**
     * 更新到发信息
     */
    int TRAIN_UPDATE = 32;

    /**
     * PC对终端消息
     */
    int TEMP_MSG = 999;
}
