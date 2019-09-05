package com.znv.fssrqs.controller.face.compare.n.n;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:11
 */

@Data
public class QueryResultParams {


    @NotBlank
    private String taskId;

    @NotNull
    private Integer from;

    @NotNull
    private Integer size;

    private String remark;

    @NotNull(message = "相似度不能为空")
    private String sim;
}
