package com.znv.fssrqs.service.control.device.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.ibatis.annotations.MapKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CameraControlDTO {

    @NotNull(message = "库ID不能为空")
    private String LibId;
    @NotNull(message = "布控开始时间不能为空")
    private String ControlStartTime;
    @NotNull(message = "布控结束时间不能为空")
    private String ControlEndTime;

    private List<String> CameraIds;

    private String Title;

    @NotNull(message = "布控数限制不能为空")
    private Integer LibCountLimit;

    private String CameraId;

    private String Id;

    public String getCameraId() {
        return CameraId;
    }

    public void setCameraId(String cameraId) {
        CameraId = cameraId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLibId() {

        return LibId;
    }

    public void setLibId(String libId) {
        LibId = libId;
    }

    public String getControlStartTime() {
        return ControlStartTime;
    }

    public void setControlStartTime(String controlStartTime) {
        ControlStartTime = controlStartTime;
    }

    public String getControlEndTime() {
        return ControlEndTime;
    }

    public void setControlEndTime(String controlEndTime) {
        ControlEndTime = controlEndTime;
    }

    public List<String> getCameraIds() {
        return CameraIds;
    }

    public void setCameraIds(List<String> cameraIds) {
        CameraIds = cameraIds;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Integer getLibCountLimit() {
        return LibCountLimit;
    }

    public void setLibCountLimit(Integer libCountLimit) {
        LibCountLimit = libCountLimit;
    }
}
