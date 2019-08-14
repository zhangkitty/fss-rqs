package com.znv.fssrqs.entity.mysql;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:36
 */
@Data
public class FaceTaskEntity {

    private String taskId;

    private String faceAiUnitId;

    private String taskName;

    private String userId;

    private Date createTime;

    private String precinctId;

    private String cameraId;

    private String gpsx;

    private String gpsy;

    private String dyPolicyId;

    private String faceAiParamsText;

    private String realTaskId;

    private Integer simThreshold;

    private String rtspServer;

    private String mbusServer;

    private String fnmsServer;

    private String sdkaccessServer;

    private Integer taskType;

    private Integer accessType;
}
