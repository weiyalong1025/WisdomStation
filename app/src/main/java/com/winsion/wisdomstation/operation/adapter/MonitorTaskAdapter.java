package com.winsion.wisdomstation.operation.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.operation.constants.TaskState;
import com.winsion.wisdomstation.operation.constants.TaskType;
import com.winsion.wisdomstation.operation.entity.TaskEntity;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.winsion.wisdomstation.view.GifView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/18 0018.
 */

public class MonitorTaskAdapter extends CommonAdapter<TaskEntity> {
    private OnButtonClickListener mListener;

    public interface OnButtonClickListener {
        void onButtonClick(TaskEntity taskEntity, View button);
    }

    public MonitorTaskAdapter(Context context, List<TaskEntity> data) {
        super(context, R.layout.item_operation, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TaskEntity taskEntity, int position) {
        viewHolder.setVisible(R.id.iv_job_icon, false);
        // 最后一条数据显示条目底部的分割线
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }
        // 根据任务类型显示对应的名称和显隐对应的任务类型图标
        int taskType = taskEntity.getTaktype();
        String taskName = taskEntity.getTaskname();

        // 网格和预案任务不显示车次
        if (taskType == TaskType.GRID || taskType == TaskType.PLAN) {
            viewHolder.setVisible(R.id.tv_train_number, false);
            viewHolder.setVisible(R.id.tv_train_number_title, false);
        } else {
            viewHolder.setVisible(R.id.tv_train_number, true);
            viewHolder.setVisible(R.id.tv_train_number_title, true);
            String trainNumber = taskEntity.getTrainnumber();
            trainNumber = TextUtils.isEmpty(trainNumber) ? getString(R.string.nothing) : trainNumber;
            viewHolder.setText(R.id.tv_train_number, trainNumber);
        }

        // 任务名称过长时显示为跑马灯效果
        TextView tvTaskName = viewHolder.getView(R.id.tv_task_name);
        tvTaskName.setText(taskName);
        tvTaskName.setSelected(true);

        // 设置按钮背景
        viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.btn_bg_finish);

        // 根据任务类型显示任务类型名称、任务类型图标和按钮显示文字
        int jobSize = taskEntity.getJob();
        switch (taskType) {
            // 任务
            case TaskType.TASK:
                viewHolder.setText(R.id.task_type_name, getString(R.string.task_name));
                viewHolder.setVisible(R.id.iv_type_icon, false);
                viewHolder.setText(R.id.btn_status, jobSize + getString(R.string.a_job));
                break;
            // 命令
            case TaskType.COMMAND:
                viewHolder.setText(R.id.task_type_name, getString(R.string.command_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_command);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                viewHolder.setText(R.id.btn_status, jobSize + getString(R.string.a_command));
                break;
            // 协作
            case TaskType.COOPERATE:
                viewHolder.setText(R.id.task_type_name, getString(R.string.cooperation_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_cooperation);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                viewHolder.setText(R.id.btn_status, jobSize + getString(R.string.a_cooperation));
                break;
            // 网格
            case TaskType.GRID:
                viewHolder.setText(R.id.task_type_name, getString(R.string.grid_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_grid1);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                viewHolder.setText(R.id.btn_status, jobSize + getString(R.string.a_job));
                break;
            // 预案
            case TaskType.PLAN:
                viewHolder.setText(R.id.task_type_name, getString(R.string.alarm_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_alarm);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                viewHolder.setText(R.id.btn_status, jobSize + getString(R.string.a_plan));
                break;
        }

        // 把需要用到的时间转换好
        long currentTime = System.currentTimeMillis();
        int lastTime;

        String planStartTimeStr = taskEntity.getPlanstarttime();
        String realStartTimeStr = taskEntity.getRealstarttime();
        long planStartTime = ConvertUtils.parseDate(planStartTimeStr, Formatter.DATE_FORMAT1);
        long realStartTime = ConvertUtils.parseDate(realStartTimeStr, Formatter.DATE_FORMAT1);

        String planEndTimeStr = taskEntity.getPlanendtime();
        String realEndTimeStr = taskEntity.getRealendtime();
        long planEndTime = ConvertUtils.parseDate(planEndTimeStr, Formatter.DATE_FORMAT1);
        long realEndTime = ConvertUtils.parseDate(realEndTimeStr, Formatter.DATE_FORMAT1);

        // 设置计划开始时间、计划结束时间
        viewHolder.setText(R.id.tv_plan_start_time, ConvertUtils.splitToMDHM(planStartTimeStr));
        viewHolder.setText(R.id.tv_plan_end_time, ConvertUtils.splitToMDHM(planEndTimeStr));

        // 根据任务完成状态(1.未开始、2.进行中、3.已完成、4.超时未开始、5.超时进行中、6.超时已完成、7.验收未通过)
        // 显示1.Item底色、2.状态图标、3.执行时间(包括文字颜色)
        boolean isTimeOut = false;
        int workStatus = taskEntity.getTaskstatus();
        switch (workStatus) {
            case TaskState.NOT_STARTED:
                // 持续时间
                lastTime = 0;
                viewHolder.setText(R.id.tv_last_time, lastTime + "分");
                // 判断是否超时
                isTimeOut = planStartTime < currentTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_timeout_unstart);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_not_start);
                }
                break;
            case TaskState.RUN:
                // 持续时间
                lastTime = (int) ((currentTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + "分");
                // 判断是否超时
                isTimeOut = planEndTime < currentTime;
                GifView gifView = viewHolder.getView(R.id.doing_gif);
                if (isTimeOut) {
                    gifView.setMovieResource(R.drawable.gif_doing_timeout);
                } else {
                    gifView.setMovieResource(R.drawable.gif_doing);
                }
                break;
            case TaskState.DONE:
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + "分");
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    // 超时已完成
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_timeout_done);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_done);
                }
                // 如果是网格任务已完成状态则显示为待验收
                if (taskType == TaskType.GRID) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_wait_pass);
                    viewHolder.setText(R.id.btn_status, getString(R.string.wait_acceptance));
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + "分");
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    // 超时已完成
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_not_pass);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_not_pass);
                }
                break;
        }
        switch (workStatus) {
            case TaskState.NOT_STARTED:
            case TaskState.DONE:
            case TaskState.GRID_NOT_PASS:
                viewHolder.setVisible(R.id.doing_gif, false);
                viewHolder.setVisible(R.id.iv_status, true);
                break;
            case TaskState.RUN:
                viewHolder.setVisible(R.id.doing_gif, true);
                viewHolder.setVisible(R.id.iv_status, false);
                break;

        }
        if (isTimeOut) {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.yellow1);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.red2);
        } else {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.gray8);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.blue1);
        }

        // 更改任务状态按钮点击事件
        viewHolder.setOnClickListener(R.id.btn_status, view -> {
            if (mListener != null) {
                mListener.onButtonClick(taskEntity, view);
            }
        });
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mListener = listener;
    }
}
