package com.winsion.dispatch.data.constants;

/**
 * Created by wyl on 2017/6/10
 */
public interface Urls {
    // 用户登录
    String LOGIN = "kingkong/0.01/auth/userLogin";
    // 查询作业列表数据
    String BASE_QUERY = "kingkong/0.01/job/findByBaseCondition";
    // 用户上传文件
    String UPLOAD = "kingkong/0.01/job/uploadJobOperatorFile";
    // 更改任务状态
    String JOb = "kingkong/0.01/job/PublishTaskCommand";
    // 上传单个文件
    String UPLOAD_SINGLE = "kingkong/0.01/fileUpload/uploadSingleFile";
    // 获取联系人列表
    String QUERY_CONTACT_LIST = "kingkong/0.01/job/findByBaseCondition";
    // 向服务器发送心跳
    String QUERY_HEARTBEAT = "kingkong/0.01/auth/heartBeat";
    // 用户登出请求
    String USER_LOGOUT = "kingkong/0.01/auth/userLogout";
    // 问题状态变更
    String PROBLEM_STATE_CHANGE = "grid/0.01/patrolSubmit/problemStateChange";
    // 巡检结果提交
    String SUBMIT = "grid/0.01/patrolSubmit/submit";
    // 预警步骤
    String WARN_TASK_STEP = "kingkong/0.01/warn/feedbackWarnTaskStep";
    // 查询设备对应区域列表
    String DEVICE_AREA = "kingkong/0.01/device/findGroupByCondition";
    // 改变设备状态
    String CHANGE_DEVICE = "kingkong/0.01/airCondition/changeDeviceValue";
    // 改变消息状态
    String MESSAGE_HANDLING = "kingkong/0.01/job/messageHandling";
    // 检查更新
    String CHECK_UPDATE = "/kingkong/0.01/file/versionCheck";
}
