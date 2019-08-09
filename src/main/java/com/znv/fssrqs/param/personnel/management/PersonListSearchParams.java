package com.znv.fssrqs.param.personnel.management;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.6.10 下午2:57
 */
@Data
public class PersonListSearchParams {
    private String StartTime;

    private String EndTime;

    private String[] LibId;

    private String IsDel;

    private String ImgData;

    private Double SimThreshold;

    private Integer CurrentPage;

    private Integer PageSize;

    private String Name;

    private String IDNumber;

    private Integer[] AlgorithmType;
}
