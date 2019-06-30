package com.znv.fssrqs.service.face.search.one.n.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import javax.jws.HandlerChain;
import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.18 下午2:25
 */
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CommonSearchResultDTO {

    @JSONField(name = "OfficeID")
    private String office_id;

    @JSONField(name = "LibID")
    private Integer lib_id;

    @JSONField(name = "PersonID")
    private String person_id;

    @JSONField(name = "Top")
    private Integer top;

    @JSONField(name = "ImgHeight")
    private Integer img_height;

    @JSONField(name = "LeftPos")
    private Integer left_pos;

    @JSONField(name = "ImgWidth")
    private Integer img_width;

    @JSONField(name = "Similarity")
    private Float similarity;

    @JSONField(name = "DeviceName")
    private String camera_name;

    @JSONField(name = "IsAlarm")
    private String is_alarm;

    @JSONField(name = "FaceDisAppearTime")
    private String leave_time;

    @JSONField(name = "EnterTime")
    private String enter_time;

    @JSONField(name = "DeviceID")
    private String camera_id;

    @JSONField(name = "OfficeName")
    private String office_name;

    @JSONField(name = "DeviceKind")
    private Integer camera_type;

    @JSONField(name = "UUID")
    private String uuid;

    @JSONField(name = "SmallPictureUrl")
    private String SmallPictureUrl;

    @JSONField(name = "BigPictureUrl")
    private String BigPictureUrl;

    @JSONField(name = "OpTime")
    private String op_time;

}
