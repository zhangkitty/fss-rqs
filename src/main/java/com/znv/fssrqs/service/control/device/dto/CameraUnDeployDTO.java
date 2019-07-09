package com.znv.fssrqs.service.control.device.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@Data
public class CameraUnDeployDTO {

    private List<String> Ids;

    private List<String> CameraIds;
    @NotNull(message = "库ID不能为空")
    private String LibId;

}
