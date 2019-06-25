/**
 * <pre>
 * 标  题: SearchByTrailParam.java.
 * 版权所有: 版权所有(C)2001-2019
 * 公   司: 深圳中兴力维技术有限公司
 * 内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明
 * 其他说明: // 其它内容的说明
 * 完成日期: 2019-6-11 // 输入完成日期
 * </pre>
 * <pre>
 * 修改记录1:
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 *
 * @version 1.0
 * @author Konghaifei
 */
package com.znv.fssrqs.service.elasticsearch.trailsearch;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class SearchByTrailParam {
    @JSONField(name = "enter_time_start")
    private String enterTimeStart;
    @JSONField(name = "enter_time_end")
    private String enterTimeEnd;
    @JSONField(name = "office_id")
    private List<String> officeId;
    @JSONField(name = "camera_id")
    private List<String> cameraId;
    @JSONField(name = "feature_value")
    private List<String> featureValue;
    @JSONField(name = "sim_threshold")
    private double simThreshold;
    @JSONField(name = "filter_type")
    private String filterType;
    @JSONField(name = "is_lopq")
    private boolean isLopq;

    @JSONField(name = "sort_field")
    private String sortField;
    @JSONField(name = "sort_order")
    private String sortOrder;
    private int from;
    private int size;
    @JSONField(name = "coarse_code_num")
    private int coarseCodeNum;

    public String getEnterTimeStart() {
        return enterTimeStart;
    }

    public void setEnterTimeStart(String enterTimeStart) {
        this.enterTimeStart = enterTimeStart;
    }

    public String getEnterTimeEnd() {
        return enterTimeEnd;
    }

    public void setEnterTimeEnd(String enterTimeEnd) {
        this.enterTimeEnd = enterTimeEnd;
    }

    public List<String> getOfficeId() {
        return officeId;
    }

    public void setOfficeId(List<String> officeId) {
        this.officeId = officeId;
    }

    public List<String> getCameraId() {
        return cameraId;
    }

    public void setCameraId(List<String> cameraId) {
        this.cameraId = cameraId;
    }

    public List<String> getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(List<String> featureValue) {
        this.featureValue = featureValue;
    }

    public double getSimThreshold() {
        return simThreshold;
    }

    public void setSimThreshold(double simThreshold) {
        this.simThreshold = simThreshold;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public boolean isLopq() {
        return isLopq;
    }

    public void setLopq(boolean lopq) {
        isLopq = lopq;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCoarseCodeNum() {
        return coarseCodeNum;
    }

    public void setCoarseCodeNum(int coarseCodeNum) {
        this.coarseCodeNum = coarseCodeNum;
    }
}
