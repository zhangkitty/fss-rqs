package com.znv.fssrqs.service.face.search.one.n.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.jws.HandlerChain;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午2:25
 */
@Data
public class CommonSearchResultDTO {

    @JSONField(name = "OfficeID")
    private String office_id;

    @JSONField(name = "LibID")
    private String lib_id;

    @JSONField(name = "Top")
    private String top;

    @JSONField(name = "Similarity")
    private String similarity;

    @JSONField(name = "DeviceName")
    private String camera_name;

    @JSONField(name = "FaceDisAppearTime")
    private String leave_time;

    @JSONField(name = "EnterTime")
    private String enter_time;

    @JSONField(name = "DeviceID")
    private String camera_id;

    @JSONField(name = "OfficeName")
    private String office_name;

    @JSONField(name = "DeviceKind")
    private String camera_type;

    @JSONField(name = "field_6")
    private String uuid;

}
