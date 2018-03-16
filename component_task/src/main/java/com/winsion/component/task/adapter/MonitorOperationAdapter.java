package com.winsion.component.task.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.ImageView;
import android.widget.TextView;

import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.basic.utils.constants.Formatter;
import com.winsion.component.task.R;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.entity.JobEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/3/16.
 * 任务监控详情界面作业列表adapter
 */

public class MonitorOperationAdapter extends CommonAdapter<JobEntity> {

    public MonitorOperationAdapter(Context context, List<JobEntity> data) {
        super(context, R.layout.task_item_monitor_operation, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, JobEntity mJobEntity, int position) {
        // 设置任务名
        TextView tvTaskName = viewHolder.getView(R.id.tv_task_name);
        tvTaskName.setText(mJobEntity.getTaskname());
        tvTaskName.setSelected(true);

        // 根据任务类型显示任务类型名称和任务类型图标
        int taskType = mJobEntity.getTaktype();
        switch (taskType) {
            // 任务
            case TaskType.TASK:
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_operation_name));
                viewHolder.setVisible(R.id.iv_type_icon, false);
                break;
            // 命令
            case TaskType.COMMAND:
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_command_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.task_ic_command);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 协作
            case TaskType.COOPERATE:
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_cooperation_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.task_ic_cooperation);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 网格
            case TaskType.GRID:
                viewHolder.setText(R.id.task_type_name, getString(R.string.value_grid_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.task_ic_type_grid);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 预案
            case TaskType.PLAN:
                viewHolder.setText(R.id.task_type_name, getString(R.string.value_alarm_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.task_ic_type_alarm);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
        }

        // 设置位置信息
        viewHolder.setText(R.id.tv_location, mJobEntity.getTaskareaname());

        // 把需要用到的时间转换好
        int lastTime = 0;
        long currentTime = System.currentTimeMillis();

        String planStartTimeStr = mJobEntity.getPlanstarttime();
        String realStartTimeStr = mJobEntity.getRealstarttime();
        long planStartTime = ConvertUtils.parseDate(planStartTimeStr, Formatter.DATE_FORMAT1);
        long realStartTime = ConvertUtils.parseDate(realStartTimeStr, Formatter.DATE_FORMAT1);

        String planEndTimeStr = mJobEntity.getPlanendtime();
        String realEndTimeStr = mJobEntity.getRealendtime();
        long planEndTime = ConvertUtils.parseDate(planEndTimeStr, Formatter.DATE_FORMAT1);
        long realEndTime = ConvertUtils.parseDate(realEndTimeStr, Formatter.DATE_FORMAT1);

        // 设置任务计划和实际开始结束时间
        String splitPlanStartTime = ConvertUtils.splitToHM(planStartTimeStr);
        String splitPlanEndTime = ConvertUtils.splitToHM(planEndTimeStr);
        viewHolder.setText(R.id.tv_plan_time, String.format("%s ~ %s", splitPlanStartTime, splitPlanEndTime));

        String splitRealStartTime = ConvertUtils.splitToHM(realStartTimeStr);
        String splitRealEndTime = ConvertUtils.splitToHM(realEndTimeStr);
        viewHolder.setText(R.id.tv_real_time, String.format("%s ~ %s", splitRealStartTime, splitRealEndTime));

        boolean isTimeOut = false;
        int workStatus = mJobEntity.getWorkstatus();
        viewHolder.setText(R.id.btn_status, mJobEntity.getOpteamname());
        switch (workStatus) {
            case TaskState.NOT_STARTED:
                // 未开始
                // 设置持续时间
                viewHolder.setText(R.id.tv_last_time, String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planStartTime < currentTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_timeout_unstart);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_start);
                }
                break;
            case TaskState.RUN:
                // 进行中
                // 设置持续时间
                lastTime = (int) ((System.currentTimeMillis() - realStartTime) / (60 * 1000));
                viewHolder.setText(R.id.tv_last_time, String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < currentTime;
                ImageView ivStatus = viewHolder.getView(R.id.iv_status);
                if (isTimeOut) {
                    ImageLoader.loadGif(ivStatus, R.drawable.task_gif_doing_timeout);
                } else {
                    ImageLoader.loadGif(ivStatus, R.drawable.task_gif_doing);
                }
                break;
            case TaskState.DONE:
                // 已完成
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                viewHolder.setText(R.id.tv_last_time, String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_timeout_done);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_done);
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 验收未通过
                // 设置持续时间
                lastTime = (int) ((realEndTime - realStartTime) / (60 * 1000));
                viewHolder.setText(R.id.tv_last_time, String.format("%s%s", String.valueOf(lastTime), getString(R.string.suffix_minute)));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_pass);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_pass);
                }
                break;
        }

        // 根据是否超时设置任务模块背景色
        if (isTimeOut) {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.basic_yellow1);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.basic_red2);
        } else {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.basic_gray8);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.basic_blue1);
        }
        viewHolder.setOnClickListener(R.id.btn_video, v -> ToastUtils.showToast(mContext, "视频"));
        viewHolder.setOnClickListener(R.id.btn_broadcast, v -> ToastUtils.showToast(mContext, "广播"));
        viewHolder.setOnClickListener(R.id.btn_talkback, v -> ToastUtils.showToast(mContext, "对讲"));
        viewHolder.setOnClickListener(R.id.btn_message, v -> ToastUtils.showToast(mContext, "消息"));
        viewHolder.setOnClickListener(R.id.btn_call, v -> ToastUtils.showToast(mContext, "呼叫"));
    }

    private String getString(@StringRes int strRes) {
        return mContext.getResources().getString(strRes);
    }
}
