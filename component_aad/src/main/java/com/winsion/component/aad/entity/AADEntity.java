package com.winsion.component.aad.entity;

import java.io.Serializable;

/**
 * Created by w on 2017/10/19.
 * 到发实体
 */

public class AADEntity implements Serializable {

//        "runDate": "2017-10-20 00:00:00.0",
//        "endStation": "北京西",
//        "checkportId": "97ead173-9854-11e7-a5b7-782bcb3332bb,91d3acc3-9854-11e7-a5b7-782bcb3332bb",
//        "departTime": "2017-10-20 06:42:00.0",
//        "realArriveTime": "",
//        "trainStatus": "0",
//        "realDepartTime": "2017-10-20 06:42:00.0",
//        "platformId": "5F06BFF4-C7BE-41AA-AD71-70E56B6957C0",
//        "trainLate": "0",
//        "checkportName": "A3,B2",
//        "runsId": "ab5edee2-b4e6-11e7-9fdf-782bcb3332bb",
//        "arriveTime": "2017-10-22 09:27:58.0",
//        "checkTime": "2017-10-20 06:22:00.0",
//        "startStation": "郑州东",
//        "trainNumber": "G1564",
//        "platformName": "14站台",
//        "waitroomName": "候车层",
//        "direction": ""

    private String runsId;          //车次id
    private String arriveTime;      //计划到达时间
    private String departTime;      //计划发车时间
    private String realArriveTime;  //实际达到时间
    private String realDepartTime;  //实际发车时间
    private int direction;          //上下行方向（0：上行，1：下行，暂时无数据）
    private String trainNumber;     //车次号
    private String runDate;         //运行日期
    private int trainStatus;        //列车状态（0:未检票，1:正在检票）
    private String endStation;      //到达站
    private String startStation;    //始发站
    private int trainLate;          //列车晚点状态（0：正点，1：确认晚点时间，2：不确认晚点时间 3：停运）
    private String waitroomName;    //候车室
    private String checkportName;   //检票口
    private String platformName;    //站台
    private String track;           //股道
    private String checkportId;     //检票口id
    private String platformId;      //站台id

    public String getCheckportId() {
        return checkportId;
    }

    public void setCheckportId(String checkportId) {
        this.checkportId = checkportId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public String getRealDepartTime() {
        return realDepartTime;
    }

    public void setRealDepartTime(String realDepartTime) {
        this.realDepartTime = realDepartTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getRealArriveTime() {
        return realArriveTime;
    }

    public void setRealArriveTime(String realArriveTime) {
        this.realArriveTime = realArriveTime;
    }

    public int getTrainStatus() {
        return trainStatus;
    }

    public void setTrainStatus(int trainStatus) {
        this.trainStatus = trainStatus;
    }

    public int getTrainLate() {
        return trainLate;
    }

    public void setTrainLate(int trainLate) {
        this.trainLate = trainLate;
    }

    public String getCheckportName() {
        return checkportName;
    }

    public void setCheckportName(String checkportName) {
        this.checkportName = checkportName;
    }

    public String getRunsId() {
        return runsId;
    }

    public void setRunsId(String runsId) {
        this.runsId = runsId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getWaitroomName() {
        return waitroomName;
    }

    public void setWaitroomName(String waitroomName) {
        this.waitroomName = waitroomName;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "AADEntity{" +
                "runsId='" + runsId + '\'' +
                ", arriveTime='" + arriveTime + '\'' +
                ", departTime='" + departTime + '\'' +
                ", realArriveTime='" + realArriveTime + '\'' +
                ", realDepartTime='" + realDepartTime + '\'' +
                ", direction=" + direction +
                ", trainNumber='" + trainNumber + '\'' +
                ", runDate='" + runDate + '\'' +
                ", trainStatus=" + trainStatus +
                ", endStation='" + endStation + '\'' +
                ", startStation='" + startStation + '\'' +
                ", trainLate=" + trainLate +
                ", waitroomName='" + waitroomName + '\'' +
                ", checkportName='" + checkportName + '\'' +
                ", platformName='" + platformName + '\'' +
                '}';
    }
}
