package com.znv.fssrqs.param.echarts;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created by ZNV on 2018/12/16.
 */

@Data
public class PersonListGroupQueryParam {

    @JSONField(name = "personlib_type")
    private List<Integer> personLibType;
    @JSONField(name = "lib_id")
    private List<Integer> libId;
    @JSONField(name = "is_del")
    private String isDel = "0";
    @JSONField(name = "addr")
    private List<String> addr;


    @JSONField(name = "age_group")
    private boolean ageGroup = true ;
    @JSONField(name = "sex_group")
    private boolean sexGroup = true;
    @JSONField(name = "flag_group")
    private boolean flagGroup = true;
    @JSONField(name = "time_group")
    private boolean timeGroup = true;

    public void setPersonLibType(List<Integer> personLibType) {
        this.personLibType = personLibType;
    }
    public List<Integer> getPersonLibType() {
        return personLibType;
    }

    public void setLibId(List<Integer> libId) {
        this.libId = libId;
    }
    public List<Integer> getLibId() {
        return libId;
    }


    public void setAddr(List<String> addr) {
        this.addr = addr;
    }
    public List<String> getAddr() {
        return addr;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }
    public String getIsDel() {
        return isDel;
    }

    public void setAgeGroup(boolean ageGroup) {
        this.ageGroup = ageGroup;
    }
    public boolean getAgeGroup() {
        return ageGroup;
    }

    public void setSexGroup(boolean sexGroup) {
        this.sexGroup = sexGroup;
    }
    public boolean getSexGroup() {
        return sexGroup;
    }

    public void setFlagGroup(boolean flagGroup) {
        this.flagGroup = flagGroup;
    }
    public boolean getFlagGroup() {
        return flagGroup;
    }

    public void setTimeGroup(boolean timeGroup) {
        this.timeGroup = timeGroup;
    }
    public boolean getTimeGroup() {
        return timeGroup;
    }
}
