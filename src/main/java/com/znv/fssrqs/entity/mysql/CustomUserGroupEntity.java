package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:23
 */
@Data
public class CustomUserGroupEntity {

    private Integer treeId;

    private String treeName;

    private String treeDesc;

    private Integer defaultTree;

    private Integer systemTree;
}