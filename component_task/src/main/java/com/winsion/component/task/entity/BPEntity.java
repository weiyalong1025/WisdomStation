package com.winsion.component.task.entity;

/**
 * Created by 10295 on 2017/12/16 0016.
 * 蓝牙点实体
 */

public class BPEntity {
    private String bluetoothId; // 蓝牙点ID
    private long lastTime;  // 最后一次接到蓝牙信号的时间

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
