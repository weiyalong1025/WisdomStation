package com.winsion.dispatch.modules.operation.entity;

import java.io.Serializable;

/**
 * Created by wyl on 2017/7/18
 */
public class RunEntity implements Serializable {
    /**
     * 编号
     */
    private String runsid;

    /**
     * 车次
     */
    private String trainnumber;

    /**
     * 运行区域名称
     */
    private String runareaname;

    /**
     * 运行区域类型
     */
    private String areatypename;

    /**
     * 类型编号
     */
    private String areatypeno;

    public String getRunsid() {
        return runsid;
    }

    public void setRunsid(String runsid) {
        this.runsid = runsid;
    }

    public String getTrainnumber() {
        return trainnumber;
    }

    public void setTrainnumber(String trainnumber) {
        this.trainnumber = trainnumber;
    }

    public String getRunareaname() {
        return runareaname;
    }

    public void setRunareaname(String runareaname) {
        this.runareaname = runareaname;
    }

    public String getAreatypename() {
        return areatypename;
    }

    public void setAreatypename(String areatypename) {
        this.areatypename = areatypename;
    }

    public String getAreatypeno() {
        return areatypeno;
    }

    public void setAreatypeno(String areatypeno) {
        this.areatypeno = areatypeno;
    }
}
