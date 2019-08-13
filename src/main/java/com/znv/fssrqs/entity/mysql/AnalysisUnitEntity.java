package com.znv.fssrqs.entity.mysql;

import lombok.Data;
import sun.rmi.server.InactiveGroupException;

@Data
public class AnalysisUnitEntity {
    private String DeviceID;
    private String DeviceName;
    private Integer DeviceType;
    private String IP;
    private Integer Port;
    private Integer LoginState;
}
