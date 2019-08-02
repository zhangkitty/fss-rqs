package com.znv.fssrqs.controller.face.compare.n.n;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午5:31
 */

@Data
public class QueryTaskParams {

    private String taskId;

    private String createUser;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
