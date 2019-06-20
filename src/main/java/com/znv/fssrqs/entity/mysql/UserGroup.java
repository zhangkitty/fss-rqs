package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by dongzelong on  2019/6/3 10:08.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroup {
    private Integer UserGroupID;
    private Integer UpUserGroupID;
    private String UserGroupName;
    private String UserGroupDesc;
    private Date CreateTime;
    private String CreateUserID;
    private Integer RoleID;
    private String UserID;

    public Integer getUserGroupID() {
        return UserGroupID;
    }

    public void setUserGroupID(Integer userGroupID) {
        UserGroupID = userGroupID;
    }

    public Integer getUpUserGroupID() {
        return UpUserGroupID;
    }

    public void setUpUserGroupID(Integer upUserGroupID) {
        UpUserGroupID = upUserGroupID;
    }

    public String getUserGroupName() {
        return UserGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        UserGroupName = userGroupName;
    }

    public String getUserGroupDesc() {
        return UserGroupDesc;
    }

    public void setUserGroupDesc(String userGroupDesc) {
        UserGroupDesc = userGroupDesc;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public String getCreateUserID() {
        return CreateUserID;
    }

    public void setCreateUserID(String createUserID) {
        CreateUserID = createUserID;
    }

    public Integer getRoleID() {
        return RoleID;
    }

    public void setRoleID(Integer roleID) {
        RoleID = roleID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
