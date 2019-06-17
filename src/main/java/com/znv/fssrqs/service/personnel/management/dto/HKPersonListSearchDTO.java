package com.znv.fssrqs.service.personnel.management.dto;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * @author zhangcaochao
 * @Description 查询海康人员列表的入参
 * @Date 2019.06.11 上午10:39
 */

@Data
public class HKPersonListSearchDTO {

    @JSONField(name = "picBase64")
    private String ImgData;

    @JSONField(name = "similarityMin")
    private String SimThreshold;

    private String similarityMax = "1.0";

    @JSONField(name = "pageNo")
    private String CurrentPage;

    @JSONField(name = "pageSize")
    private String PageSize;

    @JSONField(name = "humanName")
    private String Name;

    @JSONField(name="credentialsNum")
    private String IDNumber;

    private String sex = "-1";

    private String beginBirthDate;

    private String endBirthDate;

    private String credentialsType;
}
