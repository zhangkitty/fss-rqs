package com.znv.fssrqs.service.control.device.dto;

import java.util.List;

public class CameraUnDeployDTO {
    private List<String> CameraIds;
    private String LibId;

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
