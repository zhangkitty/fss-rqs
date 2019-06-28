package com.znv.fssrqs.service.personnel.management.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.17 上午10:28
 */

@Data
public class OnePersonListDTO {

    @JSONField(name="LibID")
    private String lib_id;

    @JSONField(name="PersonID")
    private String person_id;

    @JSONField(name="Name")
    private String person_name;

    @JSONField(name="Birth")
    private String birth;

    @JSONField(name="EthicCode")
    private String nation;

    @JSONField(name="NationalityCode")
    private String country;

    @JSONField(name="PositiveUrl")
    private String positive_url;

    @JSONField(name="NegativeUrl")
    private String negative_url;

    @JSONField(name="Addr")
    private String addr;

    @JSONField(name="Tel")
    private String tel;

    @JSONField(name="NatureResidence")
    private String nature_residence;

    @JSONField(name="RoomNumber")
    private String room_number;

    @JSONField(name="DoorOpen")
    private Integer door_open;

    @JSONField(name="GenderCode")
    private Integer sex;

    @JSONField(name="ImageName")
    private String image_name;

    @JSONField(name="Feature")
    private String feature;

    @JSONField(name="IDNumber")
    private String card_id;

    @JSONField(name="Flag")
    private Integer flag;

    @JSONField(name="Comment")
    private String comment;

    @JSONField(name="ControlStartTime")
    private String control_start_time;

    @JSONField(name="ControlEndTime")
    private String control_end_time;

    @JSONField(name="IsDel")
    private Integer is_del;

    @JSONField(name="CreateTime")
    private String create_time;

    @JSONField(name="ModifyTime")
    private String modify_time;

    @JSONField(name="CommunityId")
    private String community_id;

    @JSONField(name="CommunityName")
    private String community_name;

    @JSONField(name="ControlCommunityId")
    private String control_community_id;

    @JSONField(name="ControlPersonId")
    private String control_person_id;

    @JSONField(name="ControlEventId")
    private String control_event_id;

    @JSONField(name="ImageId")
    private String image_id;

    @JSONField(name="PersonlibType")
    private Integer personlib_type;

    @JSONField(name="ControlPoliceCategory")
    private String control_police_category;

    @JSONField(name="ControlPersonTel")
    private String control_person_tel;

    @JSONField(name="ControlPersonName")
    private String control_person_name;

    @JSONField(name="BelongPoliceStation")
    private String belong_police_station;

    @JSONField(name="CardType")
    private String card_type;

    @JSONField(name="Description")
    private String description;

    @JSONField(name="InfoKind")
    private String info_kind;

    @JSONField(name="SourceID")
    private String source_id;

    @JSONField(name="PersonAlarmLevel")
    private String alarm_level;

    @JSONField(name="Score")
    private String score;

    @JSONField(name="Sim")
    private String sim;

    @JSONField(name="ImageUrl")
    private String image_url;
}
