package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by dongzelong on  2019/6/3 14:10.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapDeviceLocation {
    private String ApeID;

    private String ApeName;

    private Double Latitude;

    private Double Longitude;
    private Integer UseType;

    public String getApeID() {
        return ApeID;
    }

    public void setApeID(String apeID) {
        ApeID = apeID;
    }

    public String getApeName() {
        return ApeName;
    }

    public void setApeName(String apeName) {
        ApeName = apeName;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Integer getUseType() {
        return UseType;
    }

    public void setUseType(Integer useType) {
        UseType = useType;
    }
}
