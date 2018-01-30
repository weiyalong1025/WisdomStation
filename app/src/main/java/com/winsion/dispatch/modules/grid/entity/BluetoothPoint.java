package com.winsion.dispatch.modules.grid.entity;

/**
 * Created by 10295 on 2017/12/16 0016.
 */

public class BluetoothPoint {
    // 蓝牙点ID
    private String bluetoothId;
    // 最后一次接到蓝牙信号的时间
    private long lastTime;

    public String getBluetoothId() {
        return bluetoothId;
    }

    public void setBluetoothId(String bluetoothId) {
        this.bluetoothId = bluetoothId;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
