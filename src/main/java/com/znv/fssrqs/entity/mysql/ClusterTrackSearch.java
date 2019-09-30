package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;


public class ClusterTrackSearch {
    @JSONField(name = "StartTime")
    @JsonProperty("StartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date StartTime;
    @JSONField(name = "EndTime")
    @JsonProperty("EndTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date EndTime;
    @JSONField(name = "FusedID")
    @JsonProperty("FusedID")
    private List<String> FusedID;

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public List<String> getFusedID() {
        return FusedID;
    }

    public void setFusedID(List<String> fusedID) {
        FusedID = fusedID;
    }
}
