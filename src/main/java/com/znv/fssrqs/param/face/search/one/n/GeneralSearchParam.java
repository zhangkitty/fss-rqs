package com.znv.fssrqs.param.face.search.one.n;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhangcaochao
 * @Description 1:N通用检索入参
 * @Date 2019.6.6 上午11:13
 */
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class GeneralSearchParam {

//    @NotNull (message = "设备ID不能为空")
    private String[] DeviceIDs;

    @NotBlank(message = "开始时间不能为空")
    private String StartTime;

    @NotBlank(message = "结束时间不能为空")
    private String EndTime;

    @NotNull(message = "页码不能为空")
    private Integer CurrentPage;

    @NotNull(message = "每页记录数不能为空")
    private Integer PageSize;

    private Float SimilarityDegree;

    //查询类型， 0： 无图检索，1：极速检索，2：精确检索
    private Integer QueryType;

    //图片特征值列表
    private String[] FeatureValue;

    //排序字段
    private String SortField;

    //排序方式asc-升序,desc-降序
    private String SortOrder;

    //支持传多张图片，“or”:按图片并集搜索，“and”：按图片交集进行搜索，不传此字段默认是并集
    private String FilterType;

    //是否进行图片查询，true：进行图片查询，false：不进行图片查询
    private Boolean  IsCalcSim;

    //年龄
    private String AgeLowerLimit;

    //年龄
    private String AgeUpLimit;

    //眼镜，0：没戴，1：普通眼镜，2：太阳镜
    private String Glass;

    //是否戴口罩，0：否，1：是
    private String Respirator;

    //肤色，0：黄种人，1：黑种人，2：白种人，3：维族人
    private String SkinColor;

    //胡子，0：无，1：有
    private String Mustache;

    //表情，0-9: 0:生气，1：平静，2：困惑，3：厌恶，4：高兴，5：悲伤，6：惊恐，7：惊喜，8：斜视，9：尖叫
    private String Emotion;

    // 睁眼，0：无，1：有
    private String EyeOpen;

    // 张嘴，0：无，1：有
    private String MouthOpen;

    //性别，0：未知的性别  1：男，2： 女， 9：未说明的性别
    private String GenderType;

    private String UUID;
}
