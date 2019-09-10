package com.znv.fssrqs.controller.reid.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:49
 */

@Data
public class ReidTaskParam {

    @NotBlank(message = "taskName不能为空")
    String taskName;

    @NotBlank(message = "reidUnitId不能为空")
    String reidUnitId;

    @NotBlank(message = "deviceId不能为空")
    String deviceId;

    @NotBlank(message = "deviceName不能为空")
    String deviceName;

    @NotBlank(message = "deviceSite不能为空")
    String deviceSite;

    Float gpsX;

    Float gpsY;

    @NotBlank(message = "userId不能为空")
    String userId;

    @NotBlank(message = "url不能为空")
    String url;

    String reidParamsText;
}
