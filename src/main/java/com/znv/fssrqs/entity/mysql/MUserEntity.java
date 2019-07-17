package com.znv.fssrqs.entity.mysql;

import lombok.Data;

@Data
public class MUserEntity {
    private String UserID;


    private String SessionId;
    private String UserName;
    private String Password;
    private String PrecinctId;
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
}
