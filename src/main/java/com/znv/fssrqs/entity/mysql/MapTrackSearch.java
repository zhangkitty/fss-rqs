package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by dongzelong on  2019/6/5 9:18.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class MapTrackSearch {
    @JSONField(name = "StartTime")
    @JsonProperty("StartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date StartTime;
    @JSONField(name = "EndTime")
    @JsonProperty("EndTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date EndTime;
    @JSONField(name = "DeviceIDs")
    @JsonProperty("DeviceIDs")
    private List<String> CameraIDs;
    @JSONField(name = "FeatureValue")
    @JsonProperty("FeatureValue")
    private List<String> Features;
    @JSONField(name = "SimilarityDegree")
    @JsonProperty("SimilarityDegree")
    private Integer Sim;
    @JSONField(name = "FilterType")
    @JsonProperty("FilterType")
    private String FilterType;
    @JSONField(name = "QueryType")
    @JsonProperty("QueryType")
    private Integer QueryType;
    @JSONField(name = "SortOrder")
    @JsonProperty("SortOrder")
    private String SortOrder;

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

    public List<String> getCameraIDs() {
        return CameraIDs;
    }

    public void setCameraIDs(List<String> cameraIDs) {
        CameraIDs = cameraIDs;
    }

    public List<String> getFeatures() {
        return Features;
    }

    public void setFeatures(List<String> features) {
        Features = features;
    }

    public Integer getSim() {
        return Sim;
    }

    public void setSim(Integer sim) {
        Sim = sim;
    }

    public String getFilterType() {
        return FilterType;
    }

    public void setFilterType(String filterType) {
        FilterType = filterType;
    }

    public Integer getQueryType() {
        return QueryType;
    }

    public void setQueryType(Integer queryType) {
        QueryType = queryType;
    }

    public String getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(String sortOrder) {
        SortOrder = sortOrder;
    }
}
