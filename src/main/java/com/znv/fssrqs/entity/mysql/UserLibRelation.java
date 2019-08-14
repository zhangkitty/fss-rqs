package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dongzelong on  2019/6/4 14:42.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLibRelation {
    private Integer UserGroupID;
    private Integer PersonLibID;
    private String PersonLibType;
    private String LibAlarmLevel;
    private String EventID;
    private String LibName;
    private Integer CapacityType;
    private String CreatorID;
    private String PowerIDs;
    private String HkLibID;

    @JSONField(name = "Control")
    @JsonProperty("Control")
    private boolean Control;

    public void setControl(boolean control) {
        Control = control;
    }

    public Integer getUserGroupID() {
        return UserGroupID;
    }

    public void setUserGroupID(Integer userGroupID) {
        UserGroupID = userGroupID;
    }

    public Integer getPersonLibID() {
        return PersonLibID;
    }

    public void setPersonLibID(Integer personLibID) {
        PersonLibID = personLibID;
    }

    public String getPersonLibType() {
        return PersonLibType;
    }

    public void setPersonLibType(String personLibType) {
        PersonLibType = personLibType;
    }

    public String getLibAlarmLevel() {
        return LibAlarmLevel;
    }

    public void setLibAlarmLevel(String libAlarmLevel) {
        LibAlarmLevel = libAlarmLevel;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String eventID) {
        EventID = eventID;
    }

    public String getLibName() {
        return LibName;
    }

    public void setLibName(String libName) {
        LibName = libName;
    }

    public Integer getCapacityType() {
        return CapacityType;
    }

    public void setCapacityType(Integer capacityType) {
        CapacityType = capacityType;
    }

    public String getCreatorID() {
        return CreatorID;
    }

    public void setCreatorID(String creatorID) {
        CreatorID = creatorID;
    }

    public String getPowerIDs() {
        return PowerIDs;
    }

    public void setPowerIDs(String powerIDs) {
        PowerIDs = powerIDs;
    }

    public String getHkLibID() {
        return HkLibID;
    }

    public void setHkLibID(String hkLibID) {
        HkLibID = hkLibID;
    }

    public boolean isControl() {
        return Control;
    }
}
