package com.znv.fssrqs.service.face.search.one.n.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zhangcaochao
 * @Description 精确搜素的入参
 * @Date 2019.6.18 上午11:31
 */

@Data
public class CommonSearchParams {

    @JSONField(name = "size")
    private Integer PageSize;

    private Integer from;

    @JSONField(name = "camera_id")
    private String[] DeviceIDs;

    @JSONField(name = "enter_time_start")
    private String StartTime;

    @JSONField(name = "enter_time_end")
    private String EndTime;

    @JSONField(name = "sortField")
    private String SortField;

    @JSONField(name = "sortOrder")
    private String SortOrder;

    @JSONField(name = "age_start")
    private Integer AgeLowerLimit;

    @JSONField(name = "age_end")
    private Integer AgeUpLimit;

    @JSONField(name = "glass")
    private Integer Glass;

    @JSONField(name = "mask")
    private Integer Respirator;

    @JSONField(name = "race")
    private Integer SkinColor;

    @JSONField(name = "beard")
    private Integer Mustache;

    @JSONField(name = "emotion")
    private Integer Emotion;

    @JSONField(name = "eye_open")
    private Integer EyeOpen;

    @JSONField(name = "mouth_open")
    private Integer MouthOpen;

    @JSONField(name = "gender")
    private Integer GenderType;

    @JSONField(name = "sim_threshold")
    private Integer SimilarityDegree;

    @JSONField(name = "is_calcSim")
    private Boolean IsCalcSim;

    @JSONField(name = "feature_value")
    private String[] FeatureValue;

    @JSONField(name = "filter_type")
    private String FilterType;

    private String feature_name = "rt_feature.feature_high";
}

