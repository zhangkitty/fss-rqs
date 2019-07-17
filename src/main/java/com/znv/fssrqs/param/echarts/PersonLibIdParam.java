package com.znv.fssrqs.param.echarts;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Data() 下午1:53
 */

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PersonLibIdParam {

    private ArrayList<Integer> LibID;
}
