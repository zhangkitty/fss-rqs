package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.7.2 下午7:52
 */

@Data
public class MSubscribersEntity {

    private Integer id;

    private String url;

    private Data startTime;

    private Data endTime;

    private String cameraIds;
}
