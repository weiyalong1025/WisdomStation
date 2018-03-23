package com.winsion.component.task.adapter;

import android.content.Context;

import com.winsion.component.basic.utils.ConvertUtils;
import com.winsion.component.basic.constants.Formatter;
import com.winsion.component.task.R;
import com.winsion.component.task.entity.BPEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 * 附近蓝牙点列表Adapter
 */

public class BluetoothPointAdapter extends CommonAdapter<BPEntity> {

    public BluetoothPointAdapter(Context context, List<BPEntity> data) {
        super(context, R.layout.task_item_bluetooth_info, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, BPEntity BPEntity, int position) {
        viewHolder.setText(R.id.tv_bluetooth_address, BPEntity.getBluetoothId());
        String lastTime = ConvertUtils.formatDate(BPEntity.getLastTime(), Formatter.DATE_FORMAT10);
        viewHolder.setText(R.id.tv_last_time, lastTime);
    }
}
