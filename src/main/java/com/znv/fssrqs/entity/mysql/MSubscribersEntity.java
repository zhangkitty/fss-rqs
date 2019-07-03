package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.7.2 下午7:52
 */

@Data
public class MSubscribersEntity {

    private Integer subscriberId;

    private String alarmPushUrl;

    private Data pushStartTime;

    private Data pushEndTime;
}
