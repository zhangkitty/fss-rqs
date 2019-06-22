package com.znv.fssrqs.param.face.search.one.n;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.21 下午11:18
 */

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ExactSearchResultParams {

    @NotBlank(message = "EventID不能为空")
    private String EventID;

    @NotNull(message = "CurrentPage不能为空")
    private Integer CurrentPage;

    @NotNull(message = "PageSize不能为空")
    private Integer PageSize;

    private String SortField;

    private String SortOrder;

}
