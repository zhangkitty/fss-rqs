package com.znv.fssrqs.entity.mysql;

import java.util.ArrayList;

/**
 * 登录用户
 * @author xkh
 */
public class User {
    private String sessionId;
    private String username;
    private String userId;
    private String password;
    private boolean pwdoutdate;

    private String precinctId;

    private String roleId;

    private String usergroupId;

    private String departmentId;

    private Integer userType;

    private String employeeId;

    private String trueName;

    private String mobileTelephone;

    private String eMail;

    private String telephone;

    private String address;

    private Integer userState;

    private String updateTime;

    private String description;

    private String adminUser;

    private String fax;

    private Integer userSex;

    private Integer userLevel;

    private Integer loginClientType;

    private String dtpwupdatetime;

    private Long userIndex;

    private Integer flowroleId;

    private Integer systemFlag;

    private Integer isShow;

    private String createTime;

    private byte[] userImg;

    public String getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(String precinctId) {
        this.precinctId = precinctId == null ? null : precinctId.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getUsergroupId() {
        return usergroupId;
    }

    public void setUsergroupId(String usergroupId) {
        this.usergroupId = usergroupId == null ? null : usergroupId.trim();
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId == null ? null : departmentId.trim();
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId == null ? null : employeeId.trim();
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName == null ? null : trueName.trim();
    }

    public String getMobileTelephone() {
        return mobileTelephone;
    }

    public void setMobileTelephone(String mobileTelephone) {
        this.mobileTelephone = mobileTelephone == null ? null : mobileTelephone.trim();
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail == null ? null : eMail.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getUserState() {
        return userState;
    }

    public void setUserState(Integer userState) {
        this.userState = userState;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updatetime) {
        this.updateTime = updatetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser == null ? null : adminUser.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getLoginClientType() {
        return loginClientType;
    }

    public void setLoginClientType(Integer loginClientType) {
        this.loginClientType = loginClientType;
    }

    public String getDtpwupdatetime() {
        return dtpwupdatetime;
    }

    public void setDtpwupdatetime(String dtpwupdatetime) {
        this.dtpwupdatetime = dtpwupdatetime;
    }

    public Long getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(Long userIndex) {
        this.userIndex = userIndex;
    }

    public Integer getFlowroleId() {
        return flowroleId;
    }

    public void setFlowroleId(Integer flowroleId) {
        this.flowroleId = flowroleId;
    }

    public Integer getSystemFlag() {
        return systemFlag;
    }

    public void setSystemFlag(Integer systemFlag) {
        this.systemFlag = systemFlag;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public byte[] getUserImg() {
        return userImg;
    }

    public void setUserImg(byte[] userImg) {
        this.userImg = userImg;
    }

    public boolean isPwdoutdate() {
        return pwdoutdate;
    }

    public void setPwdoutdate(boolean pwdoutdate) {
        this.pwdoutdate = pwdoutdate;
    }

    private Precinct precinct;

    private String remoteIp;
    private ArrayList<String> functionlist;

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public Precinct getPrecinct() {
        return precinct;
    }

    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getFunctionlist() {
        return functionlist;
    }

    public void setFunctionlist(ArrayList<String> functionlist) {
        this.functionlist = functionlist;
    }

}
