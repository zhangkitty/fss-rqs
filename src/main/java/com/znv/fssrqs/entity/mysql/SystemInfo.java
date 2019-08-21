package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemInfo {
    private Integer ID;
    private Integer DefaultFlag;
    private String Version;
    private Boolean HideImage;
    private String DefaultName;
    private String CurrentName;
    private String DefaultImage;
    private String CurrentImage;
    private String DefaultImageName;
    private String CurrentImageName;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getDefaultFlag() {
        return DefaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        DefaultFlag = defaultFlag;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public Boolean getHideImage() {
        return HideImage;
    }

    public void setHideImage(Boolean hideImage) {
        HideImage = hideImage;
    }

    public String getDefaultName() {
        return DefaultName;
    }

    public void setDefaultName(String defaultName) {
        DefaultName = defaultName;
    }

    public String getCurrentName() {
        return CurrentName;
    }

    public void setCurrentName(String currentName) {
        CurrentName = currentName;
    }

    public String getDefaultImage() {
        return DefaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        DefaultImage = defaultImage;
    }

    public String getCurrentImage() {
        return CurrentImage;
    }

    public void setCurrentImage(String currentImage) {
        CurrentImage = currentImage;
    }

    public String getDefaultImageName() {
        return DefaultImageName;
    }

    public void setDefaultImageName(String defaultImageName) {
        DefaultImageName = defaultImageName;
    }

    public String getCurrentImageName() {
        return CurrentImageName;
    }

    public void setCurrentImageName(String currentImageName) {
        CurrentImageName = currentImageName;
    }
}