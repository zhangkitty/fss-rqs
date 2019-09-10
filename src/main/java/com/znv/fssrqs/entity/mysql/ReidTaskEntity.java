package com.znv.fssrqs.entity.mysql;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:28
 */
@Data
public class ReidTaskEntity {

    private int taskId;

    private String taskName;

    private String reidUnitId;

    private String deviceId;

    private String deviceName;

    private String deviceSite;

    private Float gpsX;

    private Float gpsY;

    private String userId;

    private String url;

    private String reidParamsText;

    private Date createTime;

    private Date updateTime;
}
