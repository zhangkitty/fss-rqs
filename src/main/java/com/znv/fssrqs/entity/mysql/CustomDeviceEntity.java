package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:55
 */

@Data
public class CustomDeviceEntity {

    private String nodeId;

    private String nodeName;

    private Integer nodeKind;

    private String upNodeId;

    private Integer treeId;
}
