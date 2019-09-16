package com.znv.fssrqs.entity.mysql;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午10:28
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReidTaskEntity {

    private int taskId;

    private String taskName;

    private String reidUnitId;

    private String deviceId;

    private String userId;

    private String url;

    private String reidParamsText;

    private String createTime;

    private String updateTime;
}
