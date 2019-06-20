package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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
    private String ApeID;
    private String PrecinctID;
    private String StationID;
    //设备名称
    private String Name;
    private Integer ApeKind;
    private Integer DeviceType;
    private Integer ManufactureID;
    private Integer ModelID;
    private Date LaunchTime;
    private String UserDeviceID;
    private Integer DeviceIndex;
    private Integer DeviceUseState;
    private String DeviceModel;
    private Date PurchaseTime;
    private Date UseTime;
    private Integer UseYears;
    private Date UpdateTime;
    private String InstallSite;
    private String DevicePrincipal;

    private String PrincipalTel;

    private String PrincipalEmail;

    private Integer PreDel;

    private Integer X;
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
