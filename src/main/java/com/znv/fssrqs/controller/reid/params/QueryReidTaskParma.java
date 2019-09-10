package com.znv.fssrqs.controller.reid.params;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:39
 */
@Data
public class QueryReidTaskParma {

    @NotNull(message = "没有传分页参数")
    private Integer pageSize;

    @NotNull(message = "没有传分页参数")
    private Integer pageNum;
}
