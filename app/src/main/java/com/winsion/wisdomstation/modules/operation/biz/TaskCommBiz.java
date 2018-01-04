package com.winsion.wisdomstation.modules.operation.biz;

import android.content.Context;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.common.biz.CommonBiz;
import com.winsion.wisdomstation.common.listener.StateListener;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.data.NetDataSource;
import com.winsion.wisdomstation.data.constants.OpeCode;
import com.winsion.wisdomstation.data.constants.OpeType;
import com.winsion.wisdomstation.data.constants.Urls;
import com.winsion.wisdomstation.data.listener.ResponseListener;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.operation.entity.JobEntity;
import com.winsion.wisdomstation.modules.operation.entity.JobParameter;
import com.winsion.wisdomstation.modules.operation.entity.WarnTaskStep;
import com.winsion.wisdomstation.utils.FilePathUtils;
import com.winsion.wisdomstation.utils.IOUtils;
import com.winsion.wisdomstation.utils.ToastUtils;

import java.io.File;

/**
 * Created by 10295 on 2017/12/17 0017.
 */

public class TaskCommBiz {
    /**
     * 更改任务状态
     *
     * @param opType {@link OpeType}
     */
    public static void changeJobStatus(Context context, JobEntity jobEntity, int opType, StateListener listener) {
        // 获取备注
        String note = getNote(context, jobEntity.getJoboperatorsid());
        JobParameter jobParameter = new JobParameter();
        jobParameter.setUsersId(CacheDataSource.getUserId());
        jobParameter.setJobsId(jobEntity.getJobsid());
        jobParameter.setSsId(CommonBiz.getBSSID(context));
        jobParameter.setTaskId(jobEntity.getTasksid());
        jobParameter.setOpormotId(jobEntity.getJoboperatorsid());
        jobParameter.setNote(note);
        jobParameter.setOpType(opType);
        NetDataSource.post(null, Urls.JOb, jobParameter, OpeCode.TASK, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                listener.onSuccess();
                // 如果是预警任务开始需要调用确认接口
                if (opType == OpeType.BEGIN && jobEntity.getTaktype() == TaskType.PLAN) {
                    confirmWarning(context, jobEntity.getRunsid());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                listener.onFailed();
            }
        });
    }

    /**
     * 确认预警
     */
    private static void confirmWarning(Context context, String stepID) {
        WarnTaskStep warnTaskStep = new WarnTaskStep();
        warnTaskStep.setWarnTaskStepID(stepID);
        NetDataSource.post(null, Urls.WARN_TASK_STEP, warnTaskStep, 0, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                ToastUtils.showToast(context, R.string.confirm_success);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                ToastUtils.showToast(context, R.string.confirm_failed);
            }
        });
    }

    /**
     * 获取任务备注
     */
    private static String getNote(Context context, String id) {
        String note = "";
        try {
            File file = new File(FilePathUtils.getPerformerPath(CacheDataSource.getUserId(), id));
            if (file.exists()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.getName().endsWith(".txt")) {
                        note = IOUtils.read(f);
                    }
                }
            }
        } catch (Exception e) {
            ToastUtils.showToast(context, R.string.please_check_sdcard_state);
        }
        return note;
    }
}
