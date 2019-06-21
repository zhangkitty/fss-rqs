package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

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
    private String ApeID;
    private String PowerIDs;
    private Integer UserGroupID;
    private String ApeKinds;

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
}
