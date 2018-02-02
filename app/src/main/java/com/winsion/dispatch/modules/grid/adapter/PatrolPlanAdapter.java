package com.winsion.dispatch.modules.grid.adapter;

import android.content.Context;

import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.grid.entity.PatrolPlanEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 * 巡检计划Adapter
 */

public class PatrolPlanAdapter extends CommonAdapter<PatrolPlanEntity> {
    public PatrolPlanAdapter(Context context, List<PatrolPlanEntity> data) {
        super(context, R.layout.item_patrol_plan, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, PatrolPlanEntity patrolPlanEntity, int position) {
        viewHolder.setText(R.id.tv_location, patrolPlanEntity.getPointname());
        String planStartTime = ConvertUtils.splitToHM(patrolPlanEntity.getPlanstarttime());
        viewHolder.setText(R.id.tv_plan_start_time, planStartTime);
        String planEndTime = ConvertUtils.splitToHM(patrolPlanEntity.getPlanendtime());
        viewHolder.setText(R.id.tv_plan_end_time, planEndTime);
        viewHolder.setText(R.id.tv_finish_count, String.valueOf(patrolPlanEntity.getFinishcount()));
        viewHolder.setText(R.id.tv_total_count, String.valueOf(patrolPlanEntity.getItemscount()));
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom_split, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom_split, false);
        }

        if (patrolPlanEntity.isArrive()) {
            viewHolder.setTextColorRes(R.id.tv_location, R.color.blue1);
            viewHolder.setTextColorRes(R.id.tv_plan_start_time, R.color.blue1);
            viewHolder.setTextColorRes(R.id.tv_plan_end_time, R.color.yellow2);
            viewHolder.setTextColorRes(R.id.tv_finish_count, R.color.white1);
            viewHolder.setTextColorRes(R.id.tv_total_count, R.color.blue1);
            viewHolder.setTextColorRes(R.id.tv_plan_start, R.color.gray2);
            viewHolder.setTextColorRes(R.id.tv_plan_end, R.color.gray2);
            viewHolder.setTextColorRes(R.id.tv_split_line, R.color.gray2);
        } else {
            viewHolder.setTextColorRes(R.id.tv_location, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_plan_start_time, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_plan_end_time, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_finish_count, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_total_count, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_plan_start, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_plan_end, R.color.gray6);
            viewHolder.setTextColorRes(R.id.tv_split_line, R.color.gray6);
        }
    }
}
