package com.znv.fssrqs.param.personnel.management;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.10 下午2:57
 */
@Data
public class PersonListSearchParams {


    @NotBlank(message = "开始时间不能为空")
    private String StartTime;

    @NotBlank(message = "结束时间不能为空")
    private String EndTime;

    private String[] LibId;

    private String IsDel;

    private String ImgData;

    private String SimThreshold;

    @NotBlank(message = "分页参数不能为空")
    private String From;

    @NotBlank(message = "分页参数不能为空")
    private String Size;

    private String Name;

    private String IDNumber;

    private String AlgorithmType;



}
