package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dongzelong on  2019/6/3 10:37.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupDeviceRelation extends Device {
    @JSONField(name = "ApeID")
    @JsonProperty("ApeID")
    private String ApeID;
    @JSONField(name = "PowerIDs")
    @JsonProperty("PowerIDs")
    private String PowerIDs;
    @JSONField(name = "UserGroupID")
    @JsonProperty("UserGroupID")
    private Integer UserGroupID;
    @JSONField(name = "ApeKinds")
    @JsonProperty("ApeKinds")
    private String ApeKinds;
    private Integer UseType;

    @Override
    public String getApeID() {
        return ApeID;
    }

    @Override
    public void setApeID(String apeID) {
        ApeID = apeID;
    }

    public String getPowerIDs() {
        return PowerIDs;
    }

    public void setPowerIDs(String powerIDs) {
        PowerIDs = powerIDs;
    }

    public Integer getUserGroupID() {
        return UserGroupID;
    }

    public void setUserGroupID(Integer userGroupID) {
        UserGroupID = userGroupID;
    }

    public String getApeKinds() {
        return ApeKinds;
    }

    public void setApeKinds(String apeKinds) {
        ApeKinds = apeKinds;
    }

    public Integer getUseType() {
        return UseType;
    }

    public void setUseType(Integer useType) {
        UseType = useType;
    }
}
