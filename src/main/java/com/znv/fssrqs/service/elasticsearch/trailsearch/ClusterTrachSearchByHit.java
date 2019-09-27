package com.znv.fssrqs.service.elasticsearch.trailsearch;

import com.alibaba.fastjson.annotation.JSONField;

public class ClusterTrachSearchByHit {
    private String cameraId;
    private String enterTime;
    private String leaveTime;
    private String gpsx;
    private String gpsy;
    private int stayNum;
    private double score;
    private String personId;
    private int libId;
    private String smallPictureUrl;
    private String bigPictureUuid;
    private String uuid;
    private int imageNo;


    @JSONField(name = "CameraID")
    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    @JSONField(name = "EnterTime")
    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    @JSONField(name = "LeaveTime")
    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    @JSONField(name = "GpsX")
    public String getGpsx() {
        return gpsx;
    }

    public void setGpsx(String gpsx) {
        this.gpsx = gpsx;
    }

    @JSONField(name = "GpsY")
    public String getGpsy() {
        return gpsy;
    }

    public void setGpsy(String gpsy) {
        this.gpsy = gpsy;
    }

    @JSONField(name = "StayNum")
    public int getStayNum() {
        return stayNum;
    }

    public void setStayNum(int stayNum) {
        this.stayNum = stayNum;
    }

    @JSONField(name = "Score")
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @JSONField(name = "PersonID")
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JSONField(name = "LibID")
    public int getLibId() {
        return libId;
    }

    public void setLibId(int libId) {
        this.libId = libId;
    }

    @JSONField(name = "SmallPictureUrl")
    public String getSmallPictureUrl() {
        return smallPictureUrl;
    }

    public void setSmallPictureUrl(String smallPictureUrl) {
        this.smallPictureUrl = smallPictureUrl;
    }

    @JSONField(name = "BigPictureUuid")
    public String getBigPictureUuid() {
        return bigPictureUuid;
    }

    public void setBigPictureUuid(String bigPictureUuid) {
        this.bigPictureUuid = bigPictureUuid;
    }

    @JSONField(name = "Uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }



    @JSONField(name = "ImageNo")
    public int getImageNo() {
        return imageNo;
    }

    public void setImageNo(int imageNo) {
        this.imageNo = imageNo;
    }
}
