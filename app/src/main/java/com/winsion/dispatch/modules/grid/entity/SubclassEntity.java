package com.winsion.dispatch.modules.grid.entity;

/**
 * Created by wyl on 2017/6/28
 * 提交问题提 - 类别下子类
 */
public class SubclassEntity {
    /**
     * 子类编号
     */
    private String id;
    /**
     * 大类编号
     */
    private String classificationid;

    /**
     * 类型名称
     */
    private String typename;

    /**
     * 预计花费时长
     */
    private int plancosttime;

    /**
     * 优先级
     */
    private int priority;

    public String getClassificationid() {
        return classificationid;
    }

    public void setClassificationid(String classificationid) {
        this.classificationid = classificationid;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getPlancosttime() {
        return plancosttime;
    }

    public void setPlancosttime(int plancosttime) {
        this.plancosttime = plancosttime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
