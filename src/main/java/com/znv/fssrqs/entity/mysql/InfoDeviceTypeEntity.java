package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InfoDeviceTypeEntity {
    @JsonProperty("DeviceTypeID")
    private Integer DeviceTypeID;

    @JsonProperty("DeviceTypeName")
    private String DeviceTypeName;
}
