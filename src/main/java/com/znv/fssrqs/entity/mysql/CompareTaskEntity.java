package com.znv.fssrqs.entity.mysql;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:26
 */

@Data
public class CompareTaskEntity {

    private String taskId;

    private String taskName;

    private Integer lib1;

    private Integer lib2;

    private Integer status;

    private String createUser;

    private String updateTime;

    private Integer remainningTime;

    private String errorMessage;

    private Float process;

    private Float sim;

    private String lib1Name;

    private String lib2Name;


}
