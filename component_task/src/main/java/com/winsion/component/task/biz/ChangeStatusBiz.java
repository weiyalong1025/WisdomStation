package com.winsion.component.task.biz;

import android.content.Context;

import com.winsion.component.basic.biz.BasicBiz;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.data.NetDataSource;
import com.winsion.component.basic.constants.OpeCode;
import com.winsion.component.basic.constants.OpeType;
import com.winsion.component.basic.constants.Urls;
import com.winsion.component.basic.listener.ResponseListener;
import com.winsion.component.basic.listener.StateListener;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.utils.FileUtils;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.task.R;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.entity.JobEntity;
import com.winsion.component.task.entity.JobParameter;
import com.winsion.component.task.entity.WarnTaskStep;

import java.io.File;
import java.io.IOException;

/**
 * Created by 10295 on 2017/12/17 0017
 * 操作作业状态
 */

public class ChangeStatusBiz {
    /**
     * 更改任务状态
     *
     * @param opType {@link OpeType}
     */
    public void changeJobStatus(Context context, JobEntity jobEntity, int opType, StateListener listener) {
        // 获取备注
        String note = getNote(context, jobEntity.getJoboperatorsid());
        JobParameter jobParameter = new JobParameter();
        jobParameter.setUsersId(CacheDataSource.getUserId());
        jobParameter.setJobsId(jobEntity.getJobsid());
        jobParameter.setSsId(BasicBiz.getBSSID(context));
        jobParameter.setTaskId(jobEntity.getTasksid());
        jobParameter.setOpormotId(jobEntity.getJoboperatorsid());
        jobParameter.setNote(note);
        jobParameter.setOpType(opType);
        NetDataSource.post(this, Urls.JOb, jobParameter, OpeCode.TASK, new ResponseListener<String>() {
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
    private void confirmWarning(Context context, String stepID) {
        WarnTaskStep warnTaskStep = new WarnTaskStep();
        warnTaskStep.setWarnTaskStepID(stepID);
        NetDataSource.post(this, Urls.WARN_TASK_STEP, warnTaskStep, 0, new ResponseListener<String>() {
            @Override
            public String convert(String jsonStr) {
                return jsonStr;
            }

            @Override
            public void onSuccess(String result) {
                ToastUtils.showToast(context, R.string.toast_confirm_success);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                ToastUtils.showToast(context, R.string.toast_confirm_failed);
            }
        });
    }

    /**
     * 获取任务备注
     */
    private String getNote(Context context, String id) {
        String note = "";
        try {
            File performerDir = DirAndFileUtils.getPerformerDir(CacheDataSource.getUserId(), id);
            File[] files = performerDir.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".txt")) {
                    note = FileUtils.readFile2String(f, "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note;
    }
}
