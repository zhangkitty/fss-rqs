package com.znv.fssrqs.service.control.device.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CameraUnDeployDTO {

    private List<String> ids;

    private List<String> CameraIds;
    @NotNull(message = "库ID不能为空")
    private String LibId;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getCameraIds() {
        return CameraIds;
    }

    public void setCameraIds(List<String> cameraIds) {
        CameraIds = cameraIds;
    }

    public String getLibId() {
        return LibId;
    }

    public void setLibId(String libId) {
        LibId = libId;
    }
}
