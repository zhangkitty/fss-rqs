package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:39
 */

@Data
public class ReidAnalysisUnitEntity {

    private  String  deviceId;

    private  String serviceIp;

    private  Integer httpPort;

    private  Integer loginState;

    private  String  kafkaBootstrap_servers;

    private  String  kafkaTopic;

}
