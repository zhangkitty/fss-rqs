package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InfoManufactureEntity {
    @JsonProperty("ManufactureID")
    private Integer ManufactureID;

    @JsonProperty("ManufactureName")
    private String ManufactureName;
}
