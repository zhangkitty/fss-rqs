package com.znv.fssrqs.controller.face.compare.n.n;

import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:11
 */

@Data
public class QueryResultParams {

    private String taskId;

    private Integer from;

    private Integer size;

    private String remark;

    private String sim;
}
