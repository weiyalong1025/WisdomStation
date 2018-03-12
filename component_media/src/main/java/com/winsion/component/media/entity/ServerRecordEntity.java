package com.winsion.component.media.entity;

/**
 * Created by wyl on 2017/6/22
 */
public class ServerRecordEntity {
    private String filepath;
    private String usersid;
    private String joboperatorsid;
    private String type;
    private String jobsid;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUsersid() {
        return usersid;
    }

    public void setUsersid(String usersid) {
        this.usersid = usersid;
    }

    public String getJoboperatorsid() {
        return joboperatorsid;
    }

    public void setJoboperatorsid(String joboperatorsid) {
        this.joboperatorsid = joboperatorsid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJobsid() {
        return jobsid;
    }

    public void setJobsid(String jobsid) {
        this.jobsid = jobsid;
    }
}
