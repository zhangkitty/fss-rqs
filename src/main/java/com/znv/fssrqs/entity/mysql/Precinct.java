package com.znv.fssrqs.entity.mysql;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by dongzelong on  2019/8/1 11:57.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class Precinct extends JSONObject {
    private static final long serialVersionUID = 1L;
    private String precinctId;

    private String precinctName;

    private String areaCode;

    private String upPrecinctId;

    public String getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(String precinctId) {
        this.precinctId = precinctId;
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getUpPrecinctId() {
        return upPrecinctId;
    }

    public void setUpPrecinctId(String upPrecinctId) {
        this.upPrecinctId = upPrecinctId;
    }
}
