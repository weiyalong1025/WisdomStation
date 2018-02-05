package com.winsion.dispatch.modules.grid.entity;

/**
 * Created by wyl on 2017/7/21
 */
public class DeviceEntity {
    /**
     * 类别编号
     */
    private String classificationid;

    /**
     * 设备名称
     */
    private String devicename;

    /**
     * 区域编号
     */
    private String areaid;

    /**
     * 设备序号
     */
    private String deviceno;

    public String getClassificationid() {
        return classificationid;
    }

    public void setClassificationid(String classificationid) {
        this.classificationid = classificationid;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getDeviceno() {
        return deviceno;
    }

    public void setDeviceno(String deviceno) {
        this.deviceno = deviceno;
    }
}
