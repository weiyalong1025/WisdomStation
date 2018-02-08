package com.winsion.dispatch.modules.operation.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.operation.constants.TaskState;
import com.winsion.dispatch.modules.operation.constants.TaskType;
import com.winsion.dispatch.modules.operation.entity.JobEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.constants.Formatter;
import com.winsion.dispatch.view.GifView;
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
        void onButtonClick(JobEntity jobEntity, View button);
    }

    public OperatorTaskListAdapter(Context context, List<JobEntity> data) {
        super(context, R.layout.item_operation, data);
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
            viewHolder.setVisible(R.id.tv_train_number, false);
            viewHolder.setVisible(R.id.tv_train_number_title, false);
        } else {
            viewHolder.setVisible(R.id.tv_train_number, true);
            viewHolder.setVisible(R.id.tv_train_number_title, true);
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
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_task_name));
                viewHolder.setVisible(R.id.iv_type_icon, false);
                break;
            // 命令
            case TaskType.COMMAND:
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_command_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_command);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 协作
            case TaskType.COOPERATE:
                viewHolder.setText(R.id.task_type_name, getString(R.string.name_cooperation_name));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_cooperation);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 网格
            case TaskType.GRID:
                viewHolder.setText(R.id.task_type_name, getString(R.string.value_grid_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_grid1);
                viewHolder.setVisible(R.id.iv_type_icon, true);
                break;
            // 预案
            case TaskType.PLAN:
                viewHolder.setText(R.id.task_type_name, getString(R.string.value_alarm_task));
                viewHolder.setImageResource(R.id.iv_type_icon, R.drawable.ic_alarm);
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
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.btn_bg_start);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_click_to_start));
                // 持续时间
                lastTime = 0;
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
                // 判断是否超时
                isTimeOut = planStartTime < currentTime;
                if (isTimeOut) {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_timeout_unstart);
                } else {
                    viewHolder.setImageResource(R.id.iv_status, R.drawable.ic_not_start);
                }
                break;
            case TaskState.RUN:
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.btn_bg_finish);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_click_to_finish));
                // 持续时间
                lastTime = (int) ((currentTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
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
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.btn_bg_done);
                viewHolder.setText(R.id.btn_status, getString(R.string.spinner_finished));
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
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
                    viewHolder.setText(R.id.btn_status, getString(R.string.btn_wait_acceptance));
                }
                break;
            case TaskState.GRID_NOT_PASS:
                // 设置按钮背景和文字
                viewHolder.setBackgroundRes(R.id.btn_status, R.drawable.btn_bg_restart);
                viewHolder.setText(R.id.btn_status, getString(R.string.btn_restart));
                // 持续时间
                lastTime = (int) ((realEndTime - realStartTime) / 60000);
                viewHolder.setText(R.id.tv_last_time, lastTime + getString(R.string.suffix_minute));
                // 判断是否超时
                isTimeOut = planEndTime < realEndTime;
                if (isTimeOut) {
                    // 超时已完成，因没有超时未通过图标，这里使用一样的
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

        /*
        渐变效果
        if (isTimeOut) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View view = viewHolder.getView(R.id.ll_bg_color);
                ValueAnimator valueAnimator = (ValueAnimator) view.getTag();
                if (valueAnimator == null) {
                    valueAnimator = ValueAnimator.ofArgb(0xFF333339, 0xFF74592C);
                    valueAnimator.setDuration(2000);
                    valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
                    valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    valueAnimator.addUpdateListener(animation -> {
                        int colorValue = (int) animation.getAnimatedValue();
                        viewHolder.setBackgroundColor(R.id.ll_bg_color, colorValue);
                    });
                }
                viewHolder.setTag(R.id.ll_bg_color, valueAnimator);
                valueAnimator.start();
            } else {
                viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.yellow1);
            }
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.red2);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View view = viewHolder.getView(R.id.ll_bg_color);
                ValueAnimator valueAnimator = (ValueAnimator) view.getTag();
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
            }
            viewHolder.setBackgroundRes(R.id.ll_bg_color, R.color.gray8);
            viewHolder.setTextColorRes(R.id.tv_last_time, R.color.blue1);
        }*/

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
                mListener.onButtonClick(jobEntity, view);
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
