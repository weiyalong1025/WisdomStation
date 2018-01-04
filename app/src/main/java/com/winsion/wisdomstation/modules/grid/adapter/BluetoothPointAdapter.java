package com.winsion.wisdomstation.modules.grid.adapter;

import android.content.Context;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.modules.grid.entity.BluetoothPoint;
import com.winsion.wisdomstation.utils.ConvertUtils;
import com.winsion.wisdomstation.utils.constants.Formatter;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 */

public class BluetoothPointAdapter extends CommonAdapter<BluetoothPoint> {

    public BluetoothPointAdapter(Context context, List<BluetoothPoint> data) {
        super(context, R.layout.item_bluetooth_info, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, BluetoothPoint bluetoothPoint, int position) {
        viewHolder.setText(R.id.tv_bluetooth_address, bluetoothPoint.getBluetoothId());
        String lastTime = ConvertUtils.formatDate(bluetoothPoint.getLastTime(), Formatter.DATE_FORMAT10);
        viewHolder.setText(R.id.tv_last_time, lastTime);
    }
}
