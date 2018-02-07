package com.winsion.dispatch.modules.grid.entity;

/**
 * Created by wyl on 2017/6/28
 * 提交问题提 - 类别下子类
 */
public class SubclassEntity {
    private String id;  // 子类编号
    private String classificationid;    // 大类编号
    private String typename;    // 类型名称
    private int plancosttime;   // 预计花费时长
    private int priority;   // 优先级

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
