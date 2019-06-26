package com.znv.fssrqs.service.elasticsearch.history.alarm;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class HistoryAlarmHit {
    @JSONField(name = "Score")
    private double score;
    @JSONField(name = "Similarity")
    private double similarity;
    @JSONField(name = "PersonID")
    private String personId;
    @JSONField(name = "LibID")
    private int libId;
    @JSONField(name = "EnterTime")
    private String enterTime;
    @JSONField(name = "LeaveTime")
    private String leaveTime;
    @JSONField(name = "OpTime")
    private String opTime;
    @JSONField(name = "ImgUrl")
    private String imgUrl;
    @JSONField(name = "BigPictureUuid")
    private String bigPictureUuid;
    @JSONField(name = "CameraID")
    private String cameraId;
    @JSONField(name = "CameraName")
    private String cameraName;
    @JSONField(name = "OfficeID")
    private String officeId;
    @JSONField(name = "OfficeName")
    private String officeName;
    @JSONField(name = "PersonName")
    private String personName;
    @JSONField(name = "AlarmType")
    private int alarmType;
    @JSONField(name = "ControlEventID")
    private String controlEventId;
    @JSONField(name = "Birth")
    private String birth;
    @JSONField(name = "ImgWidth")
    private int imgWidth;
    @JSONField(name = "ImgHeight")
    private int imgHeight;
    @JSONField(name = "LeftPos")
    private int leftPos;
    @JSONField(name = "RightPos")
    private int rightPos;
    @JSONField(name = "Top")
    private int top;
    @JSONField(name = "Bottom")
    private int bottom;
    @JSONField(name = "Uuid")
    private String uuid;

    /**
     * 布控警种
     */
    @JSONField(name = "ControlPoliceCategory")
    private String controlPoliceCategory;
    /**
     * 布控警员警号
     */
    @JSONField(name = "ControlPersonID")
    private String controlPersonId;
    /**
     * 布控人员名称
     */
    @JSONField(name = "ControlPersonName")
    private String controlPersonName;
    /**
     * 所属辖区，布控人单位
     */
    @JSONField(name = "ControlCommunityID")
    private String controlCommunityId;
    /**
     * 布控人联系方式
     */
    @JSONField(name = "ControlPersonTel")
    private String controlPersonTel;
    /**
     * 布控开始时间
     */
    @JSONField(name = "ControlStartTime")
    private String controlStartTime;
    /**
     * 布控结束时间
     */
    @JSONField(name = "ControlEndTime")
    private String controlEndTime;
    /**
     * 所属派出所
     */
    @JSONField(name = "BelongPoliceStation")
    private String belongPoliceStation;

    /**
     * 布控原因
     */
    private String comment;
}
