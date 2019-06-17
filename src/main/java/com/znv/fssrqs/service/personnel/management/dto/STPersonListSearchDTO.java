package com.znv.fssrqs.service.personnel.management.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.17 上午10:00
 */

@Data
public class STPersonListSearchDTO {

    @JSONField(name = "start_time")
    private String StartTime;

    @JSONField(name = "end_time")
    private String EndTime;

    @JSONField(name = "lib_id")
    private String[] LibId;

    @JSONField(name="is_del")
    private String IsDel;

    private String ImgData;

    @JSONField(name = "sim_threshold")
    private String SimThreshold;

    @JSONField(name = "size")
    private String PageSize;

    @JSONField(name = "person_name")
    private String Name;

    @JSONField(name = "card_id")
    private String IDNumber;

    private String from;

    private Boolean lib_aggregation=true;

}
