package com.winsion.dispatch.modules.grid.adapter;

import android.content.Context;

import com.winsion.dispatch.R;
import com.winsion.component.task.constants.TaskState;
import com.winsion.component.task.entity.TaskEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 * 问题管理Adapter
 */

public class ProblemManageAdapter extends CommonAdapter<TaskEntity> {
    public interface ConfirmButtonListener {
        void onPassButtonClick(TaskEntity taskEntity);

        void onNotPassButtonClick(TaskEntity taskEntity);
    }

    private ConfirmButtonListener confirmButtonListener;

    public ProblemManageAdapter(Context context, List<TaskEntity> data) {
        super(context, R.layout.item_problem, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TaskEntity taskEntity, int position) {
        String[] split = taskEntity.getTaskname().split(" ");
        String deviceName;
        String subclassName;
        if (split.length != 2) {
            deviceName = subclassName = "数据解析异常";
        } else {
            deviceName = split[0];
            subclassName = split[1];
        }
        viewHolder.setText(R.id.tv_commit_time, taskEntity.getPlanstarttime().substring(0, 16));
        viewHolder.setText(R.id.tv_location, taskEntity.getAreaname());
        viewHolder.setText(R.id.tv_device_name, deviceName);
        viewHolder.setText(R.id.tv_grade, taskEntity.getMemo());
        viewHolder.setText(R.id.tv_subclass_name, subclassName);
        viewHolder.setText(R.id.tv_desc, taskEntity.getNotes());
        int process = taskEntity.getTaskstatus();
        String processName;
        switch (process) {
            case TaskState.NOT_STARTED:
                processName = "未开始";
                break;
            case TaskState.RUN:
                processName = "进行中";
                break;
            case TaskState.DONE:
                processName = "待验收";
                break;
            case TaskState.GRID_CONFIRMED:
                processName = "验收通过";
                break;
            case TaskState.GRID_NOT_PASS:
                processName = "验收未通过";
                break;
            default:
                processName = "未知状态";
                break;
        }

        viewHolder.setText(R.id.tv_state, processName);

        viewHolder.setOnClickListener(R.id.btn_pass, (view) -> {
            if (process == TaskState.DONE && confirmButtonListener != null) {
                confirmButtonListener.onPassButtonClick(taskEntity);
            }
        });
        viewHolder.setOnClickListener(R.id.btn_not_pass, (view) -> {
            if (process == TaskState.DONE && confirmButtonListener != null)
                confirmButtonListener.onNotPassButtonClick(taskEntity);
        });

        // 设置底色
        switch (process) {
            case TaskState.NOT_STARTED:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.basic_red3);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.basic_red4);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
            case TaskState.RUN:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.basic_green1);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.basic_green2);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
            case TaskState.DONE:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.basic_gray8);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.basic_blue1);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.basic_btn_yellow);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.basic_btn_yellow);
                break;
            case TaskState.GRID_NOT_PASS:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.basic_yellow3);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.basic_yellow4);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
        }
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }

        // 操作中(接口调用中)按钮不可用
        if (taskEntity.isInOperation()) {
            viewHolder.getView(R.id.btn_pass).setEnabled(false);
            viewHolder.getView(R.id.btn_not_pass).setEnabled(false);
        } else {
            viewHolder.getView(R.id.btn_pass).setEnabled(true);
            viewHolder.getView(R.id.btn_not_pass).setEnabled(true);
        }
    }

    public void setConfirmButtonListener(ConfirmButtonListener listener) {
        this.confirmButtonListener = listener;
    }
}
