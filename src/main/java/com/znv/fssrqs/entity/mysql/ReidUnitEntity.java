package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReidUnitEntity {
    @JsonProperty("DeviceID")
    private String DeviceID;

    @JsonProperty("DeviceName")
    private String DeviceName;

    @JsonProperty("DeviceType")
    private Integer DeviceType;

    @JsonProperty("ServiceIP")
    private String IP;

    @JsonProperty("HttpPort")
    private Integer Port;

    @JsonProperty("ManufactureID")
    private String ManufactureID;

    @JsonProperty("Capacity")
    private Integer Capacity;

    @JsonProperty("Version")
    private String Version;

    @JsonProperty("LoginState")
    private Integer LoginState;
}
