package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by dongzelong on  2019/6/4 14:12.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonLib {
    private Integer LibID;
    private String PersonLibType;
    private String LibName;
    private String LibAlarmLevel;
    private String Description;
    private String CreatorID;
    private String EventID;
    private String RegionID;
    private Integer CapacityType;
    private String HkLibID;
    private Date CreateTime;
    private Date ModifyTime;

    @JSONField(name = "Control")
    @JsonProperty("Control")
    private boolean Control;

    public Integer getLibID() {
        return LibID;
    }

    public void setLibID(Integer libID) {
        LibID = libID;
    }

    public String getPersonLibType() {
        return PersonLibType;
    }

    public void setPersonLibType(String personLibType) {
        PersonLibType = personLibType;
    }

    public String getLibName() {
        return LibName;
    }

    public void setLibName(String libName) {
        LibName = libName;
    }

    public String getLibAlarmLevel() {
        return LibAlarmLevel;
    }

    public void setLibAlarmLevel(String libAlarmLevel) {
        LibAlarmLevel = libAlarmLevel;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatorID() {
        return CreatorID;
    }

    public void setCreatorID(String creatorID) {
        CreatorID = creatorID;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String eventID) {
        EventID = eventID;
    }

    public String getRegionID() {
        return RegionID;
    }

    public void setRegionID(String regionID) {
        RegionID = regionID;
    }

    public Integer getCapacityType() {
        return CapacityType;
    }

    public void setCapacityType(Integer capacityType) {
        CapacityType = capacityType;
    }

    public String getHkLibID() {
        return HkLibID;
    }

    public void setHkLibID(String hkLibID) {
        HkLibID = hkLibID;
    }

    public void setControl(boolean control) {
        Control = control;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public Date getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        ModifyTime = modifyTime;
    }

    public boolean isControl() {
        return Control;
    }
}
