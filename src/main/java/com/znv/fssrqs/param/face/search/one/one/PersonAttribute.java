package com.znv.fssrqs.param.face.search.one.one;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:13
 */

@Data
public class PersonAttribute {

    @JSONField(name = "MouthOpen")
    private Integer mouthOpen;

    private Integer attractive;

    private Integer gender;

    private Integer race;

    private Integer beard;

    private Integer eyeglass;

    private Integer age;

    private Integer smile;

    private Integer mask;

    private Integer EyeOpen;
}
