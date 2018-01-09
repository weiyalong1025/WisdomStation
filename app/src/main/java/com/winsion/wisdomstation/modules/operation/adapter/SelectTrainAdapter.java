package com.winsion.wisdomstation.modules.operation.adapter;

import android.content.Context;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.modules.operation.entity.RunEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2018/1/8.
 * 发送命令/协作中选择车次界面的Adapter
 */

public class SelectTrainAdapter extends CommonAdapter<RunEntity> {
    private RunEntity selectTrainEntity;

    public SelectTrainAdapter(Context context, List<RunEntity> data) {
        super(context, R.layout.item_select_train, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, RunEntity runEntity, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.view_divider, false);
        } else {
            viewHolder.setVisible(R.id.view_divider, true);
        }
        viewHolder.setText(R.id.tv_train_number, runEntity.getTrainnumber());
        if (runEntity == selectTrainEntity) {
            viewHolder.setVisible(R.id.iv_select, true);
        } else {
            viewHolder.setVisible(R.id.iv_select, false);
        }
        viewHolder.setOnClickListener(R.id.rl_content, v -> {
            selectTrainEntity = runEntity;
            notifyDataSetChanged();
        });
    }

    public RunEntity getSelectTrainEntity() {
        return selectTrainEntity;
    }
}
