package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by dongzelong on  2019/6/3 10:41.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device {
    @JSONField(name = "ApeID")
    @JsonProperty("ApeID")
    private String ApeID;
    @JSONField(name = "PrecinctID")
    @JsonProperty("PrecinctID")
    private String PrecinctID;
    @JSONField(name = "StationID")
    @JsonProperty("StationID")
    private String StationID;
    //设备名称
    @JSONField(name = "Name")
    @JsonProperty("Name")
    private String Name;
    @JSONField(name = "ApeKind")
    @JsonProperty("ApeKind")
    private Integer ApeKind;
    @JSONField(name = "DeviceType")
    @JsonProperty("DeviceType")
    private Integer DeviceType;
    @JSONField(name = "ManufactureID")
    @JsonProperty("ManufactureID")
    private Integer ManufactureID;
    @JSONField(name = "ModelID")
    @JsonProperty("ModelID")
    private Integer ModelID;
    @JSONField(name = "LaunchTime")
    @JsonProperty("LaunchTime")
    private Date LaunchTime;
    @JSONField(name = "UserDeviceID")
    @JsonProperty("UserDeviceID")
    private String UserDeviceID;
    @JSONField(name = "DeviceIndex")
    @JsonProperty("DeviceIndex")
    private Integer DeviceIndex;
    @JSONField(name = "DeviceUseState")
    @JsonProperty("DeviceUseState")
    private Integer DeviceUseState;
    @JSONField(name = "DeviceModel")
    @JsonProperty("DeviceModel")
    private String DeviceModel;
    @JSONField(name = "PurchaseTime")
    @JsonProperty("PurchaseTime")
    private Date PurchaseTime;
    @JSONField(name = "UseTime")
    @JsonProperty("UseTime")
    private Date UseTime;
    @JSONField(name = "UseYears")
    @JsonProperty("UseYears")
    private Integer UseYears;
    @JSONField(name = "UpdateTime")
    @JsonProperty("UpdateTime")
    private Date UpdateTime;
    @JSONField(name = "InstallSite")
    @JsonProperty("InstallSite")
    private String InstallSite;
    @JSONField(name = "DevicePrincipal")
    @JsonProperty("DevicePrincipal")
    private String DevicePrincipal;
    @JSONField(name = "PrincipalTel")
    @JsonProperty("PrincipalTel")
    private String PrincipalTel;
    @JSONField(name = "PrincipalEmail")
    @JsonProperty("PrincipalEmail")
    private String PrincipalEmail;
    @JSONField(name = "PreDel")
    @JsonProperty("PreDel")
    private Integer PreDel;

    @JSONField(name = "X")
    @JsonProperty("X")
    private Integer X;
    @JSONField(name = "Y")
    @JsonProperty("Y")
    private Integer Y;

    @JsonIgnore
    public String getApeID() {
        return ApeID;
    }

    public void setApeID(String apeID) {
        ApeID = apeID;
    }

    @JsonIgnore
    public String getPrecinctID() {
        return PrecinctID;
    }

    public void setPrecinctID(String precinctID) {
        PrecinctID = precinctID;
    }

    @JsonIgnore
    public String getStationID() {
        return StationID;
    }

    public void setStationID(String stationID) {
        StationID = stationID;
    }
    @JsonIgnore
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @JsonIgnore
    public Integer getApeKind() {
        return ApeKind;
    }

    public void setApeKind(Integer apeKind) {
        ApeKind = apeKind;
    }

    @JsonIgnore
    public Integer getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(Integer deviceType) {
        DeviceType = deviceType;
    }

    @JsonIgnore
    public Integer getManufactureID() {
        return ManufactureID;
    }

    public void setManufactureID(Integer manufactureID) {
        ManufactureID = manufactureID;
    }

    @JsonIgnore
    public Integer getModelID() {
        return ModelID;
    }

    public void setModelID(Integer modelID) {
        ModelID = modelID;
    }

    @JsonIgnore
    public Date getLaunchTime() {
        return LaunchTime;
    }

    public void setLaunchTime(Date launchTime) {
        LaunchTime = launchTime;
    }

    @JsonIgnore
    public String getUserDeviceID() {
        return UserDeviceID;
    }

    public void setUserDeviceID(String userDeviceID) {
        UserDeviceID = userDeviceID;
    }

    @JsonIgnore
    public Integer getDeviceIndex() {
        return DeviceIndex;
    }

    public void setDeviceIndex(Integer deviceIndex) {
        DeviceIndex = deviceIndex;
    }

    @JsonIgnore
    public Integer getDeviceUseState() {
        return DeviceUseState;
    }

    public void setDeviceUseState(Integer deviceUseState) {
        DeviceUseState = deviceUseState;
    }

    @JsonIgnore
    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    @JsonIgnore
    public Date getPurchaseTime() {
        return PurchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        PurchaseTime = purchaseTime;
    }

    @JsonIgnore
    public Date getUseTime() {
        return UseTime;
    }

    public void setUseTime(Date useTime) {
        UseTime = useTime;
    }

    @JsonIgnore
    public Integer getUseYears() {
        return UseYears;
    }

    public void setUseYears(Integer useYears) {
        UseYears = useYears;
    }

    @JsonIgnore
    public Date getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
    }

    @JsonIgnore
    public String getInstallSite() {
        return InstallSite;
    }

    public void setInstallSite(String installSite) {
        InstallSite = installSite;
    }

    @JsonIgnore
    public String getDevicePrincipal() {
        return DevicePrincipal;
    }

    public void setDevicePrincipal(String devicePrincipal) {
        DevicePrincipal = devicePrincipal;
    }

    @JsonIgnore
    public String getPrincipalTel() {
        return PrincipalTel;
    }

    public void setPrincipalTel(String principalTel) {
        PrincipalTel = principalTel;
    }

    @JsonIgnore
    public String getPrincipalEmail() {
        return PrincipalEmail;
    }

    public void setPrincipalEmail(String principalEmail) {
        PrincipalEmail = principalEmail;
    }

    @JsonIgnore
    public Integer getPreDel() {
        return PreDel;
    }

    public void setPreDel(Integer preDel) {
        PreDel = preDel;
    }

    @JsonIgnore
    public Integer getX() {
        return X;
    }

    public void setX(Integer x) {
        X = x;
    }

    @JsonIgnore
    public Integer getY() {
        return Y;
    }

    public void setY(Integer y) {
        Y = y;
    }
}
