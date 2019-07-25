package com.znv.fssrqs.param.face.search.one.one;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:17
 */

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class One2OneFaceCompareRestParams {

    @NotBlank(message = "图片不能为空")
    private String ImageOne;

    @NotBlank(message = "图片不能为空")
    private String ImageTwo;


}

