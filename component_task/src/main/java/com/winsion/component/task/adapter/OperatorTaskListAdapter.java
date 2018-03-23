package com.winsion.component.task.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.task.R;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.constants.TaskType;
import com.winsion.component.task.entity.JobEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/18 0018
 */

public class OperatorTaskListAdapter extends CommonAdapter<JobEntity> {
    private Context mContext;
    private List<JobEntity> listData;
    private OnButtonClickListener mListener;

    public interface OnButtonClickListener {
        void onButtonClick(JobEntity jobEntity);
    }

    public OperatorTaskListAdapter(Context context, List<JobEntity> data) {
        super(context, R.layout.task_item_operation, data);
        this.mContext = context;
        this.listData = data;
    }

    @Override
    protected void convert(ViewHolder viewHolder, JobEntity jobEntity, int position) {
        viewHolder.setVisible(R.id.iv_job_icon, false);
        // 最后一条数据显示条目底部的分割线
        if (position == listData.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }
        // 根据任务类型显示对应的名称和显隐对应的任务类型图标
        int taskType = jobEntity.getTaktype();
        String taskName = jobEntity.getTaskname();

        // 网格和预案任务不显示车次
        if (taskType == TaskType.GRID || taskType == TaskType.PLAN) {
            viewHolder.setVisible(R.id.ll_train_info, false);
        } else {
            viewHolder.setVisible(R.id.ll_train_info, true);
            String trainNumber = jobEntity.getTrainnumber();
            trainNumber = TextUtils.isEmpty(trainNumber) ? getString(R.string.value_nothing) : trainNumber;
            viewHolder.setText(R.id.tv_train_number, trainNumber);
        }

        // 任务名称过长时显示为跑马灯效果
        TextView tvTaskName = viewHolder.getView(R.id.tv_task_name);
        tvTaskName.setText(taskName);
        tvTaskName.setSelected(true);

        // 根据任务类型显示任务类型名称和任务类型图标
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

        // 把需要用到的时间转换好
        int lastTime;
        long currentTime = System.currentTimeMillis();

        String planStartTimeStr = jobEntity.getPlanstarttime();
        String realStartTimeStr = jobEntity.getRealstarttime();
        long planStartTime = ConvertUtils.parseDate(planStartTimeStr, Formatter.DATE_FORMAT1);
        long realStartTime = ConvertUtils.parseDate(realStartTimeStr, Formatter.DATE_FORMAT1);

        String planEndTimeStr = jobEntity.getPlanendtime();
        String realEndTimeStr = jobEntity.getRealendtime();
        long planEndTime = ConvertUtils.parseDate(planEndTimeStr, Formatter.DATE_FORMAT1);
        long realEndTime = ConvertUtils.parseDate(realEndTimeStr, Formatter.DATE_FORMAT1);

        // 设置计划开始时间、计划结束时间
        viewHolder.setText(R.id.tv_plan_start_time, ConvertUtils.splitToMDHM(planStartTimeStr));
        viewHolder.setText(R.id.tv_plan_end_time, ConvertUtils.splitToMDHM(planEndTimeStr));

        // 根据任务完成状态(1.未开始、2.进行中、3.已完成、4.超时未开始、5.超时进行中、6.超时已完成、7.验收未通过)
        // 显示1.Item底色、2.按钮底色、3.按钮文字、4.状态图标、5.执行时间(包括文字颜色)
        boolean isTimeOut = false;
        int workStatus = jobEntity.getWorkstatus();
        switch (workStatus) {
            case TaskState.NOT_STARTED:
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.task_btn_bg_start);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_click_to_start));
                // 持续时间
                lastTime = 0;
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
                // 判断是否超时
                isTimeOut = planStartTime < currentTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_timeout_unstart);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_start);
                }
                break;
            case TaskState.RUN:
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.task_btn_bg_finish);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_click_to_finish));
                // 持续时间
                lastTime = (int) ((currentTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
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
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.task_btn_bg_done);
                viewHolder.setText(R.id.btn_status, getString(R.string.spinner_finished));
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    // 超时已完成
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_timeout_done);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_done);
                }
                // 如果是网格任务已完成状态则显示为待验收
                if (taskType == TaskType.GRID) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_wait_pass);
                    viewHolder.setText(R.id.btn_status, getString(R.string.btn_wait_pass));
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.task_btn_bg_restart);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_restart));
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    // 超时已完成，因没有超时未通过图标，这里使用一样的
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_pass);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.task_ic_not_pass);
                }
                break;
        }

        if (isTimeOut) {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.basic_yellow1);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.basic_red2);
        } else {
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.basic_gray8);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.basic_blue1);
        }

        // 更改任务状态按钮点击事件
        viewHolder.setOnClickListener(R.id.btn_status, view -> {
            if (mListener != null) {
                mListener.onButtonClick(jobEntity);
            }
        });

        Button btnStatus = viewHolder.getView(R.id.btn_status);
        if (jobEntity.isInOperation()) {
            btnStatus.setEnabled(false);
        } else {
            btnStatus.setEnabled(true);
        }
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mListener = listener;
    }
}
