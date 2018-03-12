package com.winsion.dispatch.modules.reminder.adapter;

import android.content.Context;
import android.support.annotation.StringRes;

import com.winsion.component.basic.listener.ClickListener;
import com.winsion.component.basic.utils.ToastUtils;
import com.winsion.component.task.constants.TaskType;
import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.reminder.constants.ReadStatus;
import com.winsion.dispatch.modules.reminder.entity.SystemRemindEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:28
 */

public class SystemRemindAdapter extends CommonAdapter<SystemRemindEntity> {

    public interface OnSelectChangeListener {
        void onSelectChange(int selectSize);
    }

    private boolean isMultipleDeleteStatus; // 当前是否是多选删除状态
    private ClickListener<SystemRemindEntity> deleteBtnClickListener;
    private OnSelectChangeListener onSelectChangeListener;
    private List<SystemRemindEntity> selectData = new ArrayList<>();

    public SystemRemindAdapter(Context context, List<SystemRemindEntity> data) {
        super(context, R.layout.item_system_remind, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, SystemRemindEntity systemRemindEntity, int position) {
        int taskType = systemRemindEntity.getTaskType();
        int typeTextResId = R.string.title_operation_remind;
        switch (taskType) {
            case TaskType.COMMAND:
                typeTextResId = R.string.title_command_remind;
                break;
            case TaskType.COOPERATE:
                typeTextResId = R.string.title_cooperation_remind;
                break;
            case TaskType.GRID:
                typeTextResId = R.string.title_grid_remind;
                break;
            case TaskType.PLAN:
                typeTextResId = R.string.title_plan_remind;
                break;
        }
        viewHolder.setText(R.id.tv_remind_type, getString(typeTextResId));
        viewHolder.setText(R.id.tv_remind_content, systemRemindEntity.getVoicecontent());
        String sendTime = systemRemindEntity.getSendtime();
        viewHolder.setText(R.id.tv_remind_date, sendTime.substring(0, sendTime.length() - 2));

        if (systemRemindEntity.getReaded() == 1) {
            viewHolder.setVisible(R.id.iv_red_dot, false);
            viewHolder.setTextColorRes(R.id.tv_remind_type, R.color.basic_gray2);
            viewHolder.setTextColorRes(R.id.tv_remind_content, R.color.basic_gray2);
        } else {
            viewHolder.setVisible(R.id.iv_red_dot, true);
            viewHolder.setTextColorRes(R.id.tv_remind_type, R.color.basic_blue1);
            viewHolder.setTextColorRes(R.id.tv_remind_content, R.color.basic_white1);
        }

        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom, false);
        }

        if (isMultipleDeleteStatus) {
            viewHolder.setVisible(R.id.iv_delete, false);
            viewHolder.setVisible(R.id.iv_select, true);
            if (selectData.contains(systemRemindEntity)) {
                viewHolder.setImageResource(R.id.iv_select, R.drawable.basic_ic_check_box);
            } else {
                viewHolder.setImageResource(R.id.iv_select, R.drawable.basic_ic_check_box_outline);
            }
        } else {
            viewHolder.setVisible(R.id.iv_delete, true);
            viewHolder.setVisible(R.id.iv_select, false);
        }

        viewHolder.setOnClickListener(R.id.iv_delete, v -> {
            if (deleteBtnClickListener != null) {
                deleteBtnClickListener.onClick(systemRemindEntity);
            }
        });

        viewHolder.setOnClickListener(R.id.iv_select, v -> selectOneItem(viewHolder, position));
    }

    public void selectOneItem(ViewHolder viewHolder, int position) {
        SystemRemindEntity systemRemindEntity = mDatas.get(position);
        if (systemRemindEntity.getReaded() == ReadStatus.UNREAD) {
            ToastUtils.showToast(mContext, R.string.toast_only_read_remind_can_be_selected);
        } else {
            if (selectData.contains(systemRemindEntity)) {
                selectData.remove(systemRemindEntity);
                viewHolder.setImageResource(R.id.iv_select, R.drawable.basic_ic_check_box_outline);
            } else {
                selectData.add(systemRemindEntity);
                viewHolder.setImageResource(R.id.iv_select, R.drawable.basic_ic_check_box);
            }
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChange(selectData.size());
            }
        }
    }

    public void selectAll(boolean isSelectAll) {
        selectData.clear();
        if (isSelectAll) {
            for (SystemRemindEntity mData : mDatas) {
                if (mData.getReaded() == ReadStatus.READ) {
                    selectData.add(mData);
                }
            }
        }
        notifyDataSetChanged();
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectChange(selectData.size());
        }
    }

    public void changeStatus(boolean isMultipleDeleteStatus) {
        this.isMultipleDeleteStatus = isMultipleDeleteStatus;
        if (!isMultipleDeleteStatus) {
            selectData.clear();
        }
        notifyDataSetChanged();
    }

    public List<SystemRemindEntity> getSelectData() {
        return selectData;
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        this.onSelectChangeListener = listener;
    }

    public void setDeleteBtnClickListener(ClickListener<SystemRemindEntity> listener) {
        this.deleteBtnClickListener = listener;
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }
}
