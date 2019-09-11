package com.znv.fssrqs.entity.mysql;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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

    private String installSite;

    private Float X;

    private Float Y;

    private String userId;

    private String url;

    private String reidParamsText;

    private String createTime;

    private String updateTime;
}
