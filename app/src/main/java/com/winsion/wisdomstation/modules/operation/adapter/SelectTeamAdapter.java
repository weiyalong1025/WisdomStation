package com.winsion.wisdomstation.modules.operation.adapter;

import android.content.Context;
import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.modules.operation.entity.TeamEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/1/8.
 * 发送命令/协作中选择班组界面的Adapter
 */

public class SelectTeamAdapter extends CommonAdapter<TeamEntity> {
    private ArrayList<TeamEntity> selectedList = new ArrayList<>();

    public SelectTeamAdapter(Context context, List<TeamEntity> data) {
        super(context, R.layout.item_select_team, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TeamEntity teamEntity, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.view_divider, false);
        } else {
            viewHolder.setVisible(R.id.view_divider, true);
        }

        viewHolder.setOnClickListener(R.id.ll_list_item, (View v) -> {
            if (selectedList.contains(teamEntity)) {
                selectedList.remove(teamEntity);
                viewHolder.setImageResource(R.id.iv_is_select, R.drawable.ic_check_box_outline);
            } else {
                selectedList.add(teamEntity);
                viewHolder.setImageResource(R.id.iv_is_select, R.drawable.ic_check_box);
            }
        });

        viewHolder.setText(R.id.tv_performer_name, teamEntity.getTeamsName());
    }

    public ArrayList<TeamEntity> getSelectedList() {
        return selectedList;
    }
}
