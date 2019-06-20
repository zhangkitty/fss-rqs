package com.znv.fssrqs.util;

/**
 * Created by dongzelong on  2019/6/17 17:34.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HardInfo {
    private String cpuSerinumber;
    private String baseBorderSerinumber;
    private String netSerinumber;

    public HardInfo() {
    }

    public String getCpuSerinumber() {
        return this.cpuSerinumber;
    }

    public void setCpuSerinumber(String cpuSerinumber) {
        this.cpuSerinumber = cpuSerinumber;
    }

    public String getBaseBorderSerinumber() {
        return this.baseBorderSerinumber;
    }

    public void setBaseBorderSerinumber(String baseBorderSerinumber) {
        this.baseBorderSerinumber = baseBorderSerinumber;
    }

    public String getNetSerinumber() {
        return this.netSerinumber;
    }

    public void setNetSerinumber(String netSerinumber) {
        this.netSerinumber = netSerinumber;
    }
}
