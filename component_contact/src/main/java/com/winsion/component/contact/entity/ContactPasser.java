package com.winsion.component.contact.entity;

public class ContactPasser extends ContactEntity {
    private int conType;
    private String conName;
    private String conId;
    private String conPhotoUrl;
    private String conMmpId;
    private String conLoginState;

    public void setConType(int conType) {
        this.conType = conType;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public void setConPhotoUrl(String conPhotoUrl) {
        this.conPhotoUrl = conPhotoUrl;
    }

    public void setConMmpId(String conMmpId) {
        this.conMmpId = conMmpId;
    }

    public void setConLoginState(String conLoginState) {
        this.conLoginState = conLoginState;
    }

    @Override
    public int getConType() {
        return conType;
    }

    @Override
    public String getConName() {
        return conName;
    }

    @Override
    public String getConId() {
        return conId;
    }

    @Override
    public String getConPhotoUrl() {
        return conPhotoUrl;
    }

    @Override
    public String getConMmpId() {
        return conMmpId;
    }

    @Override
    public String getConLoginState() {
        return conLoginState;
    }
}
