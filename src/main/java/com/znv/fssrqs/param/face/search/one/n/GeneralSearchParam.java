package com.znv.fssrqs.param.face.search.one.n;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author zhangcaochao
 * @Description 1:N通用检索入参
 * @Date 2019.6.6 上午11:13
 */
@Data
public class GeneralSearchParam {

    @NotBlank(message = "设备ID不能为空")
    private String DeviceIDs;

    @NotBlank(message = "开始时间不能为空")
    private String BeginTime;

    @NotBlank(message = "结束时间不能为空")
    private String EndTime;

    @NotBlank(message = "页码不能为空")
    private String PageNum;

    @NotBlank(message = "每页记录数不能为空")
    private String PageSize;

    //图片特征值列表
    private String FeatureValue;

    //相似度:[0-1]
    @NotBlank(message = "相似度不能为空")
    private float Similaritydegree;

    //排序字段
    private String SortFeild;

    //排序方式asc-升序,desc-降序
    private String SortOrder;

    //年龄
    private String AgeLowerLimit;

    //年龄
    private String AgeUpLimit;

    //眼镜，0：没戴，1：普通眼镜，2：太阳镜
    private Integer Glass;

    //是否戴口罩，0：否，1：是
    private Integer Respirator;

    //肤色，0：黄种人，1：黑种人，2：白种人，3：维族人
    private Integer SkinColor;

    //胡子，0：无，1：有
    private Integer Mustache;

    //表情，0-9: 0:生气，1：平静，2：困惑，3：厌恶，4：高兴，5：悲伤，6：惊恐，7：惊喜，8：斜视，9：尖叫
    private Integer Emotion;

    // 睁眼，0：无，1：有
    private Integer EyeOpen;

    // 张嘴，0：无，1：有
    private Integer MouthOpen;

    //是否进行图片查询，true：进行图片查询，false：不进行图片查询
    private Boolean  IsCalcSim;

}
