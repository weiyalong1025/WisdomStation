package com.winsion.component.task.adapter;

import android.content.Context;

import com.winsion.component.task.R;
import com.winsion.component.task.constants.PatrolItemState;
import com.winsion.component.task.entity.PatrolItemEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/2/1.
 * 巡检项Adapter
 */

public class PatrolItemAdapter extends CommonAdapter<PatrolItemEntity> {
    private Operator operator;

    public interface Operator {
        void onNormalClick(PatrolItemEntity patrolItemEntity);

        void onAbnormalClick(PatrolItemEntity patrolItemEntity);
    }

    public PatrolItemAdapter(Context context, List<PatrolItemEntity> data) {
        super(context, R.layout.task_item_patrol_item, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, PatrolItemEntity patrolItemEntity, int position) {
        viewHolder.setText(R.id.tv_item_name, patrolItemEntity.getItemdescribe());
        switch (patrolItemEntity.getDevicestate()) {
            case PatrolItemState.UNDONE:
                viewHolder.setVisible(R.id.iv_normal, true);
                viewHolder.setVisible(R.id.iv_abnormal, true);
                viewHolder.setVisible(R.id.tv_normal_time, false);
                viewHolder.setVisible(R.id.tv_abnormal_time, false);
                break;
            case PatrolItemState.ABNORMAL:
                viewHolder.setVisible(R.id.iv_normal, false);
                viewHolder.setVisible(R.id.iv_abnormal, true);
                viewHolder.setVisible(R.id.tv_normal_time, false);
                viewHolder.setVisible(R.id.tv_abnormal_time, true);
                viewHolder.setText(R.id.tv_abnormal_time,
                        patrolItemEntity.getPatroltime().split(" ")[1].substring(0, 5));
                break;
            case PatrolItemState.NORMAL:
                viewHolder.setVisible(R.id.iv_normal, true);
                viewHolder.setVisible(R.id.iv_abnormal, false);
                viewHolder.setVisible(R.id.tv_normal_time, true);
                viewHolder.setVisible(R.id.tv_abnormal_time, false);
                viewHolder.setText(R.id.tv_normal_time,
                        patrolItemEntity.getPatroltime().split(" ")[1].substring(0, 5));
                break;
        }

        viewHolder.setOnClickListener(R.id.iv_normal, (v) -> {
            if (operator != null) {
                operator.onNormalClick(patrolItemEntity);
            }
        });
        viewHolder.setOnClickListener(R.id.iv_abnormal, (v) -> {
            if (operator != null) {
                operator.onAbnormalClick(patrolItemEntity);
            }
        });
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
