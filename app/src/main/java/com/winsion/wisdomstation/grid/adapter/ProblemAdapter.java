package com.winsion.wisdomstation.grid.adapter;

import android.content.Context;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.operation.constants.TaskState;
import com.winsion.wisdomstation.operation.entity.TaskEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 */

public class ProblemAdapter extends CommonAdapter<TaskEntity> {
    private OnButtonClickListener passButtonListener;
    private OnButtonClickListener notPassButtonListener;

    public interface OnButtonClickListener {
        void onButtonClick(TaskEntity taskEntity);
    }

    public ProblemAdapter(Context context, List<TaskEntity> data) {
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
            if (process == TaskState.DONE && passButtonListener != null)
                passButtonListener.onButtonClick(taskEntity);
        });
        viewHolder.setOnClickListener(R.id.btn_not_pass, (view) -> {
            if (process == TaskState.DONE && notPassButtonListener != null)
                notPassButtonListener.onButtonClick(taskEntity);
        });

        // 设置底色
        switch (process) {
            case TaskState.NOT_STARTED:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.red3);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.red4);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
            case TaskState.RUN:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.green1);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.green2);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
            case TaskState.DONE:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.gray8);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.blue1);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_yellow);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_yellow);
                break;
            case TaskState.GRID_NOT_PASS:
                viewHolder.setBackgroundRes(R.id.ll_bg, R.color.yellow3);
                viewHolder.setTextColorRes(R.id.tv_state, R.color.yellow4);
                viewHolder.setBackgroundRes(R.id.btn_pass, R.drawable.btn_gray);
                viewHolder.setBackgroundRes(R.id.btn_not_pass, R.drawable.btn_gray);
                break;
        }
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }
    }

    public void setOnPassClickListener(OnButtonClickListener listener) {
        this.passButtonListener = listener;
    }

    public void setOnNotPassClickListener(OnButtonClickListener listener) {
        this.notPassButtonListener = listener;
    }
}
