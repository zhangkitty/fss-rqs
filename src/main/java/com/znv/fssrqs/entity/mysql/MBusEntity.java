package com.znv.fssrqs.entity.mysql;

import lombok.Data;

@Data
public class MBusEntity {
    private String DeviceID;
    private String DeviceName;
    private String IP;
    private String PrivateIP;
    private Integer Port;
    private Integer LoginState;
}
